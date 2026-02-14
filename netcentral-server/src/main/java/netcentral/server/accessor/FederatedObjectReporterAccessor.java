package netcentral.server.accessor;

import java.time.ZonedDateTime;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.constants.APRSQueryType;
import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetAnnounceReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetCheckInOutReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetMessageReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetQuestionAnswerReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetQuestionReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetSecureReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetStartReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralObjectAnnounceReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralReport;
import com.kc1vmz.netcentral.common.constants.NetCentralQueryType;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.NetCentralServerConfig;
import netcentral.server.object.Net;
import netcentral.server.object.NetMessage;
import netcentral.server.object.NetQuestion;
import netcentral.server.object.NetQuestionAnswer;
import netcentral.server.object.Participant;
import netcentral.server.object.User;

@Singleton
public class FederatedObjectReporterAccessor {
    private static final Logger logger = LogManager.getLogger(FederatedObjectReporterAccessor.class);

    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;
    @Inject
    private NetCentralServerConfig netConfigServerConfig;

    public void announce(User loggedInUser, APRSObject object) {
        if (netConfigServerConfig.isFederated() && object.isAlive()) {
            try {
                String reportObjectType = null;
                if (object.getType().equals(ObjectType.EOC)) {
                    reportObjectType = APRSNetCentralObjectAnnounceReport.OBJECT_TYPE_EOC;
                } else if (object.getType().equals(ObjectType.MEDICAL)) {
                    reportObjectType = APRSNetCentralObjectAnnounceReport.OBJECT_TYPE_MEDICAL;
                } else if (object.getType().equals(ObjectType.SHELTER)) {
                    reportObjectType = APRSNetCentralObjectAnnounceReport.OBJECT_TYPE_SHELTER;
                } else if (object.getType().equals(ObjectType.RESOURCE)) {
                    reportObjectType = APRSNetCentralObjectAnnounceReport.OBJECT_TYPE_GENERAL;
                }

                if (reportObjectType != null) {
                    APRSNetCentralObjectAnnounceReport report = new APRSNetCentralObjectAnnounceReport(object.getCallsignFrom(), reportObjectType, object.getCallsignFrom(), object.getComment(), object.getType());
                    if (netConfigServerConfig.isFederatedPushUserDefinedPacket()) {
                        transceiverCommunicationAccessor.sendReport(loggedInUser, report);
                    } 
                    if (netConfigServerConfig.isFederatedPushMessage()) {
                        sendReportAsMessage(loggedInUser, report);
                    }
                }
            } catch (Exception e) {
                logger.error("Exception caught", e);
            }
        }
    }

    public void announce(User loggedInUser, Net net) {
        try {
            if (netConfigServerConfig.isFederated()  && !net.isRemote()) {
                APRSNetCentralNetAnnounceReport reportAnnounce = new APRSNetCentralNetAnnounceReport(net.getCallsign(), net.getName(), net.getDescription());
                APRSNetCentralNetStartReport reportStart = new APRSNetCentralNetStartReport(net.getCallsign(), ZonedDateTime.now());
                if (netConfigServerConfig.isFederatedPushUserDefinedPacket()) {
                    transceiverCommunicationAccessor.sendReport(loggedInUser, reportAnnounce);
                    transceiverCommunicationAccessor.sendReport(loggedInUser, reportStart);
                }
                if (netConfigServerConfig.isFederatedPushMessage()) {
                    sendReportAsMessage(loggedInUser, reportAnnounce);
                    sendReportAsMessage(loggedInUser, reportStart);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }

    public void secure(User loggedInUser, Net net) {
        try {
            if (netConfigServerConfig.isFederated() && !net.isRemote()) {
                APRSNetCentralNetSecureReport reportSecure = new APRSNetCentralNetSecureReport(net.getCallsign(), ZonedDateTime.now());
                if (netConfigServerConfig.isFederatedPushUserDefinedPacket()) {
                    transceiverCommunicationAccessor.sendReport(loggedInUser, reportSecure);
                }
                if (netConfigServerConfig.isFederatedPushMessage()) {
                    sendReportAsMessage(loggedInUser, reportSecure);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }

    public void announce(User loggedInUser, Net net, Participant participant, boolean checkIn) {
        try {
            if (netConfigServerConfig.isFederated() && !net.isRemote()) {
                APRSNetCentralNetCheckInOutReport reportCheckin = new APRSNetCentralNetCheckInOutReport(net.getCallsign(), participant.getCallsign(), checkIn);
                if (netConfigServerConfig.isFederatedPushUserDefinedPacket()) {
                    transceiverCommunicationAccessor.sendReport(loggedInUser, reportCheckin);
                }
                if (netConfigServerConfig.isFederatedPushMessage()) {
                    sendReportAsMessage(loggedInUser, reportCheckin);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }

    public void announce(User loggedInUser, Net net, NetQuestion obj) {
        try {
            if (netConfigServerConfig.isFederated() && !net.isRemote()) {
                APRSNetCentralNetQuestionReport report = new APRSNetCentralNetQuestionReport(net.getCallsign(), ""+ obj.getNumber(), obj.getQuestionText());
                if (netConfigServerConfig.isFederatedPushUserDefinedPacket()) {
                    transceiverCommunicationAccessor.sendReport(loggedInUser, report);
                }
                if (netConfigServerConfig.isFederatedPushMessage()) {
                    sendReportAsMessage(loggedInUser, report);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }

    public void announce(User loggedInUser, Net net, NetQuestion question, NetQuestionAnswer answer) {
        try {
            if (netConfigServerConfig.isFederated() && !net.isRemote()) {
                APRSNetCentralNetQuestionAnswerReport report = new APRSNetCentralNetQuestionAnswerReport(net.getCallsign(), answer.getCallsign(), ""+question.getNumber(), answer.getAnswerText());
                if (netConfigServerConfig.isFederatedPushUserDefinedPacket()) {
                    transceiverCommunicationAccessor.sendReport(loggedInUser, report);
                }
                if (netConfigServerConfig.isFederatedPushMessage()) {
                    sendReportAsMessage(loggedInUser, report);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }

    public void announceStart(User loggedInUser, Net net) {
        try {
            if (netConfigServerConfig.isFederated() && !net.isRemote()) {
                APRSNetCentralNetStartReport reportStart = new APRSNetCentralNetStartReport(net.getCallsign(), net.getStartTime());
                if (netConfigServerConfig.isFederatedPushUserDefinedPacket()) {
                    transceiverCommunicationAccessor.sendReport(loggedInUser, reportStart);
                }
                if (netConfigServerConfig.isFederatedPushMessage()) {
                    sendReportAsMessage(loggedInUser, reportStart);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }

    public void announce(User loggedInUser, Net net, NetMessage netMessage, boolean isNetCentral) {
        try {
            if (netConfigServerConfig.isFederated() && !net.isRemote()) {
                APRSNetCentralNetMessageReport report = new APRSNetCentralNetMessageReport(net.getCallsign(), !isNetCentral, netMessage.getMessage());
                if (netConfigServerConfig.isFederatedPushUserDefinedPacket() ) {
                    transceiverCommunicationAccessor.sendReport(loggedInUser, report);
                }
                if (netConfigServerConfig.isFederatedPushMessage()) {
                    sendReportAsMessage(loggedInUser, report);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }

    private void sendReportAsMessage(User loggedInUser, APRSNetCentralReport report) {
        try {
            transceiverCommunicationAccessor.sendMessageNoAck(loggedInUser, report.getObjectName(), report.getObjectName(), report.getFullReportData());
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }

    private void sendMessage(User loggedInUser, String transceiverSourceId, String callsignTo, String callsignFrom, String message) {
        try {
            transceiverCommunicationAccessor.sendMessageNoAck(loggedInUser, transceiverSourceId, callsignTo, callsignFrom, message);
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }

    public void sendCommandResponse(User loggedInUser, String transceiverSourceId, APRSMessage innerAPRSMessage, String message) {
        sendMessage(loggedInUser, transceiverSourceId, innerAPRSMessage.getCallsignTo(), innerAPRSMessage.getCallsignFrom(), message);
    }

    public void interrogate(User loggedInUser, String source, APRSObject innerAPRSObject) {
        try {
            if (netConfigServerConfig.isFederated() && netConfigServerConfig.isFederatedPushMessage() && innerAPRSObject.isAlive() && (netConfigServerConfig.isFederatedInterrogate())) {
                transceiverCommunicationAccessor.sendMessageNoAck(loggedInUser, source, null, innerAPRSObject.getCallsignFrom(), APRSQueryType.PREFIX+NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE);
            }
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }
}
