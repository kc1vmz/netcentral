package netcentral.server.accessor;

import java.util.ArrayList;
import java.util.List;

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
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralEOCContactReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralEOCMobilizationReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterCensusReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterOperationalFoodReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterOperationalMaterielReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterStatusReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterWorkerReport;
import com.kc1vmz.netcentral.common.constants.NetCentralQueryType;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ObjectShelterReportingTimeframe;
import netcentral.server.object.User;

@Singleton
public class PriorityObjectCommandAccessor {
    private static final Logger logger = LogManager.getLogger(PriorityObjectCommandAccessor.class);

    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;
    @Inject
    private ReportAccessor reportAccessor;
    @Inject
    private CallsignAceAccessor accessControlAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;
    @Inject
    private FederatedObjectReporterAccessor federatedObjectReporterAccessor;


    public void processMessage(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage, String transceiverSourceId) {
        // message is for this priority object - act and respond
        String message = preStrip(innerAPRSMessage.getMessage());

        if (innerAPRSMessage.getMessageNumber() != null) {
            ackMessage(loggedInUser, innerAPRSMessage, transceiverSourceId);
        }

        if ((message == null) || (message.isEmpty()) || (message.isBlank())) {
            return;
        }

        if (message.startsWith("ack")) {
            return;
        }

        if (priorityObject.getCallsignTo().equalsIgnoreCase(innerAPRSMessage.getCallsignFrom())) {
            // the message is from inside the house
            return;
        }

        if (message.startsWith(APRSQueryType.PREFIX)) {
            processDirectedStatusQuery(loggedInUser, innerAPRSMessage, transceiverSourceId, priorityObject);
            return;
        }

        boolean canChange = allowedToChange(loggedInUser, priorityObject, innerAPRSMessage);

        switch (priorityObject.getType()) {
            case ObjectType.EOC -> processEOCCommand(loggedInUser, priorityObject, innerAPRSMessage, transceiverSourceId, message, canChange);
            case ObjectType.SHELTER -> processShelterCommand(loggedInUser, priorityObject, innerAPRSMessage, transceiverSourceId, message, canChange);
            case ObjectType.MEDICAL -> processMedicalCommand(loggedInUser, priorityObject, innerAPRSMessage, transceiverSourceId, message, canChange);
            default -> processBadCommand(loggedInUser, priorityObject, innerAPRSMessage, transceiverSourceId);
        }
    }

    private boolean allowedToChange(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage) {
        return accessControlAccessor.isWriteable(loggedInUser, priorityObject, innerAPRSMessage.getCallsignFrom());
    }

    private String preStrip(String message) {
        // some devices put space at the beginning of a message :/ 
        String ret = message;

        while ((ret.length() > 0) && (ret.startsWith(" "))) {
            ret = ret.substring(1);
        }

        return ret;
    }

    private void processBadCommand(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage, String transceiverSourceId) {
        logger.warn("Unexpected or unauthorized message sent to priority object - "+((priorityObject.getCallsignTo() != null) ? priorityObject.getCallsignTo() : "UNKNOWN"));
        logger.warn("Message: "+((innerAPRSMessage.getMessage() != null) ? innerAPRSMessage.getMessage() : "UNKNOWNMESSAGE"));
        if (!innerAPRSMessage.getCallsignFrom().equalsIgnoreCase(priorityObject.getCallsignTo())) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignTo(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
        }
    }

    private void processEOCCommand(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage, String transceiverSourceId, String message, boolean canChange) {
        boolean commandAccepted = false;
        Object report = null;

        if (message == null) {
            return;
        }

        if (!canChange) {
            processBadCommand(loggedInUser, priorityObject, innerAPRSMessage, transceiverSourceId);
            return;
        }

        if (message.length() > 4) {
            // header is 4 characters
            String prefix = message.substring(0, 2).toUpperCase();
            if (prefix.equals("EO")) {
                if ((report = APRSNetCentralEOCContactReport.isValid(priorityObject.getCallsignTo(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralEOCContactReport) report);
                } else if ((report = APRSNetCentralEOCMobilizationReport.isValid(priorityObject.getCallsignTo(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralEOCMobilizationReport) report);
                }
            }
        }

        if (commandAccepted && (report != null)) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignTo(), innerAPRSMessage.getCallsignFrom(), "Update message accepted");
            transceiverMessageAccessor.sendReport(loggedInUser, (APRSNetCentralReport) report);
        }  else {
            if (!innerAPRSMessage.getCallsignFrom().equalsIgnoreCase(priorityObject.getCallsignTo())) {
                // dont send bad command back to itself
                transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignTo(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
            }
        }
    }

    private boolean updateReport(User loggedInUser, APRSNetCentralEOCMobilizationReport report) {
        return (reportAccessor.addEOCMobilizationReport(loggedInUser, report) != null);
    }

    private boolean updateReport(User loggedInUser, APRSNetCentralEOCContactReport report) {
        return (reportAccessor.addEOCContactReport(loggedInUser, report) != null);
    }

    private void processShelterCommand(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage, String transceiverSourceId, String message, boolean canChange) {
        boolean commandAccepted = false;
        Object report = null;

        if (message == null) {
            return;
        }
        if (!canChange) {
            processBadCommand(loggedInUser, priorityObject, innerAPRSMessage, transceiverSourceId);
            return;
        }

        if (message.length() > 4) {
            // header is 4 characters
            String prefix = message.substring(0, 2).toUpperCase();
            if (prefix.equals("SH")) {
                if ((report = APRSNetCentralShelterWorkerReport.isValid(priorityObject.getCallsignTo(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralShelterWorkerReport) report);
                } else if ((report = APRSNetCentralShelterStatusReport.isValid(priorityObject.getCallsignTo(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralShelterStatusReport) report);
                } else if ((report = APRSNetCentralShelterOperationalMaterielReport.isValid(priorityObject.getCallsignTo(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralShelterOperationalMaterielReport) report);
                } else if ((report = APRSNetCentralShelterOperationalFoodReport.isValid(priorityObject.getCallsignTo(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralShelterOperationalFoodReport) report);
                } else if ((report = APRSNetCentralShelterCensusReport.isValid(priorityObject.getCallsignTo(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralShelterCensusReport) report);
                }
            }
        }
        if (commandAccepted && (report != null)) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignTo(), innerAPRSMessage.getCallsignFrom(), "Update message accepted");
            transceiverMessageAccessor.sendReport(loggedInUser, (APRSNetCentralReport) report);
        }  else {
            if (!innerAPRSMessage.getCallsignFrom().equalsIgnoreCase(priorityObject.getCallsignTo())) {
                // dont send bad command back to itself
                transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignTo(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
            }
        }
    }

    private boolean updateReport(User loggedInUser, APRSNetCentralShelterWorkerReport report) {
        return (reportAccessor.addShelterWorkerReport(loggedInUser, report) != null);
    }
    private boolean updateReport(User loggedInUser, APRSNetCentralShelterStatusReport report) {
        return (reportAccessor.addShelterStatusReport(loggedInUser, report) != null);
    }
    private boolean updateReport(User loggedInUser, APRSNetCentralShelterOperationalMaterielReport report) {
        return (reportAccessor.addShelterOperationalMaterielReport(loggedInUser, report) != null);
    }
    private boolean updateReport(User loggedInUser, APRSNetCentralShelterOperationalFoodReport report) {
        return (reportAccessor.addShelterOperationalFoodReport(loggedInUser, report) != null);
    }
    private boolean updateReport(User loggedInUser, APRSNetCentralShelterCensusReport report) {
        return (reportAccessor.addShelterCensusReport(loggedInUser, report) != null);
    }

    private void processMedicalCommand(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage, String transceiverSourceId, String message, boolean canChange) {
        if (message == null) {
            return;
        }
        if (!canChange) {
            processBadCommand(loggedInUser, priorityObject, innerAPRSMessage, transceiverSourceId);
            return;
        }

        if (message.length() > 4) {
            // header is 4 characters
            String prefix = message.substring(0, 2).toUpperCase();
            if (prefix.equals("MD")) {
            }
        }
    }

    private void ackMessage(User loggedInUser, APRSMessage msg, String transceiverSourceId) {
        logger.info("Acknowledging message");
        statisticsAccessor.incrementAcksRequested();
        statisticsAccessor.incrementAcksSent();
        transceiverMessageAccessor.sendAckMessage(loggedInUser, transceiverSourceId, msg.getCallsignTo(), msg.getCallsignFrom(), "ack"+msg.getMessageNumber());
    }

    public void processDirectedStatusQuery(User loggedInUser, APRSMessage innerAPRSMessage, String transceiverSourceId, APRSObject priorityObject) {
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
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getPriorityObjectCommandsResponse(loggedInUser, priorityObject));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.INFO)) {
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getPriorityObjectCommandsResponse(loggedInUser, priorityObject));
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getPriorityObjectInfoResponse(loggedInUser, priorityObject));
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_OBJECTS)) {
                // announce object
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_POSITION)) {
                if ((priorityObject.getLat() != null) && (priorityObject.getLon() != null)) {
                    transceiverMessageAccessor.sendObject(loggedInUser, priorityObject.getCallsignTo(), priorityObject.getCallsignTo(),
                                                            priorityObject.getComment(), priorityObject.isAlive(),
                                                            priorityObject.getLat(), priorityObject.getLon());
                }
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_STATUS)) {
                // send status
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE)) {
                // send priority object type
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getPriorityObjectTypeResponse(loggedInUser, priorityObject));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.PRIORITY_INFO)) {
                // send priority object type
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getPriorityObjectInfoResponse(loggedInUser, priorityObject));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.PRIORITY_SHELTER_CENSUS)) {
                // send shelter census
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getPriorityObjectShelterCensusResponse(loggedInUser, priorityObject));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.PRIORITY_SHELTER_OPERATIONAL_FOOD)) {
                // send operational food
                List<String> responses = getPriorityObjectShelterOperationalFoodResponse(loggedInUser, priorityObject);
                if (!responses.isEmpty()) {
                    for (String message: responses) {
                        federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, message);
                    }
                }
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.PRIORITY_SHELTER_OPERATIONAL_MATERIEL)) {
                // send materiel
                List<String> responses = getPriorityObjectShelterOperationalMaterielResponse(loggedInUser, priorityObject);
                if (!responses.isEmpty()) {
                    for (String message: responses) {
                        federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, message);
                    }
                }
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.PRIORITY_SHELTER_STATUS)) {
                // send status
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getPriorityObjectShelterStatusResponse(loggedInUser, priorityObject));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.PRIORITY_SHELTER_WORKER_CENSUS)) {
                // send worker census
                List<String> responses = getPriorityObjectShelterWorkerCensusResponse(loggedInUser, priorityObject);
                if (!responses.isEmpty()) {
                    for (String message: responses) {
                        federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, message);
                    }
                }
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.PRIORITY_EOC_MOBILIZATION)) {
                // send EOC mobilization info
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getPriorityObjectEOCMobilizationResponse(loggedInUser, priorityObject));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.PRIORITY_EOC_STATUS)) {
                // send EOC status
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getPriorityObjectEOCStatusResponse(loggedInUser, priorityObject));
            }
        } catch (Exception e) {
        }
    }

    private String getPriorityObjectCommandsResponse(User loggedInUser, APRSObject priorityObject) {
        String ret = "";
        if (priorityObject.getType().equals(ObjectType.EOC)) {
            ret = String.format("%s:%s,%s,%s,%s,%s,%s,%s",NetCentralQueryType.COMMANDS, NetCentralQueryType.COMMANDS, NetCentralQueryType.INFO, APRSQueryType.APRS_OBJECTS, APRSQueryType.APRS_POSITION, APRSQueryType.APRS_STATUS,
                                                    NetCentralQueryType.PRIORITY_EOC_MOBILIZATION, NetCentralQueryType.PRIORITY_EOC_STATUS);
        } else if (priorityObject.getType().equals(ObjectType.SHELTER)) {
            ret = String.format("%s:%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",NetCentralQueryType.COMMANDS, NetCentralQueryType.COMMANDS, NetCentralQueryType.INFO, APRSQueryType.APRS_OBJECTS, APRSQueryType.APRS_POSITION, APRSQueryType.APRS_STATUS,
                                                    NetCentralQueryType.PRIORITY_INFO, NetCentralQueryType.PRIORITY_SHELTER_CENSUS, NetCentralQueryType.PRIORITY_SHELTER_OPERATIONAL_FOOD, 
                                                    NetCentralQueryType.PRIORITY_SHELTER_OPERATIONAL_FOOD, NetCentralQueryType.PRIORITY_SHELTER_STATUS, NetCentralQueryType.PRIORITY_SHELTER_WORKER_CENSUS,
                                                    NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE);
        }
        return ret;
    }

    private String getPriorityObjectTypeResponse(User loggedInUser, APRSObject priorityObject) {
        return String.format("%s:%s", NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE, priorityObject.getType().toString());
    }

    private String getPriorityObjectInfoResponse(User loggedInUser, APRSObject priorityObject) {
        return String.format("%s:%s,%s", NetCentralQueryType.PRIORITY_INFO, priorityObject.getCallsignTo(), priorityObject.getComment());
    }

    private String getPriorityObjectEOCMobilizationResponse(User loggedInUser, APRSObject priorityObject) {
        try {
            APRSNetCentralReport report = reportAccessor.getEOCMobilizationInformation(loggedInUser, priorityObject.getCallsignTo());
            return String.format("%s:%s", report.getObjectName(), report.getReportData());
        } catch (Exception e) {
        }
        return "";
    }

    private String getPriorityObjectEOCStatusResponse(User loggedInUser, APRSObject priorityObject) {
        try {
            APRSNetCentralReport report = reportAccessor.getEOCContactInformation(loggedInUser, priorityObject.getCallsignTo());
            return String.format("%s:%s", report.getObjectName(), report.getReportData());
        } catch (Exception e) {
        }
        return "";
    }

    private String getPriorityObjectShelterStatusResponse(User loggedInUser, APRSObject priorityObject) {
        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterStatusReport(loggedInUser, priorityObject.getCallsignTo());
            return String.format("%s:%s", report.getObjectName(), report.getReportData());
        } catch (Exception e) {
        }
        return "";
    }

    private String getPriorityObjectShelterCensusResponse(User loggedInUser, APRSObject priorityObject) {
        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterCensusReport(loggedInUser, priorityObject.getCallsignTo());
            return String.format("%s:%s", report.getObjectName(), report.getReportData());
        } catch (Exception e) {
        }
        return "";
    }

    private List<String> getPriorityObjectShelterWorkerCensusResponse(User loggedInUser, APRSObject priorityObject) {
        List<String> ret = new ArrayList<>();
        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterWorkerReport(loggedInUser, priorityObject.getCallsignTo(), 1);
            ret.add(String.format("%s:%d:%s", NetCentralQueryType.PRIORITY_SHELTER_WORKER_CENSUS, 1, report.getReportData()));
        } catch (Exception e) {
        }
        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterWorkerReport(loggedInUser, priorityObject.getCallsignTo(), 2);
            ret.add(String.format("%s:%d:%s", NetCentralQueryType.PRIORITY_SHELTER_WORKER_CENSUS, 2, report.getReportData()));
        } catch (Exception e) {
        }
        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterWorkerReport(loggedInUser, priorityObject.getCallsignTo(), 3);
            ret.add(String.format("%s:%d:%s", NetCentralQueryType.PRIORITY_SHELTER_WORKER_CENSUS, 3, report.getReportData()));
        } catch (Exception e) {
        }
        return ret;
    }

    private List<String> getPriorityObjectShelterOperationalFoodResponse(User loggedInUser, APRSObject priorityObject) {
        List<String> ret = new ArrayList<>();

        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalFoodReport(loggedInUser, priorityObject.getCallsignTo(), ObjectShelterReportingTimeframe.ON_HAND);
            ret.add(String.format("%s:%s:%s", NetCentralQueryType.PRIORITY_SHELTER_OPERATIONAL_FOOD, "OnHand", report.getReportData()));
        } catch (Exception e) {
        }
        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalFoodReport(loggedInUser, priorityObject.getCallsignTo(), ObjectShelterReportingTimeframe.REQUIRED);
            ret.add(String.format("%s:%s:%s", NetCentralQueryType.PRIORITY_SHELTER_OPERATIONAL_FOOD, "Req", report.getReportData()));
        } catch (Exception e) {
        }
        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalFoodReport(loggedInUser, priorityObject.getCallsignTo(), ObjectShelterReportingTimeframe.USED);
            ret.add(String.format("%s:%s:%s", NetCentralQueryType.PRIORITY_SHELTER_OPERATIONAL_FOOD, "Used", report.getReportData()));
        } catch (Exception e) {
        }

        return ret;
    }

    private List<String> getPriorityObjectShelterOperationalMaterielResponse(User loggedInUser, APRSObject priorityObject) {
        List<String> ret = new ArrayList<>();
        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalMaterielReport(loggedInUser, priorityObject.getCallsignTo(), ObjectShelterReportingTimeframe.ON_HAND);
            ret.add(String.format("%s:%s:%s", NetCentralQueryType.PRIORITY_SHELTER_OPERATIONAL_MATERIEL, "OnHand", report.getReportData()));
        } catch (Exception e) {
        }
        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalMaterielReport(loggedInUser, priorityObject.getCallsignTo(), ObjectShelterReportingTimeframe.REQUIRED);
            ret.add(String.format("%s:%s:%s", NetCentralQueryType.PRIORITY_SHELTER_OPERATIONAL_MATERIEL, "Req", report.getReportData()));
        } catch (Exception e) {
        }
        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalMaterielReport(loggedInUser, priorityObject.getCallsignTo(), ObjectShelterReportingTimeframe.USED);
            ret.add(String.format("%s:%s:%s", NetCentralQueryType.PRIORITY_SHELTER_OPERATIONAL_MATERIEL, "Used", report.getReportData()));
        } catch (Exception e) {
        }

        return ret;
    }
}
