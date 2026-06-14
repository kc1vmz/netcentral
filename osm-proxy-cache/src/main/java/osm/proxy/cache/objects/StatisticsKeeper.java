package osm.proxy.cache.objects;

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

import jakarta.inject.Singleton;

@Singleton
public class StatisticsKeeper {
    private long requests;
    private long cacheHits;
    private long cacheMisses;

    public long getRequests() {
        return requests;
    }
    public void setRequests(long requests) {
        this.requests = requests;
    }
    public synchronized void incrementRequests() {
        this.requests++;
    }
    public long getCacheHits() {
        return cacheHits;
    }
    public void setCacheHits(long cacheHits) {
        this.cacheHits = cacheHits;
    }
    public synchronized void incrementCacheHits() {
        this.cacheHits++;
    }
    public long getCacheMisses() {
        return cacheMisses;
    }
    public void setCacheMisses(long cacheMisses) {
        this.cacheMisses = cacheMisses;
    }
    public synchronized void incrementCacheMisses() {
        this.cacheMisses++;
    }
}
