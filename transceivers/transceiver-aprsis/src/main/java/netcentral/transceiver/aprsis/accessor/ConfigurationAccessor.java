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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.aprsis.config.APRSConfiguration;

@Singleton
public class ConfigurationAccessor {
    private static final Logger logger = LogManager.getLogger(ConfigurationAccessor.class);

    @Inject
    private APRSConfiguration aprsConfiguration;

    public Map<String, String> getConfigParameters() {
        Map<String, String> ret = new HashMap<>();
        ret.put("APRSQuery", aprsConfiguration.getQuery());
        return ret;
    }
    public void setConfigParameters( Map<String, String> values) {
        if (values == null) {
            logger.warn("No configuration parameters provided");
            return;
        }
        String query = values.get("APRSQuery");
        aprsConfiguration.setQuery(query);
    }
}
