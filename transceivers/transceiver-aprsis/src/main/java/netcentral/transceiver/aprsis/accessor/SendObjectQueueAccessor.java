package netcentral.transceiver.aprsis.accessor;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.TransceiverObject;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.aprsis.config.APRSConfiguration;
import netcentral.transceiver.aprsis.config.QueueConfiguration;


@Singleton
public class SendObjectQueueAccessor {
    private static final Logger logger = LogManager.getLogger(SendObjectQueueAccessor.class);
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
    private ArrayBlockingQueue<TransceiverObject> queue = null;

    private void initializeQueue() {
        if (queue == null) {
            queue = new ArrayBlockingQueue<>(queueConfiguration.getQueueSendObjectHandlerSize());
        }
    }
    public synchronized ArrayBlockingQueue<TransceiverObject> getQueue() {
        initializeQueue();
        return queue;
    }    

    public void queueSendObject(TransceiverObject message) {
        if (!stayRunning()) {
            return;
        }
        ArrayBlockingQueue<TransceiverObject> initializedQueue = getQueue();
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

    public void sendObjects() {
        boolean run = true;
        ArrayBlockingQueue<TransceiverObject> queue = getQueue();
        while (run) {
            try {
                TransceiverObject transceiverObject = queue.take();
                statisticsAccessor.markLastHeartBeatTime3();
                send(transceiverObject);
                run = stayRunning();
            } catch (InterruptedException e) {
                logger.error("Error sending reports from queue", e);
                run = false;
            } catch (Exception e) {
                logger.error("Error sending reports from queue", e);
            }
        }
    }

    public void send(TransceiverObject obj) {
        if ((obj == null) || (obj.getTransceiverId() == null)) {
            return;
        }

        if (!obj.getTransceiverId().equals(registeredTransceiverAccessor.getRegisteredTransceiverId())) {
            logger.warn("Wrong transceiver - " + obj.getTransceiverId());
            return;
        }

        if (obj.getCallsignFrom() == null) {
            obj.setCallsignFrom(aprsConfiguration.getUsername());
        }

        // need to send this object out via APRS
        logger.info(String.format("object received %s (%s): %s", obj.getName(), obj.isAlive() ? "Alive" : "Dead", obj.getMessage()));
        aprsMessageAccessor.sendObject(obj.getName(), obj.getMessage(), obj.isAlive(), obj.getLat(), obj.getLon());
    }
}
