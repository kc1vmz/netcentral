package osm.proxy.cache.config;

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

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class ProxyConfig {
    @Value("${osm.endpoint.format}")
    private String osmEndpointFormat;
    @Value("${osm.refresh.tile.days}")
    private int osmRefreshTileDays;
    @Value("${osm.config}")
    private String mode;
    @Value("${osm.precache.queue.size}")
    private int osmPrecacheQueueSize;

    public static final String MODE_PROXY = "proxy";  // only pass through to OSM server, no not cache
    public static final String MODE_PROXYCACHE = "proxycache";  // get and cache
    public static final String MODE_CACHE = "cache";  // only use the local cache

    public String getOsmEndpointFormat() {
        return osmEndpointFormat;
    }
    public void setOsmEndpointFormat(String osmEndpointFormat) {
        this.osmEndpointFormat = osmEndpointFormat;
    }
    public int getOsmRefreshTileDays() {
        return osmRefreshTileDays;
    }
    public void setOsmRefreshTileDays(int osmRefreshTileDays) {
        this.osmRefreshTileDays = osmRefreshTileDays;
    }
    public String getMode() {
        return mode;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }
    public int getOsmPrecacheQueueSize() {
        return osmPrecacheQueueSize;
    }
    public void setOsmPrecacheQueueSize(int osmPrecacheQueueSize) {
        this.osmPrecacheQueueSize = osmPrecacheQueueSize;
    }
}
