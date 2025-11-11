package netcentral.transceiver.kiss.accessor;

import java.util.concurrent.ArrayBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessage;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessageMany;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kiss.config.QueueConfiguration;


@Singleton
public class SendMessageQueueAccessor {
    private static final Logger logger = LogManager.getLogger(SendMessageQueueAccessor.class);

    @Inject
    private APRSMessageAccessor aprsMessageAccessor;
    @Inject
    private QueueConfiguration queueConfiguration;
    @Inject
    private StatisticsAccessor statisticsAccessor;

    private boolean stop = false;
    private ArrayBlockingQueue<TransceiverMessage> queue = null;

    private void initializeQueue() {
        if (queue == null) {
            queue = new ArrayBlockingQueue<>(queueConfiguration.getQueueSendMessageHandlerSize());
        }
    }
    public synchronized ArrayBlockingQueue<TransceiverMessage> getQueue() {
        initializeQueue();
        return queue;
    }    

    public void queueSendMessage(TransceiverMessage message) {
        if (!stayRunning()) {
            return;
        }
        ArrayBlockingQueue<TransceiverMessage> initializedQueue = getQueue();
        try {
            initializedQueue.add(message);
        } catch (IllegalStateException e) {
            logger.error("IllegalStateException adding to queue", e);
        } catch (Exception e) {
            logger.error("Exception adding to queue", e);
        }
    }

    public void queueSendMessageMany(TransceiverMessageMany obj) {
        for (String message : obj.getMessages()) {
            TransceiverMessage msg = new TransceiverMessage();
            msg.setBulletin(false);
            msg.setCallsignFrom(obj.getCallsignFrom());
            msg.setCallsignTo(obj.getCallsignTo());
            msg.setMessage(message);
            queueSendMessage(msg);
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
        ArrayBlockingQueue<TransceiverMessage> queue = getQueue();
        while (run) {
            try {
                TransceiverMessage TransceiverMessage = queue.take();
                statisticsAccessor.markLastHeartBeatTime2();
                send(TransceiverMessage);
                run = stayRunning();
            } catch (InterruptedException e) {
                logger.error("Error sending message from queue", e);
            }
        }
    }

    public void send(TransceiverMessage obj) {
        if (obj.isBulletin()) {
            logger.info(String.format("Bulletin received from %s to %s: %s", obj.getCallsignFrom(), obj.getCallsignTo(), obj.getMessage()));
            aprsMessageAccessor.sendBulletin(obj.getCallsignFrom(), obj.getCallsignTo(), obj.getMessage());
        } else {
            logger.info(String.format("Message received from %s to %s: %s", obj.getCallsignFrom(), obj.getCallsignTo(), obj.getMessage()));
            aprsMessageAccessor.sendMessage(obj.getCallsignFrom(), obj.getCallsignTo(), obj.getMessage(), obj.isAckRequested());
        }
    }
}
