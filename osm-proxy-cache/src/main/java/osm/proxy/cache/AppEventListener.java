package osm.proxy.cache;


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

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import osm.proxy.cache.accessor.TileAccessor;
import osm.proxy.cache.accessor.TilePrecacheQueue;
import osm.proxy.cache.objects.TileCacheRequest;


@Singleton
public class AppEventListener implements ApplicationEventListener<StartupEvent> {
    private static final Logger logger = LogManager.getLogger(AppEventListener.class);

    @Inject
    private TilePrecacheQueue tilePrecacheQueue;
    @Inject
    private TileAccessor tileAccessor;

    @Override
    public void onApplicationEvent(StartupEvent event) {
        startPreCacheThread();
    }

    private void startPreCacheThread() {
        new Thread(null, () -> {
            while (true) {
                boolean run = true;
                boolean shutdownok = false;
                ArrayBlockingQueue<TileCacheRequest> queue = tilePrecacheQueue.getQueue();
                TileCacheRequest tileRequest = null;
                while (run) {
                    try {
                        shutdownok = false;
                        tileRequest = queue.take();
                        getTile(tileRequest);
                        run = tilePrecacheQueue.stayRunning();
                        if (!run) {
                            // if told to shutdown, that's ok
                            shutdownok = true;
                        }
                    } catch (InterruptedException e) {
                        logger.error("InterruptedException caught precaching tiles", e);
                        run = false;
                        shutdownok = true;
                    } catch (Exception e) {
                        logger.error("Exception caught precaching tiles", e);
                    }
                }
                if (!shutdownok) { 
                    logger.error("Unexpected end of processing loop - go again");
                    if (tileRequest != null) {
                        logger.error(String.format("Tile in question: lat=%f lon=%f", tileRequest.getLat(), tileRequest.getLon()));
                    }
                } else {
                    logger.info("End of processing loop");
                    break;
                }
            }
        }, "TilePrecacheThread").start();
    }

    private void getTile(TileCacheRequest tileRequest) {
        double pi = 3.141526;
        for (int z = 3; z < 15; z++) {
            double x = 0;
            double y = 0;
            x = Math.floor( ((tileRequest.getLon() + 180) / 360) * Math.pow(2,z) );
            y = Math.floor( (1 - (Math.log( Math.tan(tileRequest.getLat()*(pi/180)) + (1 / (Math.cos(tileRequest.getLat()*pi/180))))) / pi) * (Math.pow(2, z-1)) );

            int xInt = Integer.parseInt(""+Math.round(x));
            int yInt = Integer.parseInt(""+Math.round(y));
            tileAccessor.fetch(""+xInt, ""+yInt, ""+z);
        }
    }

}
