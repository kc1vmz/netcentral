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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import osm.proxy.cache.config.ProxyConfig;
import osm.proxy.cache.objects.Statistics;
import osm.proxy.cache.objects.StatisticsKeeper;
import osm.proxy.cache.objects.TileCacheRequest;
import osm.proxy.cache.utils.ConvertLonLat;

@Singleton
public class TileAccessor {
    private static final Logger logger = LogManager.getLogger(TileAccessor.class);
    private HttpClient client = null;

    @Inject
    private ProxyConfig proxyConfig;
    @Inject
    private TileDatabaseAccessor tileDatabaseAccessor;
    @Inject
    private StatisticsKeeper statisticsKeeper;
    @Inject
    private TilePrecacheQueue tilePrecacheQueue;


    public byte [] fetch(String x, String y, String z) {
        init();
        statisticsKeeper.incrementRequests();

        byte [] fileContent = null;
        ZonedDateTime now = ZonedDateTime.now();
        if (proxyConfig.getMode().equalsIgnoreCase(ProxyConfig.MODE_PROXYCACHE)) {
            // get from OSM server and cache
            fileContent = tileDatabaseAccessor.fetch(x, y, z, now);
            if (fileContent == null) {
                statisticsKeeper.incrementCacheMisses();
                fileContent = fetchBinaryFromOSM(x, y, z);
                if (fileContent != null) {
                    tileDatabaseAccessor.save(x, y, z, now, fileContent);
                }
            } else {
                statisticsKeeper.incrementCacheHits();
            }
        } else if (proxyConfig.getMode().equalsIgnoreCase(ProxyConfig.MODE_PROXY)) {
            // just get from OSM server as a passthrough - do not cache
            fileContent = fetchBinaryFromOSM(x, y, z);
        } else if (proxyConfig.getMode().equalsIgnoreCase(ProxyConfig.MODE_CACHE)) {
            // only use the cache
            fileContent = tileDatabaseAccessor.fetch(x, y, z, now);
            if (fileContent == null) {
                statisticsKeeper.incrementCacheMisses();
            } else {
                statisticsKeeper.incrementCacheHits();
            }
        }
        return fileContent;
    }

    private synchronized void init() {
        if (client == null) {
            client = HttpClient.newHttpClient();
        }
    }

    private byte [] fetchBinaryFromOSM(String x, String y, String z) {
        String fileUrlFmt = proxyConfig.getOsmEndpointFormat();
        byte [] ret = null;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(fileUrlFmt, z, x, y)))
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                ret = response.body();
            } else {
                String error = String.format("Failed to fetch tile (%s, %s, %s) from OSM server - HTTP Status: %d - %s", x, y, z, response.statusCode(),new String(response.body()));
                logger.warn(error);
            }

        } catch (IOException | InterruptedException e) {
            logger.error("Exception caught fetching tile from OSM server", e);
        }
        return ret;
    }

    public String updateMode(String mode) {
        if ((mode != null) && ((mode.equalsIgnoreCase(ProxyConfig.MODE_CACHE)) || (mode.equalsIgnoreCase(ProxyConfig.MODE_PROXY)) || (mode.equalsIgnoreCase(ProxyConfig.MODE_PROXYCACHE)))) {
            proxyConfig.setMode(mode);
        }
        return proxyConfig.getMode();
    }

    public String getMode() {
        return proxyConfig.getMode();
    }

    public Statistics getStatistics() {
        Statistics ret = new Statistics();

        Statistics dbRet = tileDatabaseAccessor.getStatistics();
        ret.setTileCount(dbRet.getTileCount());
        ret.setCacheHits(statisticsKeeper.getCacheHits());
        ret.setCacheMisses(statisticsKeeper.getCacheMisses());
        ret.setRequests(statisticsKeeper.getRequests());

        return ret;
    }

    public void cacheByLatLonDegrees(Float lat, Float lon) {
        if (proxyConfig.getMode().equalsIgnoreCase(ProxyConfig.MODE_PROXYCACHE)) {
            TileCacheRequest request = new TileCacheRequest(lat, lon);
            tilePrecacheQueue.addRequest(request);
        }
    }

    public void cacheByLatLonAPRS(String lat, String lon) {
        if (proxyConfig.getMode().equalsIgnoreCase(ProxyConfig.MODE_PROXYCACHE)) {
            double latD = ConvertLonLat.convertLatitude(lat);
            double lonD = ConvertLonLat.convertLongitude(lon);
            TileCacheRequest request = new TileCacheRequest((float) latD, (float) lonD);
            tilePrecacheQueue.addRequest(request);
        }
    }
}
