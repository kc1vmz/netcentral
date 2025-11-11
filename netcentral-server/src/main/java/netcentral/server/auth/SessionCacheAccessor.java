package netcentral.server.auth;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Singleton;
import netcentral.server.object.User;


@Singleton
public class SessionCacheAccessor {
    private static final Logger logger = LogManager.getLogger(SessionCacheAccessor.class);

    private Map<String, String> sessionIdUserIdCacheMap = null;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();

    public void add(String sessionId, User user) {
        add(sessionId, user.getId());
    }

    public String add(User user) {
        String sessionId = String.format("%s|%s",UUID.randomUUID().toString(), LocalDateTime.now().toString());
        add(sessionId, user.getId());
        return sessionId;
    }

    public void add(String sessionId, String userId) {
        // add answer to cache
        logger.debug(String.format("Add session %s for userId %s", sessionId, userId));
        writeLock.lock();
        try {
            if (sessionIdUserIdCacheMap == null) {
                sessionIdUserIdCacheMap = new HashMap<String, String>();
            }
            sessionIdUserIdCacheMap.put(sessionId, userId);
        } finally {
            writeLock.unlock();
        }
    }
    public void remove(User user) {
        if (user.getAccessToken() != null) {
            remove(user.getAccessToken());
        }
    }

    public void remove(String sessionId) {
        logger.debug(String.format("Remove session %s", sessionId));
        writeLock.lock();
        try {
            if (sessionIdUserIdCacheMap == null) {
                sessionIdUserIdCacheMap = new HashMap<String, String>();
                return;
            }
            sessionIdUserIdCacheMap.remove(sessionId);
        } finally {
            writeLock.unlock();
        }
    }

    public User findUser(String sessionId) {
        if ((sessionId == null) || (sessionId.isBlank()))   {
            return null;
        }
        String userId = find(sessionId);
        if (userId == null) {
            return null;
        }

        User user = new User();
        user.setAccessToken(sessionId);
        user.setId(userId);
        return user;
    }

    public String find(String sessionId) {
        String ret = null;
        readLock.lock();
        try {
            if (sessionIdUserIdCacheMap == null) {
                return null;
            }
            ret = sessionIdUserIdCacheMap.get(sessionId);
        } finally {
            readLock.unlock();
        }
        return ret;
    }

    public Integer sessions() {
        Integer ret = 0;
        readLock.lock();
        try {
            if (sessionIdUserIdCacheMap != null) {
                ret = sessionIdUserIdCacheMap.size();
            }
        } finally {
            readLock.unlock();
        }
        return ret;
    }

}
