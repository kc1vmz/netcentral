package netcentral.server.accessor;

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

import java.time.ZonedDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.kc1vmz.netcentral.aprsobject.common.NetCentralServerStatistics;

import jakarta.inject.Singleton;

@Singleton
public class StatisticsAccessor {
    private NetCentralServerStatistics statistics;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();


    public NetCentralServerStatistics get() {
        NetCentralServerStatistics statisticsRet = null;
        readLock.lock();
        try {
            if (statistics != null) {
                statisticsRet = new NetCentralServerStatistics(statistics);
                // set the uptime minutes
                ZonedDateTime now = ZonedDateTime.now();
                long minutes = (long) ((now.toEpochSecond() - statisticsRet.getStartTime().toEpochSecond()) / 60);
                statisticsRet.setStartTimeMinutes(minutes);
                if (statisticsRet.getLastHeartBeatTime1() != null) {
                    long secondsSinceHeartbeat1 = now.toEpochSecond() - statisticsRet.getLastHeartBeatTime1().toEpochSecond();
                    statisticsRet.setLastHeartBeatSecondsSince1(secondsSinceHeartbeat1);
                }
                if (statisticsRet.getLastHeartBeatTime2() != null) {
                    long secondsSinceHeartbeat2 = now.toEpochSecond() - statisticsRet.getLastHeartBeatTime2().toEpochSecond();
                    statisticsRet.setLastHeartBeatSecondsSince2(secondsSinceHeartbeat2);
                }

                if (statisticsRet.getLastReceivedTime() != null) {
                    long lastReceivedSecondsSince = now.toEpochSecond() - statisticsRet.getLastReceivedTime().toEpochSecond();
                    statisticsRet.setLastReceivedSecondsSince(lastReceivedSecondsSince);
                }
                if (statisticsRet.getLastSentTime() != null) {
                    long lastSentSecondsSince = now.toEpochSecond() - statisticsRet.getLastSentTime().toEpochSecond();
                    statisticsRet.setLastSentSecondsSince(lastSentSecondsSince);
                }
            }
        } finally {
            readLock.unlock();
        }
        return statisticsRet;
    }

    public void markStartTime() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setStartTime(ZonedDateTime.now());
        } finally {
            writeLock.unlock();
        }
    }
    public void incrementObjectsReceived() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setObjectsReceived(statistics.getObjectsReceived()+1);
        } finally {
            writeLock.unlock();
        }
    }
    public void incrementObjectsSent() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setObjectsSent(statistics.getObjectsSent()+1);
        } finally {
            writeLock.unlock();
        }
    }


    public void markLastReceivedTime() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setLastReceivedTime(ZonedDateTime.now());
        } finally {
            writeLock.unlock();
        }
    }
    public void markLastSentTime() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setLastSentTime(ZonedDateTime.now());
        } finally {
            writeLock.unlock();
        }
    }
    public void incrementAcksRequested() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setAcksRequested(statistics.getAcksRequested()+1);
        } finally {
            writeLock.unlock();
        }
    }
    public void incrementAcksSent() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setAcksSent(statistics.getAcksSent()+1);
        } finally {
            writeLock.unlock();
        }
    }
    public void incrementRejsSent() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setRejsSent(statistics.getRejsSent()+1);
        } finally {
            writeLock.unlock();
        }
    }

    public void markLastHeartBeatTime1() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setLastHeartBeatTime1(ZonedDateTime.now());
        } finally {
            writeLock.unlock();
        }
    }
    public void markLastHeartBeatTime2() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setLastHeartBeatTime2(ZonedDateTime.now());
        } finally {
            writeLock.unlock();
        }
    }
    public void markLastHeartBeatTime3() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setLastHeartBeatTime3(ZonedDateTime.now());
        } finally {
            writeLock.unlock();
        }
    }
    public void markLastHeartBeatTime4() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setLastHeartBeatTime4(ZonedDateTime.now());
        } finally {
            writeLock.unlock();
        }
    }


    public void setLastHeartBeatName1(String val) {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setLastHeartBeatName1(val);
        } finally {
            writeLock.unlock();
        }
    }
    public void setLastHeartBeatName2(String val) {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setLastHeartBeatName2(val);
        } finally {
            writeLock.unlock();
        }
    }
    public void setLastHeartBeatName3(String val) {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setLastHeartBeatName3(val);
        } finally {
            writeLock.unlock();
        }
    }
    public void setLastHeartBeatName4(String val) {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setLastHeartBeatName4(val);
        } finally {
            writeLock.unlock();
        }
    }

    public void incrementNetsStarted() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setNetsStarted(statistics.getNetsStarted()+1);
        } finally {
            writeLock.unlock();
        }
    }
    public void incrementNetsClosed() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setNetsClosed(statistics.getNetsClosed()+1);
        } finally {
            writeLock.unlock();
        }
    }

    public void incrementUserLogins() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setUserLogins(statistics.getUserLogins()+1);
        } finally {
            writeLock.unlock();
        }
    }
    public void incrementUserLogouts() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setUserLogouts(statistics.getUserLogouts()+1);
        } finally {
            writeLock.unlock();
        }
    }
    public void incrementMessagesSent() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setMessagesSent(statistics.getMessagesSent()+1);
        } finally {
            writeLock.unlock();
        }
    }
    public void incrementReportsSent() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setReportsSent(statistics.getReportsSent()+1);
        } finally {
            writeLock.unlock();
        }
    }
    public void setOutstandingObjects(long value) {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new NetCentralServerStatistics();
            }
            statistics.setOutstandingObjects(value);
        } finally {
            writeLock.unlock();
        }
    }
}
