package netcentral.server.auth;

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
