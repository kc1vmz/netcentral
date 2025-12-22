package netcentral.server.utils;

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
