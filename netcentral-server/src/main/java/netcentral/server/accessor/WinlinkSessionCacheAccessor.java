package netcentral.server.accessor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Singleton;
import netcentral.server.object.WinlinkSessionCacheEntry;


@Singleton
public class WinlinkSessionCacheAccessor {
    private static final Logger logger = LogManager.getLogger(WinlinkSessionCacheAccessor.class);

    private Map<String, WinlinkSessionCacheEntry> callsignCacheEntryMap = null;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();
    private Integer totalSessions = 0;

    public boolean add(String callsign, WinlinkSessionCacheEntry entry) {
        boolean added = false;
        // add entry to cache
        logger.debug(String.format("Add callsign %s for recipient %s", callsign, entry.getRecipient()));
        writeLock.lock();
        try {
            if (callsignCacheEntryMap == null) {
                callsignCacheEntryMap = new HashMap<String, WinlinkSessionCacheEntry>();
            }

            WinlinkSessionCacheEntry existingEntry = callsignCacheEntryMap.get(callsign);
            if (existingEntry == null) {
                callsignCacheEntryMap.put(callsign, entry);
                totalSessions++;
                added = true;
            }
        } finally {
            writeLock.unlock();
        }
        return added;
    }

    public void remove(String callsign) {
        logger.debug(String.format("Remove callsign %s", callsign));
        writeLock.lock();
        try {
            if (callsignCacheEntryMap == null) {
                callsignCacheEntryMap = new HashMap<String, WinlinkSessionCacheEntry>();
                return;
            }
            callsignCacheEntryMap.remove(callsign);
        } finally {
            writeLock.unlock();
        }
    }

    public WinlinkSessionCacheEntry update(String callsign, WinlinkSessionCacheEntry entry) {
        logger.debug(String.format("Update callsign %s", callsign));
        writeLock.lock();
        try {
            if (callsignCacheEntryMap == null) {
                return null;
            }

            WinlinkSessionCacheEntry cacheEntry = callsignCacheEntryMap.get(callsign);
            if ((cacheEntry != null) && (cacheEntry.getCallsign().equals(entry.getCallsign())) && (cacheEntry.getRecipient().equals(entry.getRecipient()))) {
                // if this message is for the same recipient and from the same callsign
                cacheEntry.setPassword(entry.getPassword());
                cacheEntry.setSixDigitResponse(entry.getSixDigitResponse());
                cacheEntry.setState(entry.getState());
                cacheEntry.setThreeDigitChallenge(entry.getThreeDigitChallenge());
                return new WinlinkSessionCacheEntry(cacheEntry);
            }
        } catch (Exception e) {
        } finally {
            writeLock.unlock();
        }
        return null;
    }

    public WinlinkSessionCacheEntry find(String callsign) {
        WinlinkSessionCacheEntry ret = null;
        readLock.lock();
        try {
            if (callsignCacheEntryMap == null) {
                return null;
            }
            // return a copy
            ret = new WinlinkSessionCacheEntry(callsignCacheEntryMap.get(callsign));
        } finally {
            readLock.unlock();
        }
        return ret;
    }

    public Integer outstandingSessions() {
        Integer ret = 0;
        readLock.lock();
        try {
            if (callsignCacheEntryMap != null) {
                ret = callsignCacheEntryMap.size();
            }
        } finally {
            readLock.unlock();
        }
        return ret;
    }

    public Integer totalSessions() {
        Integer ret = 0;
        readLock.lock();
        try {
            ret = totalSessions;
        } finally {
            readLock.unlock();
        }
        return ret;
    }
}
