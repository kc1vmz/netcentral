package netcentral.transceiver.kenwood.accessor;

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

import com.kc1vmz.netcentral.aprsobject.common.TransceiverReport;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kenwood.config.QueueConfiguration;


@Singleton
public class SendReportQueueAccessor {
    private static final Logger logger = LogManager.getLogger(SendReportQueueAccessor.class);

    @Inject
    private APRSMessageAccessor aprsMessageAccessor;
    @Inject
    private QueueConfiguration queueConfiguration;
    @Inject
    private StatisticsAccessor statisticsAccessor;

    private boolean stop = false;
    private ArrayBlockingQueue<TransceiverReport> queue = null;

    private void initializeQueue() {
        if (queue == null) {
            queue = new ArrayBlockingQueue<>(queueConfiguration.getQueueSendReportHandlerSize());
        }
    }
    public synchronized ArrayBlockingQueue<TransceiverReport> getQueue() {
        initializeQueue();
        return queue;
    }    
    public void queueSendReport(TransceiverReport message) {
        if (!stayRunning()) {
            return;
        }
        ArrayBlockingQueue<TransceiverReport> initializedQueue = getQueue();
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

    public void sendReports() {
        boolean run = true;
        ArrayBlockingQueue<TransceiverReport> queue = getQueue();
        while (run) {
            try {
                TransceiverReport transceiverReport = queue.take();
                statisticsAccessor.markLastHeartBeatTime4();
                send(transceiverReport);
                run = stayRunning();
            } catch (InterruptedException e) {
                logger.error("Error sending reports from queue", e);
                run = false;
            } catch (Exception e) {
                logger.error("Error sending reports from queue", e);
            }
        }
    }

    public void send(TransceiverReport obj) {
        logger.info(String.format("Report received for %s: %s", obj.getReport().getObjectName(), obj.getReport().getReportData()));
        aprsMessageAccessor.sendReport(obj.getCallsignFrom(), obj.getReport());
    }
}
