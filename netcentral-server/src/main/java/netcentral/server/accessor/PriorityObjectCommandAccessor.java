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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ObjectShelterReportingTimeframe;
import netcentral.server.object.User;

@Singleton
public class PriorityObjectCommandAccessor {
    private static final Logger logger = LogManager.getLogger(PriorityObjectCommandAccessor.class);
    private static final String REPORT_COMMAND = "REPORT";

    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;
    @Inject
    private ReportAccessor reportAccessor;
    @Inject
    private CallsignAceAccessor accessControlAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;


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
        logger.warn("Unexpected or unauthorized message sent to priority object - "+((priorityObject.getCallsignFrom() != null) ? priorityObject.getCallsignFrom() : "UNKNOWN"));
        logger.warn("Message: "+((innerAPRSMessage.getMessage() != null) ? innerAPRSMessage.getMessage() : "UNKNOWNMESSAGE"));
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
    }

    private void processEOCCommand(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage, String transceiverSourceId, String message, boolean canChange) {
        boolean commandAccepted = false;
        Object report = null;

        if (message == null) {
            return;
        }
        if (REPORT_COMMAND.equalsIgnoreCase(message)) {
            sendEOCReports(loggedInUser, priorityObject, innerAPRSMessage, transceiverSourceId, message);
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
                if ((report = APRSNetCentralEOCContactReport.isValid(priorityObject.getCallsignFrom(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralEOCContactReport) report);
                } else if ((report = APRSNetCentralEOCMobilizationReport.isValid(priorityObject.getCallsignFrom(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralEOCMobilizationReport) report);
                }
            }
        }

        if (commandAccepted && (report != null)) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Update message accepted");
            transceiverMessageAccessor.sendReport(loggedInUser, (APRSNetCentralReport) report);
        }  else {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
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
        if (REPORT_COMMAND.equalsIgnoreCase(message)) {
            sendShelterReports(loggedInUser, priorityObject, innerAPRSMessage, transceiverSourceId, message);
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
                if ((report = APRSNetCentralShelterWorkerReport.isValid(priorityObject.getCallsignFrom(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralShelterWorkerReport) report);
                } else if ((report = APRSNetCentralShelterStatusReport.isValid(priorityObject.getCallsignFrom(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralShelterStatusReport) report);
                } else if ((report = APRSNetCentralShelterOperationalMaterielReport.isValid(priorityObject.getCallsignFrom(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralShelterOperationalMaterielReport) report);
                } else if ((report = APRSNetCentralShelterOperationalFoodReport.isValid(priorityObject.getCallsignFrom(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralShelterOperationalFoodReport) report);
                } else if ((report = APRSNetCentralShelterCensusReport.isValid(priorityObject.getCallsignFrom(), message)) != null) {
                    commandAccepted = updateReport(loggedInUser, (APRSNetCentralShelterCensusReport) report);
                }
            }
        }
        if (commandAccepted && (report != null)) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Update message accepted");
            transceiverMessageAccessor.sendReport(loggedInUser, (APRSNetCentralReport) report);
        }  else {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
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
//        boolean commandAccepted = false;
//        Object report = null;
        if (message == null) {
            return;
        }
        if (REPORT_COMMAND.equalsIgnoreCase(message)) {
            sendMedicalReports(loggedInUser, priorityObject, innerAPRSMessage, transceiverSourceId, message);
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

//        if (commandAccepted && (report != null)) {
//            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Update message accepted");
//            transceiverMessageAccessor.sendReport(loggedInUser, (APRSNetCentralReport) report);
//        }  else {
//            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
//        }
    }

    private void ackMessage(User loggedInUser, APRSMessage msg, String transceiverSourceId) {
        logger.info("Acknowledging message");
        statisticsAccessor.incrementAcksRequested();
        statisticsAccessor.incrementAcksSent();
        transceiverMessageAccessor.sendAckMessage(loggedInUser, transceiverSourceId, msg.getCallsignTo(), msg.getCallsignFrom(), "ack"+msg.getMessageNumber());
    }

    private void sendEOCReports(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage, String transceiverSourceId, String message) {
        try {
            APRSNetCentralReport report = reportAccessor.getEOCContactInformation(loggedInUser, innerAPRSMessage.getCallsignTo());
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Contacts: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending EOC contact report", e);
        }

        try {
            APRSNetCentralReport report = reportAccessor.getEOCMobilizationInformation(loggedInUser, innerAPRSMessage.getCallsignTo());
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "MobStatus: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending EOC mobilization report", e);
        }
    }

    private void sendShelterReports(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage, String transceiverSourceId, String message) {
        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterStatusReport(loggedInUser, innerAPRSMessage.getCallsignTo());
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Status: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending shelter census report", e);
        }

        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterCensusReport(loggedInUser, innerAPRSMessage.getCallsignTo());
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Census: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending shelter census report", e);
        }

        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterWorkerReport(loggedInUser, innerAPRSMessage.getCallsignTo(), 1);
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Workers1: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending worker census shift 1 report", e);
        }

        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterWorkerReport(loggedInUser, innerAPRSMessage.getCallsignTo(), 2);
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Workers2: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending worker census shift 2 report", e);
        }

        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterWorkerReport(loggedInUser, innerAPRSMessage.getCallsignTo(), 3);
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Workers3: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending worker census shift 3 report", e);
        }

        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalFoodReport(loggedInUser, innerAPRSMessage.getCallsignTo(), ObjectShelterReportingTimeframe.ON_HAND);
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "FoodHave: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending food (today) report", e);
        }

        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalFoodReport(loggedInUser, innerAPRSMessage.getCallsignTo(), ObjectShelterReportingTimeframe.REQUIRED);
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "FoodReq: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending food (tomorrow) report", e);
        }

        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalFoodReport(loggedInUser, innerAPRSMessage.getCallsignTo(), ObjectShelterReportingTimeframe.USED);
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "FoodUsed: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending food (needed) report", e);
        }

        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalMaterielReport(loggedInUser, innerAPRSMessage.getCallsignTo(), ObjectShelterReportingTimeframe.ON_HAND);
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "MatHave: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending materiel (today) report", e);
        }

        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalMaterielReport(loggedInUser, innerAPRSMessage.getCallsignTo(), ObjectShelterReportingTimeframe.REQUIRED);
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "MatReq: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending materiel (tomorrow) report", e);
        }

        try {
            APRSNetCentralReport report = reportAccessor.getLatestShelterOperationalMaterielReport(loggedInUser, innerAPRSMessage.getCallsignTo(), ObjectShelterReportingTimeframe.USED);
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "MatUsed: "+report.getReportData());
        } catch (Exception e) {
           logger.error("Exception caught sending materiel (needed) report", e);
        }
    }

    private void sendMedicalReports(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage, String transceiverSourceId, String message) {
    }
}
