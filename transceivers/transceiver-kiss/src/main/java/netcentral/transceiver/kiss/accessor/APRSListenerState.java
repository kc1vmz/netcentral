package netcentral.transceiver.kiss.accessor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Singleton;

@Singleton
public class APRSListenerState {
    private static final Logger logger = LogManager.getLogger(APRSListenerState.class);
    private boolean active = false;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();

    public boolean isActive() {
        boolean ret = false;
        readLock.lock();
        try {
            ret = active;
        } finally {
            readLock.unlock();
        }
        return ret;
    }

    public void setActive(boolean a) {
        writeLock.lock();
        try {
            active = a;
            if (a) {
                logger.debug("Setting APRS Listener state to true");
            } else {
                logger.debug("Setting APRS Listener state to false");
            }
        } finally {
            writeLock.unlock();
        }
    }
}
