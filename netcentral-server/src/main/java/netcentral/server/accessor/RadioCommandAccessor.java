package netcentral.server.accessor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.RadioStyle;
import netcentral.server.object.Net;
import netcentral.server.object.NetMessage;
import netcentral.server.object.Participant;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.User;

@Singleton
public class RadioCommandAccessor {
    private static final Logger logger = LogManager.getLogger(RadioCommandAccessor.class);
    private static final String COMMAND_CHECK_IN = "CI";
    private static final String COMMAND_CHECK_OUT = "CO";
    private static final String COMMAND_TACTICAL_CALLSIGN = "T";
    private static final String COMMAND_NET_MESSAGE = "M";
    private static final String COMMAND_NET_MESSAGE_NET_CONTROL = "MC";
    private static final String COMMAND_REPLAY_NET_MESSAGES = "R";
    private static final String COMMAND_LIST_PARTICIPANTS = "L";
    private static final String COMMAND_PING_PARTICIPANT = "P";
    private static final String COMMAND_INFO = "I";
    private static final String COMMAND_HELP = "H";
    private static final String COMMAND_STATUS = "S";
    private static final String COMMAND_OPERATIONAL_STATUS = "O";
    private static final String COMMAND_VOICE_FREQUENCY = "D";
    private static final String COMMAND_ACK = "ack";
    private static final String COMMAND_REJ = "rej";
    private static final String COMMAND_TRANSMIT_POWER = "X";
    private static final String COMMAND_ELECTRICAL_POWER = "E";
    private static final String COMMAND_BACKUP_ELECTRICAL_POWER = "B";
    private static final String COMMAND_RADIO_STYLE = "Y";

    @Inject
    private NetAccessor netAccessor;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;
    @Inject
    private ParticipantAccessor participantAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;
    @Inject
    private TrackedStationAccessor trackedStationAccessor;
    @Inject
    private NetMessageAccessor netMessageAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;

    public void processMessage(User loggedInUser, APRSMessage message, String transceiverSourceId) {
        try {
            if ((message == null) || (message.getCallsignFrom() == null) || (message.getCallsignFrom() == null) || (message.getMessage() == null)) {
                logger.error("Message data incomplete");
                return;
            }
            Net net = netAccessor.getByCallsign(null, message.getCallsignTo());
            if (net != null) {
                processMessage(loggedInUser, message, net, transceiverSourceId);
            } else {
                // do nothing - not our message
            }
        } catch (Exception e) {
            logger.error("Exception caught processing radio message", e);
        }
    }

    private boolean isNetParticipant(User loggedInUser, APRSMessage message, Net net) {
        boolean ret = false;
        Participant participant = new Participant();
        participant.setCallsign(message.getCallsignFrom());

        List<Net> nets = netParticipantAccessor.getAllNets(loggedInUser, participant);
        if ((nets != null) && (!nets.isEmpty())) {
            // check if in this net
            for (Net netEntry : nets) {
                if (netEntry.getCallsign().equalsIgnoreCase(net.getCallsign())) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

    private void ackOrRej(User loggedInUser, APRSMessage msg, String transceiverSourceId, boolean isParticipant) {
        try {
            if (msg.getMessageNumber() != null) {
                if (isParticipant) {
                    ackMessage(loggedInUser, msg, transceiverSourceId);
                } else {
                    rejMessage(loggedInUser, msg, transceiverSourceId);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught during ack or rej", e);
        }
    }

    private void processMessage(User loggedInUser, APRSMessage msg, Net net, String transceiverSourceId) {
        // message is for this net - act and respond
        boolean ackOrRejPerformed = false;
        String message = preStrip(msg.getMessage());
        boolean isParticipant = isNetParticipant(loggedInUser, msg, net);

        if ((message == null) || (message.isEmpty()) || (message.isBlank())) {
            return;
        }

        String [] words = message.split(" ");
        int index = 0;
        while (index < words.length) {
            String command = words[index];
            if (COMMAND_CHECK_IN.equalsIgnoreCase(command)) {
                if (msg.getMessageNumber() != null) {
                    ackMessage(loggedInUser, msg, transceiverSourceId);
                    ackOrRejPerformed = true;
                }
                processCheckIn(loggedInUser, msg, net, transceiverSourceId);
                isParticipant = true;
                index++;
            } else if (COMMAND_CHECK_OUT.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                processCheckOut(loggedInUser, msg, net, transceiverSourceId);
                isParticipant = false;
                index++;
            } else if (COMMAND_NET_MESSAGE.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                processNetMessage(loggedInUser, msg, net, transceiverSourceId, false);
                // cannot have multiple
                break;
            } else if (COMMAND_NET_MESSAGE_NET_CONTROL.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                processNetMessage(loggedInUser, msg, net, transceiverSourceId, true);
                // cannot have multiple
                break;
            } else if (COMMAND_REPLAY_NET_MESSAGES.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                processReplayMessages(loggedInUser, msg, net, transceiverSourceId);
                // cannot have multiple
                break;
            } else if (COMMAND_LIST_PARTICIPANTS.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                processListParticipants(loggedInUser, msg, net, transceiverSourceId);
                // cannot have multiple
                break;
            } else if (COMMAND_HELP.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                processHelp(loggedInUser, msg, net, transceiverSourceId);
                // cannot have multiple
                break;
            } else if (COMMAND_STATUS.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                processStatus(loggedInUser, msg, net, transceiverSourceId);
                // cannot have multiple
                break;
            } else if (COMMAND_OPERATIONAL_STATUS.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                processOperationalStatus(loggedInUser, msg, net, transceiverSourceId);
                // cannot have multiple
                break;
            } else if (COMMAND_VOICE_FREQUENCY.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                if (index+1 < words.length) {
                    String value = words[index+1];
                    processVoiceFrequency(loggedInUser, msg, net, transceiverSourceId, value);
                    index += 2;
                } else {
                    processBadCommand(loggedInUser, msg, net, transceiverSourceId);
                    break;
                }
            } else if (COMMAND_TRANSMIT_POWER.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                if (index+1 < words.length) {
                    String value = words[index+1];
                    processTransmitPower(loggedInUser, msg, net, transceiverSourceId, value);
                    index += 2;
                } else {
                    processBadCommand(loggedInUser, msg, net, transceiverSourceId);
                    break;
                }
            } else if (COMMAND_ELECTRICAL_POWER.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                if (index+1 < words.length) {
                    String value = words[index+1];
                    processElectricalPowerType(loggedInUser, msg, net, transceiverSourceId, value);
                    index += 2;
                } else {
                    processBadCommand(loggedInUser, msg, net, transceiverSourceId);
                    break;
                }
            } else if (COMMAND_BACKUP_ELECTRICAL_POWER.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                if (index+1 < words.length) {
                    String value = words[index+1];
                    processBackupElectricalPowerType(loggedInUser, msg, net, transceiverSourceId, value);
                    index += 2;
                } else {
                    processBadCommand(loggedInUser, msg, net, transceiverSourceId);
                    break;
                }
            } else if (COMMAND_RADIO_STYLE.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                if (index+1 < words.length) {
                    String value = words[index+1];
                    processRadioStyle(loggedInUser, msg, net, transceiverSourceId, value);
                    index += 2;
                } else {
                    processBadCommand(loggedInUser, msg, net, transceiverSourceId);
                    break;
                }
            } else if (COMMAND_PING_PARTICIPANT.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                processPingParticipant(loggedInUser, msg, net, transceiverSourceId);
                // cannot have multiple
                break;
            } else if (COMMAND_INFO.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                processInfo(loggedInUser, msg, net, transceiverSourceId);
                // cannot have multiple
                break;
            } else if (COMMAND_TACTICAL_CALLSIGN.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                if (index+1 < words.length) {
                    String value = words[index+1];
                    processTacticalCallsign(loggedInUser, msg, net, transceiverSourceId, value);
                    index += 2;
                } else {
                    processBadCommand(loggedInUser, msg, net, transceiverSourceId);
                    break;
                }
            } else if (command.startsWith(COMMAND_ACK)) {
                // do nothing - response to us
                break;
            } else if (command.startsWith(COMMAND_REJ)) {
                // do nothing - response to us
                break;
            } else {
                if (!ackOrRejPerformed) {
                    ackOrRej(loggedInUser, msg, transceiverSourceId, isParticipant);
                    ackOrRejPerformed = true;
                    if (!isParticipant) {
                        break;
                    }
                }
                processBadCommand(loggedInUser, msg, net, transceiverSourceId);
                break;
            }
        }
    }

    private String preStrip(String message) {
        // some devices put space at the beginning of a message :/ 
        String ret = message;

        while ((ret.length() > 0) && (ret.startsWith(" "))) {
            ret = ret.substring(1);
        }

        return ret;
    }

    private void processTacticalCallsign(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId, String value) {
        Participant participant = participantAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        if (participant == null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "You are not in this net");
            return;
        }

        participant.setTacticalCallsign(value);
        netParticipantAccessor.updateParticipant(loggedInUser, net, participant);
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), 
                                                    String.format("Tactical call sign updated to %s", participant.getTacticalCallsign()));
    }

    private void processRadioStyle(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId, String value) {
        Participant participant = participantAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        if (participant == null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "You are not in this net");
            return;
        }
        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        if (trackedStation == null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "Cannot find station");
            return;
        }

        RadioStyle valueEnum = RadioStyle.UNKNOWN;
        if (RadioStyle.APPLIANCE.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = RadioStyle.APPLIANCE;
        } else if (RadioStyle.BASE.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = RadioStyle.BASE;
        } else if (RadioStyle.HANDHELD.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = RadioStyle.HANDHELD;
        } else if (RadioStyle.INTERNET.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = RadioStyle.INTERNET;
        } else if (RadioStyle.MOBILE.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = RadioStyle.MOBILE;
        } else if (RadioStyle.OTHER.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = RadioStyle.OTHER;
        }

        trackedStation.setRadioStyle(valueEnum);
        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), 
                                                    String.format("Radio style updated to %s", trackedStation.getRadioStyle()));
    }

    private void processElectricalPowerType(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId, String value) {
        Participant participant = participantAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        if (participant == null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "You are not in this net");
            return;
        }
        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        if (trackedStation == null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "Cannot find station");
            return;
        }

        ElectricalPowerType valueEnum = ElectricalPowerType.UNKNOWN;
        if (ElectricalPowerType.BATTERY.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = ElectricalPowerType.BATTERY;
        } else if (ElectricalPowerType.COMMERCIAL.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = ElectricalPowerType.COMMERCIAL;
        } else if (ElectricalPowerType.GENERATOR.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = ElectricalPowerType.GENERATOR;
        } else if (ElectricalPowerType.NONE.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = ElectricalPowerType.NONE;
        } else if (ElectricalPowerType.SOLAR.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = ElectricalPowerType.SOLAR;
        }
        trackedStation.setElectricalPowerType(valueEnum);
        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), 
                                                    String.format("Electrical power updated to %s", trackedStation.getElectricalPowerType()));
    }

    private void processBackupElectricalPowerType(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId, String value) {
        Participant participant = participantAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        if (participant == null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "You are not in this net");
            return;
        }
        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        if (trackedStation == null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "Cannot find station");
            return;
        }

        ElectricalPowerType valueEnum = ElectricalPowerType.UNKNOWN;
        if (ElectricalPowerType.BATTERY.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = ElectricalPowerType.BATTERY;
        } else if (ElectricalPowerType.COMMERCIAL.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = ElectricalPowerType.COMMERCIAL;
        } else if (ElectricalPowerType.GENERATOR.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = ElectricalPowerType.GENERATOR;
        } else if (ElectricalPowerType.NONE.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = ElectricalPowerType.NONE;
        } else if (ElectricalPowerType.SOLAR.toString().toUpperCase().startsWith(value.toUpperCase())) {
            valueEnum = ElectricalPowerType.SOLAR;
        }
        trackedStation.setBackupElectricalPowerType(valueEnum);
        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), 
                                                    String.format("Backup electrical power updated to %s", trackedStation.getBackupElectricalPowerType()));
    }

    private void processTransmitPower(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId, String value) {
        Participant participant = participantAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        if (participant == null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "You are not in this net");
            return;
        }
        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        if (trackedStation == null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "Cannot find station");
            return;
        }

        trackedStation.setTransmitPower(Integer.parseInt(value));
        trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), 
                                                    String.format("Transmit power updated to %d", trackedStation.getTransmitPower()));
    }

    private void processInfo(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId) {
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), net.getName()+"/vf:"+net.getVoiceFrequency()+"/"+net.getDescription());
    }

    private void processBadCommand(User loggedInUser, APRSMessage msg, Net net, String transceiverSourceId) {
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), msg.getCallsignFrom(), "Bad command.  Send H for help.");
    }

    private void processPingParticipant(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId) {
        String messageString = message.getMessage();
        String callsign = messageString.substring(2); // go past "p "
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), callsign, "You were pinged by "+message.getCallsignFrom() );
    }

    private void processVoiceFrequency(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId, String value) {
        Participant participant = participantAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        if (participant == null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "You are not in this net");
            return;
        }
        String freq = value;
        participant.setVoiceFrequency(freq);
        participantAccessor.update(loggedInUser, participant.getCallsign(), participant);
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "Voice frequency updated to "+freq);
    }

    private void processOperationalStatus(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId) {
        String messageString = message.getMessage();
        String callsign = messageString.substring(2); // go past "o "
        TrackedStation trackedStation = null;

        try {
            trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);
        } catch (Exception e) {
            trackedStation = null;
        }

        if (trackedStation != null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), 
                                            String.format("Operational status of %s is %s", callsign, trackedStation.getStatus().toString()));
        } else {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), 
                                            String.format("Operational status of %s is unknown", callsign));
        }
    }

    private void processStatus(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId) {
        Participant participant = participantAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        if (participant == null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "You are not in a net");
            return;
        }
        String messageString = message.getMessage();
        String status = messageString.substring(2); // go past "s "
        participant.setStatus(status);
        participantAccessor.update(loggedInUser, participant.getCallsign(), participant);
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "Status updated to "+status);
    }

    private void processHelp(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId) {
        List<String> helpMessages = new ArrayList<>();
        helpMessages.add("Net Central supports the following commands");
        helpMessages.add(String.format("%s - %s", COMMAND_CHECK_IN, "Check in"));
        helpMessages.add(String.format("%s - %s", COMMAND_CHECK_OUT, "Check out"));
        helpMessages.add(String.format("%s X - %s", COMMAND_TRANSMIT_POWER, "Report transmit power X in watts"));
        helpMessages.add(String.format("%s X - %s", COMMAND_ELECTRICAL_POWER, "Report electrical (BATTERY,COMMERCIAL,GENERATOR,NONE,SOLAR)"));
        helpMessages.add(String.format("%s X - %s", COMMAND_BACKUP_ELECTRICAL_POWER, "Report bkup electr (BATTERY,COMMERCIAL,GENERATOR,NONE,SOLAR)"));
        helpMessages.add(String.format("%s X - %s", COMMAND_RADIO_STYLE, "Report radio (APPLIANCE,BASE,HANDHELD,INTERNET,MOBILE,OTHER)"));
        helpMessages.add(String.format("%s X - %s", COMMAND_TACTICAL_CALLSIGN, "Set tactical call sign"));
        helpMessages.add(String.format("%s - %s", COMMAND_INFO, "Send net info"));
        helpMessages.add(String.format("%s - %s", COMMAND_NET_MESSAGE, "Send message to all net participants"));
        helpMessages.add(String.format("%s - %s", COMMAND_NET_MESSAGE_NET_CONTROL, "Send message to only net control"));
        helpMessages.add(String.format("%s X - %s", COMMAND_REPLAY_NET_MESSAGES, "Resend last X messages sent to net to you"));
        helpMessages.add(String.format("%s - %s", COMMAND_LIST_PARTICIPANTS, "List net participants"));
        helpMessages.add(String.format("%s CALLSIGN - %s", COMMAND_PING_PARTICIPANT, "Ping net participant"));
        helpMessages.add(String.format("%s - %s", COMMAND_HELP, "Help"));
        helpMessages.add(String.format("%s - %s", COMMAND_STATUS, "Report your status for others to see"));
        helpMessages.add(String.format("%s CALLSIGN - %s", COMMAND_OPERATIONAL_STATUS, "Check operational status of callsign"));
        helpMessages.add(String.format("%s - %s", COMMAND_VOICE_FREQUENCY, "Report your voice frequency for others to see"));
        transceiverMessageAccessor.sendMessages(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), helpMessages);
    }

    private void processListParticipants(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId) {
        List<String> participantMessages = new ArrayList<>();
        List<Participant> participants = netParticipantAccessor.getAllParticipants(loggedInUser, net);
        if ((participants == null) || (participants.isEmpty())) {
            participantMessages.add("There are currently no participants in net "+net.getCallsign());  // doubtful to reach because someone is here asking
        } else {
            participantMessages.add(String.format("There are %d participant%s in net %s", participants.size(), 
                                                            (participants.size() == 1) ? "" : "s", net.getCallsign()));  // doubtful to reach because someone is here asking
            for (Participant participant1 : participants) {
                Participant participant = participantAccessor.get(loggedInUser, participant1.getCallsign());  // get current participant info
                participantMessages.add(String.format("%s (%s) - v:%s - %s", participant.getCallsign(), 
                                                    (participant.getTacticalCallsign() == null) ? "" : participant.getTacticalCallsign(),
                                                    participant.getVoiceFrequency(), participant.getStatus()));
            }
        }
        transceiverMessageAccessor.sendMessages(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), participantMessages);
    }

    private void processReplayMessages(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId) {
        String messageString = message.getMessage();
        Integer count = 1;
        if (messageString.length() < 3) {
            try {
                count = Integer.valueOf(messageString.substring(2)); // go past "r "
            } catch (Exception e) {
                count = 1;
            }
        }

        List<NetMessage> netMessages;

        try {
            netMessages = netMessageAccessor.getAll(loggedInUser, null, net.getCompletedNetId());
        } catch (Exception e) {
            netMessages = null;
        }

        if (netMessages != null) {
            List<String> replayMessages = new ArrayList<>();
            for (NetMessage netMessage : netMessages) {
                if ((!netMessage.getMessage().startsWith("ack")) && (!netMessage.getMessage().startsWith("rej")) && (!netMessage.getRecipient().equals(NetMessage.RECIPIENT_NET_CONTROL))) {
                    // not ack's / rej's and from the net, not to the net control
                    replayMessages.add(netMessage.getMessage());
                }
            }
            int total = replayMessages.size();
            while (total > count) {
                replayMessages.remove(0);
                total--;
            }

            transceiverMessageAccessor.sendMessages(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), replayMessages);
        } else {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "No messages");
        }
    }

    private void processNetMessage(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId, boolean isNetControl) {
        String operatorMessage = message.getMessage().substring(2); // go past "m "

        // persist the net message
        NetMessage netMessage = new NetMessage();
        netMessage.setCallsignFrom(message.getCallsignFrom());
        netMessage.setCompletedNetId(net.getCompletedNetId());
        netMessage.setId(UUID.randomUUID().toString());
        netMessage.setMessage(operatorMessage);
        netMessage.setReceivedTime(ZonedDateTime.now());
        if (isNetControl) {
            netMessage.setRecipient(NetMessage.RECIPIENT_NET_CONTROL);
        } else {
            netMessage.setRecipient(NetMessage.RECIPIENT_ENTIRE_NET);
        }
        netMessage = netMessageAccessor.create(loggedInUser, netMessage);

        // send it to all participants
        if (!isNetControl) {
            List<Participant> participants = netParticipantAccessor.getAllParticipants(loggedInUser, net);
            if ((participants != null) && (!participants.isEmpty())) {
                for (Participant participant : participants) {
                    transceiverMessageAccessor.sendMessage(loggedInUser, net.getCallsign(), participant.getCallsign(), netMessage.getMessage());
                }
            }
        }
    }

    private void processCheckOut(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId) {
        Participant participant = new Participant();
        participant.setCallsign(message.getCallsignFrom());

        List<Net> nets = netParticipantAccessor.getAllNets(loggedInUser, participant);
        if ((nets != null) && (!nets.isEmpty())) {
            // check if in this net
            for (Net netEntry : nets) {
                if (netEntry.getCallsign().equalsIgnoreCase(net.getCallsign())) {
                    transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "Checked out of net "+net.getCallsign());
                    netParticipantAccessor.removeParticipant(loggedInUser, net, participant);
                    return;
                }
            }
        }
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "You are not in net "+net.getCallsign());
    }

    private void processCheckIn(User loggedInUser, APRSMessage message, Net net, String transceiverSourceId) {
        Participant participant = new Participant();
        participant.setCallsign(message.getCallsignFrom());

        List<Net> nets = netParticipantAccessor.getAllNets(loggedInUser, participant);
        if ((nets != null) && (!nets.isEmpty())) {
            // check if in this net
            for (Net netEntry : nets) {
                if (netEntry.getCallsign().equalsIgnoreCase(net.getCallsign())) {
                    // already in this net - say so
                    transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "You are already in net "+net.getCallsign());
                    return;
                }
            }
        }

        try {
            participant = participantAccessor.getByCallsign(loggedInUser, message.getCallsignFrom());
        } catch (Exception e) {
            participant = null;
        }
        if (participant == null) {
            participant = new Participant();
            participant.setCallsign(message.getCallsignFrom());
            participant = participantAccessor.create(loggedInUser, participant);
        }
        netParticipantAccessor.addParticipant(loggedInUser, net, participant);
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), "You checked into net "+net.getCallsign()+". Help - send H");
        if (net.getCheckinMessage() != null) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, net.getCallsign(), message.getCallsignFrom(), net.getCheckinMessage());
        }
    }

    private void ackMessage(User loggedInUser, APRSMessage msg, String transceiverSourceId) {
        statisticsAccessor.incrementAcksRequested();
        statisticsAccessor.incrementAcksSent();
        transceiverMessageAccessor.sendAckMessage(loggedInUser, transceiverSourceId, msg.getCallsignTo(), msg.getCallsignFrom(), "ack"+msg.getMessageNumber());
    }
    private void rejMessage(User loggedInUser, APRSMessage msg, String transceiverSourceId) {
        statisticsAccessor.incrementAcksRequested();
        statisticsAccessor.incrementRejsSent();
        transceiverMessageAccessor.sendAckMessage(loggedInUser, transceiverSourceId, msg.getCallsignTo(), msg.getCallsignFrom(), "rej"+msg.getMessageNumber());
    }
}
