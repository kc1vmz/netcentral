package netcentral.transceiver.agw.accessor;

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

import java.util.concurrent.ArrayBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessage;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessageMany;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverObject;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverQuery;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverReport;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverRequest;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.agw.config.APRSConfiguration;
import netcentral.transceiver.agw.config.QueueConfiguration;


@Singleton
public class SendQueueAccessor {
    private static final Logger logger = LogManager.getLogger(SendQueueAccessor.class);

    @Inject
    private APRSMessageAccessor aprsMessageAccessor;
    @Inject
    private QueueConfiguration queueConfiguration;
    @Inject
    private StatisticsAccessor statisticsAccessor;
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private APRSConfiguration aprsConfiguration;

    private boolean stop = false;
    private ArrayBlockingQueue<TransceiverRequest> queue = null;

    private void initializeQueue() {
        if (queue == null) {
            queue = new ArrayBlockingQueue<>(queueConfiguration.getQueueSize());
        }
    }
    public synchronized ArrayBlockingQueue<TransceiverRequest> getQueue() {
        initializeQueue();
        return queue;
    }    

    public void queueRequest(TransceiverRequest message) {
        if (!stayRunning()) {
            return;
        }
        ArrayBlockingQueue<TransceiverRequest> initializedQueue = getQueue();
        try {
            initializedQueue.add(message);
        } catch (IllegalStateException e) {
            logger.error("IllegalStateException adding to queue", e);
        } catch (Exception e) {
            logger.error("Exception adding to queue", e);
        }
    }

    public void shutdown() {
        stop = true;
    }

    public boolean stayRunning() {
        return !stop;
    }

    public void sendMessages() {
        boolean run = true;
        ArrayBlockingQueue<TransceiverRequest> queue = getQueue();
        while (run) {
            try {
                TransceiverRequest transceiverRequest = queue.take();
                statisticsAccessor.markLastHeartBeatTime2();
                send(transceiverRequest);
                run = stayRunning();
            } catch (InterruptedException e) {
                logger.error("Error sending message from queue", e);
            }
        }
    }

    public void send(TransceiverRequest objRequest) {
        if ((objRequest == null) || (objRequest.getTransceiverId() == null)) {
            return;
        }

        if (!objRequest.getTransceiverId().equals(registeredTransceiverAccessor.getRegisteredTransceiverId())) {
            return;
        }

        if (objRequest.getCallsignFrom() == null) {
            objRequest.setCallsignFrom(aprsConfiguration.getCallsign());
        }

        if (objRequest instanceof TransceiverMessage) {
            TransceiverMessage obj = (TransceiverMessage) objRequest;
            if (obj.isBulletin()) {
                aprsMessageAccessor.sendBulletin(obj.getCallsignFrom(), obj.getCallsignTo(), obj.getMessage());
            } else {
                aprsMessageAccessor.sendMessage(obj.getCallsignFrom(), obj.getCallsignTo(), obj.getMessage(), obj.isAckRequested());
            }
        } else if (objRequest instanceof TransceiverMessageMany) {
            TransceiverMessageMany objMany = (TransceiverMessageMany) objRequest;
            for (String message : objMany.getMessages()) {
                aprsMessageAccessor.sendMessage(objMany.getCallsignFrom(), objMany.getCallsignTo(), message, objMany.isAckRequested());
            }
        } else if (objRequest instanceof TransceiverObject) {
            TransceiverObject obj = (TransceiverObject) objRequest;
            aprsMessageAccessor.sendObject(obj.getName(), obj.getMessage(), obj.isAlive(), obj.getLat(), obj.getLon());
        } else if (objRequest instanceof TransceiverQuery) {
            TransceiverQuery obj = (TransceiverQuery) objRequest;
            aprsMessageAccessor.sendQuery(obj.getCallsignFrom(), obj.getCallsignTo(), obj.getQueryType());
        } else if (objRequest instanceof TransceiverReport) {
            TransceiverReport obj = (TransceiverReport) objRequest;
            aprsMessageAccessor.sendReport(obj.getCallsignFrom(), obj.getReport());
        }
    }
}
