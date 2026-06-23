package netcentral.server.accessor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.constants.APRSQueryType;
import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.common.constants.APRSConstants;
import com.kc1vmz.netcentral.common.constants.NetCentralQueryType;
import com.kc1vmz.netcentral.common.constants.NetCentralRadioCommands;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.NetCentralServerConfig;
import netcentral.server.enums.ObjectSymbolTableConstants;
import netcentral.server.object.ExpectedParticipant;
import netcentral.server.object.Net;
import netcentral.server.object.User;

@Singleton
public class NetManagerObjectCommandAccessor {
    private static final Logger logger = LogManager.getLogger(NetManagerObjectCommandAccessor.class);

    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;
    @Inject
    private FederatedObjectReporterAccessor federatedObjectReporterAccessor;
    @Inject
    private NetAccessor netAccessor;
    @Inject
    private NetCentralServerConfig netCentralServerConfig;
    @Inject
    private CompletedNetAccessor completedNetAccessor ;
    @Inject
    private NetExpectedParticipantAccessor netExpectedParticipantAccessor;

    public void processMessage(User loggedInUser, APRSObject netManagerObject, APRSMessage msg, String transceiverSourceId) {
        // message is for this general resource object - act and respond
        boolean ackOrRejPerformed = false;
        String message = preStrip(msg.getMessage());

        if ((message == null) || (message.isEmpty()) || (message.isBlank())) {
            return;
        }

        if (message.startsWith(APRSConstants.ACK)) {
            return;
        }

        if (netManagerObject.getCallsignTo().equalsIgnoreCase(msg.getCallsignFrom())) {
            // the message is from inside the house
            return;
        }

        // is this a directed query
        if (message.startsWith(APRSQueryType.PREFIX)) {
            processDirectedStatusQuery(loggedInUser, msg, transceiverSourceId, netManagerObject);
            return;
        }

        String [] words = message.split(" ");
        int index = 0;
        while (index < words.length) {
            String command = words[index];
            if (NetCentralRadioCommands.COMMAND_NETMGR_LIST.equalsIgnoreCase(command)) {
                if (msg.getMessageNumber() != null) {
                    ackMessage(loggedInUser, msg, transceiverSourceId);
                    ackOrRejPerformed = true;
                }
                processNetList(loggedInUser, netManagerObject, msg, transceiverSourceId);
                // cannot have multiple
                break;
            } else if (NetCentralRadioCommands.COMMAND_NETMGR_CREATE.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackMessage(loggedInUser, msg, transceiverSourceId);
                    ackOrRejPerformed = true;
                }
                if (index+1 < words.length) {
                    String value = words[index+1];
                    processNetCreate(loggedInUser, netManagerObject, msg, transceiverSourceId, value, false);
                    index += 2;
                } else {
                    processBadCommand(loggedInUser, netManagerObject, msg, transceiverSourceId);
                    break;
                }
                // cannot have multiple
                break;
            } else if (NetCentralRadioCommands.COMMAND_NETMGR_CREATE_PRIVATE.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackMessage(loggedInUser, msg, transceiverSourceId);
                    ackOrRejPerformed = true;
                }
                if (index+1 < words.length) {
                    String value = words[index+1];
                    processNetCreate(loggedInUser, netManagerObject, msg, transceiverSourceId, value, true);
                    index += 2;
                } else {
                    processBadCommand(loggedInUser, netManagerObject, msg, transceiverSourceId);
                    break;
                }
                // cannot have multiple
                break;
            } else if (NetCentralRadioCommands.COMMAND_NETMGR_SECURE.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackMessage(loggedInUser, msg, transceiverSourceId);
                    ackOrRejPerformed = true;
                }
                if (index+1 < words.length) {
                    String value = words[index+1];
                    processNetSecure(loggedInUser, netManagerObject, msg, transceiverSourceId, value);
                    index += 2;
                } else {
                    processBadCommand(loggedInUser, netManagerObject, msg, transceiverSourceId);
                    break;
                }
                // cannot have multiple
                break;
            } else if (NetCentralRadioCommands.COMMAND_NETMGR_HELP.equalsIgnoreCase(command)) {
                if (!ackOrRejPerformed) {
                    ackMessage(loggedInUser, msg, transceiverSourceId);
                    ackOrRejPerformed = true;
                }
                processNetHelp(loggedInUser, netManagerObject, msg, transceiverSourceId);
                // cannot have multiple
                break;
            } else if (command.startsWith(APRSConstants.ACK)) {
                // do nothing - response to us
                break;
            } else if (command.startsWith(APRSConstants.REJ)) {
                // do nothing - response to us
                break;
            } else {
                if (!ackOrRejPerformed) {
                    ackMessage(loggedInUser, msg, transceiverSourceId);
                    ackOrRejPerformed = true;
                }
                processBadCommand(loggedInUser, netManagerObject, msg, transceiverSourceId);
                break;
            }
        }
    }

    private void processNetHelp(User loggedInUser, APRSObject netManagerObject, APRSMessage msg, String transceiverSourceId) {
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), "Net Central Net Manager commands:");
        List<String> helpMessages = new ArrayList<>();
        if (netCentralServerConfig.getVerboseHelp()) {
            // send each command as a message
            helpMessages.add("Net Central Net Manager commands (verbose)");
            helpMessages.add(String.format("%s - %s", NetCentralRadioCommands.COMMAND_NETMGR_LIST, "List active nets"));
            helpMessages.add(String.format("%s NET_CALLSIGN - %s", NetCentralRadioCommands.COMMAND_NETMGR_CREATE, "Create active net"));
            helpMessages.add(String.format("%s NET_CALLSIGN - %s", NetCentralRadioCommands.COMMAND_NETMGR_CREATE_PRIVATE, "Create private active net"));
            helpMessages.add(String.format("%s NET_CALLSIGN - %s", NetCentralRadioCommands.COMMAND_NETMGR_SECURE, "Secure active net"));
            helpMessages.add(String.format("%s - %s", NetCentralRadioCommands.COMMAND_NETMGR_HELP, "Help"));
        } else {
            // send just the commands not the descriptions
            helpMessages.add("Net Central Net Manager commands");
            helpMessages.add(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", 
                                                        NetCentralRadioCommands.COMMAND_NETMGR_LIST, NetCentralRadioCommands.COMMAND_NETMGR_CREATE,
                                                        NetCentralRadioCommands.COMMAND_NETMGR_CREATE_PRIVATE, NetCentralRadioCommands.COMMAND_NETMGR_SECURE,
                                                        NetCentralRadioCommands.COMMAND_NETMGR_HELP));
        }
        transceiverMessageAccessor.sendMessages(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), helpMessages);
    }

    private void processNetSecure(User loggedInUser, APRSObject netManagerObject, APRSMessage msg, String transceiverSourceId, String net_callsign) {
        if ((net_callsign == null) || (net_callsign.isEmpty())) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), "Net callsign not provided");
            return;
        }

        try {
            Net net = netAccessor.get(loggedInUser, net_callsign);
            if (net != null) {
                if (msg.getCallsignFrom().equalsIgnoreCase(net.getCreatorName())) {
                    completedNetAccessor.create(loggedInUser, net);
                    netAccessor.delete(loggedInUser, net.getCallsign());
                    transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), "Net secured");
                } else {
                    // only creator can close net via Net Manager
                    transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), "Only creator can secure ad-hoc net");
                }
            } else {
                transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), "Net not found");
            }
            return;
        } catch (Exception e) {
            logger.error("Exception caught securing ad-hoc net", e);
        }
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), "Could not secure net");
    }

    private void processNetCreate(User loggedInUser, APRSObject netManagerObject, APRSMessage msg, String transceiverSourceId, String net_callsign, boolean isPrivate) {
        if ((net_callsign == null) || (net_callsign.isEmpty())) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), "Net callsign not provided");
            return;
        }

        String completedNetId = UUID.randomUUID().toString();
        Net net = new Net(net_callsign, net_callsign, "Ad-hoc APRS net on Net Central", "", ZonedDateTime.now(), completedNetId, 
                            netManagerObject.getLat(), netManagerObject.getLon(), (!isPrivate), msg.getCallsignFrom(),
                            true, "", (!isPrivate), true, 
                            false, ObjectSymbolTableConstants.DEFAULT_SYMBOL_TABLE_ID, ObjectSymbolTableConstants.DEFAULT_SYMBOL_TABLE_CODE);

        try {
            Net netNew = netAccessor.create(loggedInUser, net);
            if (netNew != null) {
                if (isPrivate) {
                    // need to add creator to expected participant list
                    ExpectedParticipant expectedParticipant = new ExpectedParticipant(getCallsignRoot(msg.getCallsignFrom()));
                    netExpectedParticipantAccessor.addExpectedParticipant(loggedInUser, net, expectedParticipant);
                }
                String message;
                if (isPrivate) {
                    message = String.format("Private net %s now active", net.getCallsign());
                } else {
                    message = String.format("Net %s now active", net.getCallsign());
                }
                transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), message);
                return;
            }
        } catch (Exception e) {
            logger.error("Exception caught creating ad-hoc net", e);
        }
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), "Could not start net");
    }

    private void processNetList(User loggedInUser, APRSObject netManagerObject, APRSMessage msg, String transceiverSourceId) {
        List<Net> nets = netAccessor.getAll(loggedInUser, null);

        if ((nets == null) || (nets.isEmpty())) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), "No active nets");
            return;
        }

        String preamble = "Active nets: ";
        String netList = preamble;
        for (Net net : nets) {
            if (!net.isOpen()) {
                // don't show private nets
                continue;
            }
            if (netList.length() > preamble.length()) {
                netList += ",";
            }
            netList += net.getCallsign();
            if (netList.length() >= 50) {
                transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), netList);
                netList = preamble;
            }
        }
        if (!netList.isEmpty()) {
            // get the last
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), msg.getCallsignFrom(), netList);
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

    private void processBadCommand(User loggedInUser, APRSObject netManagerObject, APRSMessage innerAPRSMessage, String transceiverSourceId) {
        logger.warn("Unexpected or unauthorized message sent to net manager object - "+((netManagerObject.getCallsignTo() != null) ? netManagerObject.getCallsignTo() : "UNKNOWN"));
        logger.warn("Message: "+((innerAPRSMessage.getMessage() != null) ? innerAPRSMessage.getMessage() : "UNKNOWNMESSAGE"));
        if (!innerAPRSMessage.getCallsignTo().equalsIgnoreCase(netManagerObject.getCallsignTo())) {
            // dont send bad command back to itself
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, netManagerObject.getCallsignTo(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
        }
    }

    private void ackMessage(User loggedInUser, APRSMessage msg, String transceiverSourceId) {
        statisticsAccessor.incrementAcksRequested();
        statisticsAccessor.incrementAcksSent();
        transceiverMessageAccessor.sendAckMessage(loggedInUser, transceiverSourceId, msg.getCallsignTo(), msg.getCallsignFrom(), APRSConstants.ACK+msg.getMessageNumber());
    }

    public void processDirectedStatusQuery(User loggedInUser, APRSMessage innerAPRSMessage, String transceiverSourceId, APRSObject aprsObject) {
        if (loggedInUser == null) {
            return;
        }
        if (innerAPRSMessage == null) {
            return;
        }
        if (innerAPRSMessage.getMessage() == null) {
            return;
        }
        if (!innerAPRSMessage.getMessage().startsWith(APRSQueryType.PREFIX)) {
            return;
        }

        try {
            String queryType = innerAPRSMessage.getMessage().substring(1);

            if (queryType.equalsIgnoreCase(NetCentralQueryType.COMMANDS)) {
                // list commands
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getNetManagerCommandsResponse(loggedInUser, aprsObject));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.INFO)) {
                // send commands and info
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getNetManagerCommandsResponse(loggedInUser, aprsObject));
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getNetManagerObjectInfoResponse(loggedInUser, aprsObject));
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_OBJECTS)) {
                // announce object
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_POSITION)) {
                // send position packet
                if ((aprsObject.getLat() != null) && (aprsObject.getLon() != null)) {
                    transceiverMessageAccessor.sendObject(loggedInUser, aprsObject.getCallsignTo(), aprsObject.getCallsignTo(),
                                                            aprsObject.getComment(), aprsObject.isAlive(),
                                                            aprsObject.getLat(), aprsObject.getLon(), aprsObject.getSymbolTableId(), aprsObject.getSymbolTableCode());
                }
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_STATUS)) {
                // send net status
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE)) {
                // send object type
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getNetManagerObjectTypeResponse(loggedInUser, aprsObject));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.GENERAL_INFO)) {
                // send general info
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getNetManagerObjectInfoResponse(loggedInUser, aprsObject));
            }
        } catch (Exception e) {
        }
    }

    private String getNetManagerObjectTypeResponse(User loggedInUser, APRSObject generalResourceObject) {
        return String.format("%s:%s", NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE, generalResourceObject.getType().toString());
    }

    private String getNetManagerCommandsResponse(User loggedInUser, APRSObject generalResourceObject) {
        return String.format("%s:%s,%s,%s,%s,%s,%s", NetCentralQueryType.COMMANDS, NetCentralQueryType.COMMANDS, APRSQueryType.APRS_OBJECTS, APRSQueryType.APRS_POSITION, APRSQueryType.APRS_STATUS,
                                                NetCentralQueryType.GENERAL_INFO, NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE);
    }

    private String getNetManagerObjectInfoResponse(User loggedInUser, APRSObject generalResourceObject) {
        return String.format("%s:%s,%s", NetCentralQueryType.GENERAL_INFO, generalResourceObject.getCallsignTo(), generalResourceObject.getComment());
    }

    private String getCallsignRoot(String callsignFrom) {
        int index = -1;
        if ((index = callsignFrom.indexOf("-")) != -1) {
            return callsignFrom.substring(0, index);
        }
        return callsignFrom;
    }
}
