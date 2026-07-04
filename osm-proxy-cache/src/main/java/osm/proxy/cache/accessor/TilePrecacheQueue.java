package osm.proxy.cache.accessor;

/*
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import osm.proxy.cache.config.ProxyConfig;
import osm.proxy.cache.objects.TileCacheRequest;

@Singleton
public class TilePrecacheQueue {

    private static final Logger logger = LogManager.getLogger(TilePrecacheQueue.class);

    @Inject
    private ProxyConfig proxyConfig;

    private ArrayBlockingQueue<TileCacheRequest> queue = null;
    private boolean stop = false;

    private void initializeQueue() {
        if (queue == null) {
            queue = new ArrayBlockingQueue<>(proxyConfig.getOsmPrecacheQueueSize());
        }
    }
    public synchronized ArrayBlockingQueue<TileCacheRequest> getQueue() {
        initializeQueue();
        return queue;
    }
    public synchronized void addRequest(TileCacheRequest obj) {
        if (!stayRunning()) {
            return;
        }
        ArrayBlockingQueue<TileCacheRequest> initializedQueue = getQueue();
        try {
            initializedQueue.add(obj);
        } catch (IllegalStateException e) {
            logger.error("IllegalStateException adding to queue", e);
            if ((e.getMessage() != null) && (!(e.getMessage().contains("queue")))) {
                // queue not full - shutdown
                shutdown();
            }
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
