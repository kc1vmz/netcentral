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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.RegisteredTransceiver;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessage;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessageMany;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverObject;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverQuery;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralReport;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.User;
import netcentral.server.transceiver.client.TransceiverRESTClient;

@Singleton
public class TransceiverCommunicationAccessor {
    private static final Logger logger = LogManager.getLogger(TransceiverCommunicationAccessor.class);
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private TransceiverRESTClient transceiverRESTClient;
    @Inject
    private StatisticsAccessor statisticsAccessor;


    public void sendQuery(User loggedInUser, String callsignFrom, String callsignTo, String queryType) {
        // send to all transceivers
        List<RegisteredTransceiver> transceivers = registeredTransceiverAccessor.getAll(loggedInUser);
        if ((transceivers != null) && (!transceivers.isEmpty())) {
            for (RegisteredTransceiver transceiver : transceivers) {
                if (transceiver.isEnabledTransmit()) {
                    TransceiverQuery msg = new TransceiverQuery();
                    msg.setCallsignFrom(callsignFrom);
                    msg.setCallsignTo(callsignTo);
                    msg.setTransceiverId(transceiver.getId());
                    msg.setQueryType(queryType);
                    sendQuery(transceiver, msg);
                }
            }
        }
    }

    public void sendMessage(User loggedInUser, String callsignFrom, String callsignTo, String message) {
        // send to all transceivers
        List<RegisteredTransceiver> transceivers = registeredTransceiverAccessor.getAll(loggedInUser);
        if ((transceivers != null) && (!transceivers.isEmpty())) {
            for (RegisteredTransceiver transceiver : transceivers) {
                if (transceiver.isEnabledTransmit()) {
                    TransceiverMessage msg = new TransceiverMessage();
                    msg.setCallsignFrom(callsignFrom);
                    msg.setCallsignTo(callsignTo);
                    msg.setTransceiverId(transceiver.getId());
                    msg.setMessage(message);
                    msg.setBulletin(false);
                    sendMessage(transceiver, msg);
                }
            }
        }
    }

    public void sendMessageNoAck(User loggedInUser, String callsignFrom, String callsignTo, String message) {
        // send to all transceivers
        List<RegisteredTransceiver> transceivers = registeredTransceiverAccessor.getAll(loggedInUser);
        if ((transceivers != null) && (!transceivers.isEmpty())) {
            for (RegisteredTransceiver transceiver : transceivers) {
                if (transceiver.isEnabledTransmit()) {
                    TransceiverMessage msg = new TransceiverMessage();
                    msg.setCallsignFrom(callsignFrom);
                    msg.setCallsignTo(callsignTo);
                    msg.setTransceiverId(transceiver.getId());
                    msg.setMessage(message);
                    msg.setBulletin(false);
                    msg.setAckRequested(false);
                    sendMessage(transceiver, msg);
                }
            }
        }
    }

    public void sendReport(User loggedInUser, APRSNetCentralReport report) {
        // send to all transceivers
        List<RegisteredTransceiver> transceivers = registeredTransceiverAccessor.getAll(loggedInUser);
        if ((transceivers != null) && (!transceivers.isEmpty())) {
            for (RegisteredTransceiver transceiver : transceivers) {
                if (transceiver.isEnabledTransmit()) {
                    TransceiverReport msg = new TransceiverReport();
                    msg.setCallsignFrom(report.getObjectName());
                    msg.setReport(report);
                    msg.setTransceiverId(transceiver.getId());
                    sendReport(transceiver, msg);
                }
            }
        }
    }

    public void sendBulletin(User loggedInUser, String callsignFrom, String bulletinId, String message) {
        // send to all transceivers
        List<RegisteredTransceiver> transceivers = registeredTransceiverAccessor.getAll(loggedInUser);
        if ((transceivers != null) && (!transceivers.isEmpty())) {
            for (RegisteredTransceiver transceiver : transceivers) {
                if (transceiver.isEnabledTransmit()) {
                    TransceiverMessage msg = new TransceiverMessage();
                    msg.setCallsignFrom(callsignFrom);
                    msg.setCallsignTo(bulletinId);
                    msg.setTransceiverId(transceiver.getId());
                    msg.setMessage(message);
                    msg.setBulletin(true);
                    sendMessage(transceiver, msg);
                }
            }
        }
    }

    public void sendMessage(User loggedInUser, String sourceId, String callsignFrom, String callsignTo, String message) {
        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, sourceId);
        if (transceiver == null) {
            logger.warn("Transceiver not provided");
            // unknown
            return;
        }
        if (transceiver.isEnabledTransmit()) {
            TransceiverMessage msg = new TransceiverMessage();
            msg.setCallsignFrom(callsignFrom);
            msg.setCallsignTo(callsignTo);
            msg.setTransceiverId(sourceId);
            msg.setMessage(message);
            msg.setBulletin(false);
            msg.setAckRequested(true);
            sendMessage(transceiver, msg);
        }
    }

    public void sendMessageNoAck(User loggedInUser, String sourceId, String callsignFrom, String callsignTo, String message) {
        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, sourceId);
        if (transceiver == null) {
            logger.warn("Transceiver not provided");
            // unknown
            return;
        }
        if (transceiver.isEnabledTransmit()) {
            TransceiverMessage msg = new TransceiverMessage();
            msg.setCallsignFrom(callsignFrom);
            msg.setCallsignTo(callsignTo);
            msg.setTransceiverId(sourceId);
            msg.setMessage(message);
            msg.setBulletin(false);
            msg.setAckRequested(false);
            sendMessage(transceiver, msg);
        }
    }

    public void sendAckMessage(User loggedInUser, String sourceId, String callsignFrom, String callsignTo, String message) {
        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, sourceId);
        if (transceiver == null) {
            logger.warn("Transceiver not provided");
            // unknown
            return;
        }
        if (transceiver.isEnabledTransmit()) {
            TransceiverMessage msg = new TransceiverMessage();
            msg.setCallsignFrom(callsignFrom);
            msg.setCallsignTo(callsignTo);
            msg.setTransceiverId(sourceId);
            msg.setMessage(message);
            msg.setBulletin(false);
            msg.setAckRequested(false);
            sendMessage(transceiver, msg);
        }
    }

    public void sendBulletin(User loggedInUser, String sourceId, String callsignFrom, String bulletinId, String message) {
        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, sourceId);
        if (transceiver == null) {
            logger.warn("Transceiver not provided");
            // unknown
            return;
        }
        if (transceiver.isEnabledTransmit()) {
            TransceiverMessage msg = new TransceiverMessage();
            msg.setCallsignFrom(callsignFrom);
            msg.setCallsignTo(bulletinId);
            msg.setTransceiverId(sourceId);
            msg.setMessage(message);
            msg.setBulletin(true);
            sendMessage(transceiver, msg);
        }
    }

    private void sendQuery(RegisteredTransceiver transceiver, TransceiverQuery msg) {
        statisticsAccessor.markLastSentTime();
        statisticsAccessor.incrementReportsSent();
        transceiverRESTClient.sendQuery(transceiver.getFqdName(), transceiver.getPort(), msg, transceiver.getId());
    }

    private void sendReport(RegisteredTransceiver transceiver, TransceiverReport msg) {
        statisticsAccessor.markLastSentTime();
        statisticsAccessor.incrementReportsSent();
        transceiverRESTClient.sendReport(transceiver.getFqdName(), transceiver.getPort(), msg, transceiver.getId());
    }

    private void sendMessage(RegisteredTransceiver transceiver, TransceiverMessage msg) {
        statisticsAccessor.markLastSentTime();
        statisticsAccessor.incrementMessagesSent();
        transceiverRESTClient.sendMessage(transceiver.getFqdName(), transceiver.getPort(), msg, transceiver.getId());
    }

    private void sendMessages(RegisteredTransceiver transceiver, TransceiverMessageMany messages) {
        statisticsAccessor.markLastSentTime();
        if ((messages != null)  && (messages.getMessages() != null)) {
            int messageCount = messages.getMessages().size();
            while (messageCount > 0) {
                statisticsAccessor.incrementMessagesSent();
                messageCount--;
            }
        }
        transceiverRESTClient.sendMessages(transceiver.getFqdName(), transceiver.getPort(), messages, transceiver.getId());
    }

    public void sendMessages(User loggedInUser, String sourceId, String callsignFrom, String callsignTo, List<String> messages) {
        RegisteredTransceiver transceiver = registeredTransceiverAccessor.get(loggedInUser, sourceId);
        if (transceiver == null) {
            logger.warn("Transceiver not provided");
            // unknown
            return;
        }
        if (transceiver.isEnabledTransmit()) {
            TransceiverMessageMany msg = new TransceiverMessageMany();
            msg.setCallsignFrom(callsignFrom);
            msg.setCallsignTo(callsignTo);
            msg.setTransceiverId(sourceId);
            msg.setMessages(messages);
            sendMessages(transceiver, msg);
        }
    }

    public void sendObject(User loggedInUser, String callsignFrom, String objectName, String message, boolean alive, String lat, String lon) {
        // send to all transceivers
        List<RegisteredTransceiver> transceivers = registeredTransceiverAccessor.getAll(loggedInUser);
        if ((transceivers != null) && (!transceivers.isEmpty())) {
            for (RegisteredTransceiver transceiver : transceivers) {
                if (transceiver.isEnabledTransmit()) {
                    TransceiverObject obj = new TransceiverObject();
                    obj.setCallsignFrom(callsignFrom);
                    obj.setName(objectName);
                    obj.setTransceiverId(transceiver.getId());
                    obj.setMessage(message);
                    obj.setAlive(alive);
                    obj.setLat(lat);
                    obj.setLon(lon);
                    transceiverRESTClient.sendObject(transceiver.getFqdName(), transceiver.getPort(), obj, transceiver.getId());
                }
            }
        }
    }

}
