package netcentral.transceiver.kiss.accessor;

import java.time.ZonedDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.kc1vmz.netcentral.aprsobject.common.TransceiverStatistics;

import jakarta.inject.Singleton;

@Singleton
public class StatisticsAccessor {
    private TransceiverStatistics statistics;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();


    public TransceiverStatistics get() {
        TransceiverStatistics statisticsRet = null;
        readLock.lock();
        try {
            if (statistics != null) {
                statisticsRet = new TransceiverStatistics(statistics);
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
                if (statisticsRet.getLastHeartBeatTime3() != null) {
                    long secondsSinceHeartbeat3 = now.toEpochSecond() - statisticsRet.getLastHeartBeatTime3().toEpochSecond();
                    statisticsRet.setLastHeartBeatSecondsSince3(secondsSinceHeartbeat3);
                }
                if (statisticsRet.getLastHeartBeatTime4() != null) {
                    long secondsSinceHeartbeat4 = now.toEpochSecond() - statisticsRet.getLastHeartBeatTime4().toEpochSecond();
                    statisticsRet.setLastHeartBeatSecondsSince4(secondsSinceHeartbeat4);
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
            }
            statistics.setAcksSent(statistics.getAcksSent()+1);
        } finally {
            writeLock.unlock();
        }
    }

    public void markLastHeartBeatTime1() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
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
                statistics = new TransceiverStatistics();
            }
            statistics.setLastHeartBeatName4(val);
        } finally {
            writeLock.unlock();
        }
    }
    public void incrementMessagesSent() {
        writeLock.lock();
        try {
            if (statistics == null) {
                statistics = new TransceiverStatistics();
            }
            statistics.setMessagesSent(statistics.getMessagesSent()+1);
        } finally {
            writeLock.unlock();
        }
    }
}
