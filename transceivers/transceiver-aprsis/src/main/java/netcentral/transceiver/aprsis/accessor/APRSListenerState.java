package netcentral.transceiver.aprsis.accessor;

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
