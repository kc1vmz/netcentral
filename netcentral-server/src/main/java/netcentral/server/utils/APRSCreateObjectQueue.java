package netcentral.server.utils;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.APRSObjectResource;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.accessor.StatisticsAccessor;
import netcentral.server.config.NetConfigServerConfig;

@Singleton
public class APRSCreateObjectQueue {
    @Inject
    private NetConfigServerConfig netConfigServerConfig;
    @Inject
    private StatisticsAccessor statisticsAccessor;

    private static final Logger logger = LogManager.getLogger(APRSCreateObjectQueue.class);

    private ArrayBlockingQueue<APRSObjectResource> queue = null;
    private boolean stop = false;

    private void initializeQueue() {
        if (queue == null) {
            queue = new ArrayBlockingQueue<>(netConfigServerConfig.getQueueObjectHandlerSize());
        }
    }
    public synchronized ArrayBlockingQueue<APRSObjectResource> getQueue() {
        initializeQueue();
        return queue;
    }
    public synchronized void addAPRSObject(APRSObjectResource obj) {
        if (!stayRunning()) {
            return;
        }
        ArrayBlockingQueue<APRSObjectResource> initializedQueue = getQueue();
        try {
            initializedQueue.add(obj);
            int count = queue.size();
            statisticsAccessor.setOutstandingObjects(count);
        } catch (IllegalStateException e) {
            logger.error("IllegalStateException adding to queue", e);
            shutdown();
        } catch (Exception e) {
            logger.error("Exception adding to queue", e);
        }
    }
    public boolean stayRunning() {
        return !stop;
    }
    public void shutdown() {
        stop = true;
    }
}
