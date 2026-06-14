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

import java.time.ZonedDateTime;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import osm.proxy.cache.config.ProxyConfig;
import osm.proxy.cache.objects.Statistics;
import osm.proxy.cache.record.TileRecord;
import osm.proxy.cache.repository.TileRepository;

@Singleton
public class TileDatabaseAccessor {
    private static final Logger logger = LogManager.getLogger(TileDatabaseAccessor.class);
    @Inject
    private ProxyConfig proxyConfig;
    @Inject
    private TileRepository tileRepository;

    private String generateTileKey(String x, String y, String z) {
        return String.format("%s-%s-%s", x, y, z);
    }

    public byte [] fetch(String x, String y, String z, ZonedDateTime now) {
        String tileKey = generateTileKey(x, y, z);

        Optional<TileRecord> recordOpt = tileRepository.findById(tileKey);
        if (recordOpt.isEmpty()) {
            // not present
            return null;
        }
        TileRecord record = recordOpt.get();
        ZonedDateTime expirationTime = now.minusDays(proxyConfig.getOsmRefreshTileDays());
        if (record.fetch_time().isBefore(expirationTime)) {
            // it is old - purge this now
            tileRepository.delete(record);
            logger.info("Purging aged tile key "+tileKey);
            return null;
        }

        // return valid data
        return record.data();
    }

    public void save(String x, String y, String z, ZonedDateTime now, byte[] fileContent) {
        Integer xn = Integer.parseInt(x);
        Integer yn = Integer.parseInt(y);
        Integer zn = Integer.parseInt(z);
        String tileKey = generateTileKey(x, y, z);
        TileRecord record = new TileRecord(tileKey, xn, yn, zn, fileContent, now);

        try {
            TileRecord saved = tileRepository.save(record);
            if (saved != null) {
                logger.error(String.format("Error writing tile key %s", tileKey));
            }
        } catch (Exception e) {
            logger.error(String.format("Exception caught writing tile key %s", tileKey), e);
        }
    }

    public Statistics getStatistics() {
        Statistics ret = new Statistics();

        ret.setTileCount(tileRepository.count());

        return ret;
    }

}
