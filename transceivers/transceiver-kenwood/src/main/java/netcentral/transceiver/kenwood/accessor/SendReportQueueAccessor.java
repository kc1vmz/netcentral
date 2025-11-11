package netcentral.transceiver.kenwood.accessor;

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
