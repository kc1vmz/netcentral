package netcentral.server.accessor;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.constants.NetCentralUserDefinedPacketConstant;
import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.object.APRSUserDefined;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralEOCContactReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralEOCMobilizationReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetAnnounceReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetCheckInOutReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetMessageReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetQuestionAnswerReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetQuestionReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetSecureReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetStartReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralObjectAnnounceReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterCensusReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterOperationalFoodReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterOperationalMaterielReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterStatusReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterWorkerReport;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.RadioStyle;
import netcentral.server.object.Net;
import netcentral.server.object.NetMessage;
import netcentral.server.object.NetQuestion;
import netcentral.server.object.NetQuestionAnswer;
import netcentral.server.object.Participant;
import netcentral.server.object.User;
import netcentral.server.record.aprs.APRSObjectRecord;
import netcentral.server.repository.aprs.APRSObjectRepository;

@Singleton
public class FederatedObjectReportIngestionAccessor {
    private static final Logger logger = LogManager.getLogger(FederatedObjectReportIngestionAccessor.class);

    @Inject
    private NetAccessor netAccessor;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;
    @Inject
    private APRSObjectRepository aprsObjectRepository;
    @Inject
    private ParticipantAccessor participantAccessor;
    @Inject
    private NetMessageAccessor netMessageAccessor;
    @Inject
    private NetQuestionAccessor netQuestionAccessor;
    @Inject
    private NetQuestionAnswerAccessor netQuestionAnswerAccessor;
    @Inject
    private ReportAccessor reportAccessor;

    
    public void processFederatedPacket(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime) {
        try {
            String data = new String(innerAPRSUserDefined.getData());
            String objectName = innerAPRSUserDefined.getCallsignFrom();

            if (processFederatedNetCreation(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralNetAnnounceReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedNetStart(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralNetStartReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedNetSecure(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralNetSecureReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedNetCheckInOut(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralNetCheckInOutReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedObjectAnnounce(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralObjectAnnounceReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedNetMessage(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralNetMessageReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedNetQuestion(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralNetQuestionReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedNetQuestionAnswer(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralNetQuestionAnswerReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedEOCContact(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralEOCContactReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedEOCMobilization(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralEOCMobilizationReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedShelterCensus(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralShelterCensusReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedShelterOperationalFood(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralShelterOperationalFoodReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedShelterOperationalMateriel(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralShelterOperationalMaterielReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedShelterStatus(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralShelterStatusReport.isValid(objectName, data))) {
                return;
            }
            if (processFederatedShelterWorkerCensus(loggedInUser, id, innerAPRSUserDefined, source, heardTime, APRSNetCentralShelterWorkerReport.isValid(objectName, data))) {
                return;
            }
        } catch (Exception e) {
        }
    }

    private boolean processFederatedShelterWorkerCensus(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralShelterWorkerReport report) {
        if (report == null) {
            return false;
        }
        reportAccessor.addShelterWorkerReport(loggedInUser, report);
        return true;
    }

    private boolean processFederatedShelterStatus(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralShelterStatusReport report) {
        if (report == null) {
            return false;
        }
        reportAccessor.addShelterStatusReport(loggedInUser, report);
        return true;
    }

    private boolean processFederatedShelterOperationalMateriel(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralShelterOperationalMaterielReport report) {
        if (report == null) {
            return false;
        }
        reportAccessor.addShelterOperationalMaterielReport(loggedInUser, report);
        return true;
    }

    private boolean processFederatedShelterOperationalFood(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralShelterOperationalFoodReport report) {
        if (report == null) {
            return false;
        }
        reportAccessor.addShelterOperationalFoodReport(loggedInUser, report);
        return true;
    }

    private boolean processFederatedShelterCensus(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralShelterCensusReport report) {
        if (report == null) {
            return false;
        }
        reportAccessor.addShelterCensusReport(loggedInUser, report);
        return true;
    }

    private boolean processFederatedEOCMobilization(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralEOCMobilizationReport report) {
        if (report == null) {
            return false;
        }
        reportAccessor.addEOCMobilizationReport(loggedInUser, report);
        return true;
    }

    private boolean processFederatedEOCContact(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralEOCContactReport report) {
        if (report == null) {
            return false;
        }
        reportAccessor.addEOCContactReport(loggedInUser, report);
        return true;
    }

    private boolean processFederatedObjectAnnounce(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralObjectAnnounceReport report) {
        if (report == null) {
            return false;
        }
        ObjectType type = ObjectType.STANDARD;
        if (report.getType().equals(APRSNetCentralObjectAnnounceReport.OBJECT_TYPE_EOC)) {
            type = ObjectType.EOC;
        } else if (report.getType().equals(APRSNetCentralObjectAnnounceReport.OBJECT_TYPE_MEDICAL)) {
            type = ObjectType.MEDICAL;
        } else if (report.getType().equals(APRSNetCentralObjectAnnounceReport.OBJECT_TYPE_SHELTER)) {
            type = ObjectType.SHELTER;
        } else if (report.getType().equals(APRSNetCentralObjectAnnounceReport.OBJECT_TYPE_GENERAL)) {
            type = ObjectType.RESOURCE;
        }
        if (type != ObjectType.STANDARD) {
            updateFederatedObjectType(innerAPRSUserDefined.getCallsignFrom(), type);
        }
        return true;
    }

    private boolean processFederatedNetMessage(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralNetMessageReport report) {
        if (report == null) {
            return false;
        }
        Net net = null;
        try {
            net = netAccessor.getByCallsign(loggedInUser, innerAPRSUserDefined.getCallsignFrom());
        } catch (Exception e) {
            net = null;
        }

        if ((net == null) || ((net != null) && (!net.isRemote()))) {
            // either does not exist in our db or is a local net
            return true;
        }

        // persist the net message
        NetMessage netMessage = new NetMessage();
        netMessage.setCallsignFrom(innerAPRSUserDefined.getCallsignFrom());
        netMessage.setCompletedNetId(net.getCompletedNetId());
        netMessage.setId(UUID.randomUUID().toString());
        netMessage.setMessage(report.getMessageText());
        netMessage.setReceivedTime(ZonedDateTime.now());
        if (report.getRecipient().equals(APRSNetCentralNetMessageReport.RECIPIENT_ALL)) {
            netMessage.setRecipient(NetMessage.RECIPIENT_ENTIRE_NET);
        } else {
            netMessage.setRecipient(NetMessage.RECIPIENT_NET_CONTROL);
        }

        netMessage = netMessageAccessor.create(loggedInUser, netMessage);
        return true;
    }

    private boolean processFederatedNetQuestion(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralNetQuestionReport report) {
        if (report == null) {
            return false;
        }
        Net net = null;
        try {
            net = netAccessor.getByCallsign(loggedInUser, innerAPRSUserDefined.getCallsignFrom());
        } catch (Exception e) {
            net = null;
        }

        if ((net == null) || ((net != null) && (!net.isRemote()))) {
            // either does not exist in our db or is a local net
            return true;
        }

        // persist the net message
        NetQuestion netQuestion = new NetQuestion();
        netQuestion.setActive(true);
        netQuestion.setAskedTime(ZonedDateTime.now());
        netQuestion.setCompletedNetId(net.getCompletedNetId());
        netQuestion.setNetQuestionId(UUID.randomUUID().toString());
        netQuestion.setReminderMinutes(0);
        netQuestion.setNextReminderTime(null);
        netQuestion.setNumber(Integer.parseInt(report.getQuestionNumber()));
        netQuestion.setQuestionText(report.getQuestionText());


        netQuestionAccessor.create(loggedInUser, netQuestion, net, null);
        return true;
    }

    private boolean processFederatedNetQuestionAnswer(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralNetQuestionAnswerReport report) {
        if (report == null) {
            return false;
        }
        Net net = null;
        try {
            net = netAccessor.getByCallsign(loggedInUser, innerAPRSUserDefined.getCallsignFrom());
        } catch (Exception e) {
            net = null;
        }

        if ((net == null) || ((net != null) && (!net.isRemote()))) {
            // either does not exist in our db or is a local net
            return true;
        }

        NetQuestion netQuestion = null;
        try {
            netQuestion = netQuestionAccessor.getByQuestionNumber(loggedInUser, net.getCompletedNetId(), Integer.parseInt(report.getQuestionNumber()));
        } catch (Exception e) {
        }

        if (netQuestion == null) {
            return true; // did not find question
        }
        // persist the net message
        NetQuestionAnswer netQuestionAnswer = new NetQuestionAnswer();
        netQuestionAnswer.setAnsweredTime(ZonedDateTime.now());
        netQuestionAnswer.setCompletedNetId(net.getCompletedNetId());
        netQuestionAnswer.setNetQuestionAnswerId(UUID.randomUUID().toString());
        netQuestionAnswer.setNetQuestionId(netQuestion.getNetQuestionId());
        netQuestionAnswer.setAnswerText(report.setAnswerText());
        netQuestionAnswer.setCallsign(report.getSenderCallsign());
        netQuestionAnswerAccessor.create(loggedInUser, netQuestion, netQuestionAnswer, net);

        return true;
    }

    private boolean processFederatedNetCheckInOut(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralNetCheckInOutReport report) {
        if (report == null) {
            return false;
        }
        Net net = null;
        try {
            net = netAccessor.getByCallsign(loggedInUser, innerAPRSUserDefined.getCallsignFrom());
        } catch (Exception e) {
            net = null;
        }

        if ((net == null) || ((net != null) && (!net.isRemote()))) {
            // either does not exist in our db or is a local net
            return true;
        }

        Participant participant = null;
        try {
            participant = new Participant(report.getReportData(), "Unknown", null, heardTime, null, null, ElectricalPowerType.UNKNOWN, ElectricalPowerType.UNKNOWN, 
                                        RadioStyle.UNKNOWN, 0, null, heardTime);
            Participant cparticipant = participantAccessor.create(loggedInUser, participant);
            participant = cparticipant;
        } catch (Exception e) {
        }

        try {
            if (report.isCheckIn()) {
                netParticipantAccessor.addParticipant(loggedInUser, net, participant);
            } else {
                netParticipantAccessor.removeParticipant(loggedInUser, net, participant);
            }
        } catch (Exception e) {
        }

        return true;
    }

    private boolean processFederatedNetSecure(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralNetSecureReport report) {
        if (report == null) {
            return false;
        }
        Net net = null;
        try {
            net = netAccessor.getByCallsign(loggedInUser, innerAPRSUserDefined.getCallsignFrom());
        } catch (Exception e) {
            net = null;
        }

        if ((net == null) || ((net != null) && (!net.isRemote()))) {
            // either does not exist in our db or is a local net
            return true;
        }

        try {
            netAccessor.delete(loggedInUser, net.getCallsign());

            updateFederatedObjectType(innerAPRSUserDefined.getCallsignFrom(), ObjectType.NET);
        } catch (Exception e) {
        }
        return true;
    }

    private boolean processFederatedNetStart(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralNetStartReport report) {
        if (report == null) {
            return false;
        }
        Net net = null;
        try {
            net = netAccessor.getByCallsign(loggedInUser, innerAPRSUserDefined.getCallsignFrom());
        } catch (Exception e) {
            net = null;
        }

        try {
            if (net == null) {
                // not in db yet, create placeholder
                net = new Net(innerAPRSUserDefined.getCallsignFrom(), " ", " ", "", report.getStartTime(),
                                        UUID.randomUUID().toString(), null, null, false, "Remote", false, null, false, false, true);
                netAccessor.create(loggedInUser, net);

                updateFederatedObjectType(innerAPRSUserDefined.getCallsignFrom(), ObjectType.NET);
                return true;
            } else if (!net.isRemote()) {
                // is local - skip
                return true;
            }

            // is a remote net
            net.setStartTime(report.getStartTime());
            netAccessor.update(loggedInUser, net.getCallsign(), net);

            updateFederatedObjectType(innerAPRSUserDefined.getCallsignFrom(), ObjectType.NET);
        } catch (Exception e) {
        }
        return true;
    }

    private void updateFederatedObjectType(String callsignFrom, ObjectType type) {
        // make the generic object a Net
        try {
            List<APRSObjectRecord> foundRecList = aprsObjectRepository.findBycallsign_from(callsignFrom);
            if ((foundRecList != null) && (!foundRecList.isEmpty())) {
                // objects get overwritten, not created new
                APRSObjectRecord first = foundRecList.get(0);
                APRSObjectRecord updated = new APRSObjectRecord(first.aprs_object_id(), first.source(), first.callsign_from(), first.callsign_to(), first.heard_time(), first.alive(), first.lat(),
                                                                        first.lon(), first.time(), first.comment(), type.ordinal());
                aprsObjectRepository.update(updated);
            }
        } catch (Exception e) {
        }
    }

    private boolean processFederatedNetCreation(User loggedInUser, String id, APRSUserDefined innerAPRSUserDefined, String source, ZonedDateTime heardTime, APRSNetCentralNetAnnounceReport report) {
        if (report == null) {
            return false;
        }

        Net net = null;
        try {
            net = netAccessor.getByCallsign(loggedInUser, innerAPRSUserDefined.getCallsignFrom());
        } catch (Exception e) {
            net = null;
        }

        try {
            if (net != null) {
                // previously created
                if (!net.isRemote()) {
                    // this is a local net here - get out
                    logger.warn("Net creation message seen for local net "+ innerAPRSUserDefined.getCallsignFrom());
                } else {
                    // this is an existing remote net - lets update
                    net.setName(report.getName());
                    net.setDescription(report.getDescription());
                    netAccessor.update(loggedInUser, net.getCallsign(), net);

                    updateFederatedObjectType(innerAPRSUserDefined.getCallsignFrom(), ObjectType.NET);
                }
                return true;
            }

            net = new Net(innerAPRSUserDefined.getCallsignFrom(), report.getName(), report.getDescription(), "", heardTime,
                                    UUID.randomUUID().toString(), null, null, false, "Remote", false, null, false, false, true);
            netAccessor.create(loggedInUser, net);

            updateFederatedObjectType(innerAPRSUserDefined.getCallsignFrom(), ObjectType.NET);
        } catch (Exception e) {
        }
        return true;
    }

    public boolean isFederatedUserDefinedPacket(APRSUserDefined innerAPRSUserDefined) {
        boolean federated = false;
        if ((innerAPRSUserDefined != null) && 
            (innerAPRSUserDefined.getDti() == NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_APRS_COMMAND) && 
            (innerAPRSUserDefined.getUserId().equals(""+NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_USER_ID)) && 
            (innerAPRSUserDefined.getPacketType().equals(""+NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_TYPE))) {
            federated = true;
        }
        return federated;
    }
}
