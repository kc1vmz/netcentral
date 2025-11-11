package netcentral.server.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Singleton;


@Singleton
public class SessionTrackedObjectAccessor {
    private static final Logger logger = LogManager.getLogger(SessionTrackedObjectAccessor.class);

    private Map<String, Boolean> sessionIdTrackedOnlyCacheMap = null;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();

    public void add(String sessionId, boolean track) {
        remove(sessionId);

        logger.debug(String.format("Adding session %s", sessionId));
        writeLock.lock();
        try {
            if (sessionIdTrackedOnlyCacheMap == null) {
                sessionIdTrackedOnlyCacheMap = new HashMap<String, Boolean>();
                return;
            }
            sessionIdTrackedOnlyCacheMap.put(sessionId, track);
        } finally {
            writeLock.unlock();
        }
    }

    public void remove(String sessionId) {
        logger.debug(String.format("Remove session %s", sessionId));
        writeLock.lock();
        try {
            if (sessionIdTrackedOnlyCacheMap == null) {
                sessionIdTrackedOnlyCacheMap = new HashMap<String, Boolean>();
                return;
            }
            sessionIdTrackedOnlyCacheMap.remove(sessionId);
        } finally {
            writeLock.unlock();
        }
    }

    public Boolean isTrackingOnly(String sessionId) {
        Boolean ret = false;
        readLock.lock();
        try {
            if (sessionIdTrackedOnlyCacheMap == null) {
                return false;
            }
            ret = sessionIdTrackedOnlyCacheMap.get(sessionId);
        } catch (Exception e) {
        } finally {
            readLock.unlock();
        }
        if (ret == null) {
            ret = false;
        }
        return ret;
    }
}
