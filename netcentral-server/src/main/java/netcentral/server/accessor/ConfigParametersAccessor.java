package netcentral.server.accessor;

import java.util.HashMap;
import java.util.Map;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import netcentral.server.object.User;

/* very low tech locking - not meant to be high performance */

@Singleton
public class ConfigParametersAccessor {
    private static final Logger logger = LogManager.getLogger(ConfigParametersAccessor.class);
    public static final String CONFIGPARAM_RAWPACKETLOGGING = "RawPacketLogging";

    private Map<String, String> kvPairs = null;

    public String getValue(User loggedInUser, String key) {
        String ret = "";
        if (key == null) {
            logger.debug("key not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "key not provided");
        }
        try {
            ret = getSetValue(false, key, null);
        } catch (Exception e) {
            logger.error("Exception caught getting value", e);
        }
        return ret;
    }

    public String setValue(User loggedInUser, String key, String value) {
        String ret = "";
        if (key == null) {
            logger.debug("key not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "key not provided");
        }

        try {
            ret = getSetValue(true, key, value);
        } catch (Exception e) {
            logger.error("Exception caught setting value", e);
        }
        return ret;
    }

    private synchronized String getSetValue(boolean set, String key, String value) {
        String ret = null;
        if (set) {
            if (kvPairs == null) {
                kvPairs = new HashMap<String, String>();
            }
            kvPairs.put(key, value);
            ret = value;
        } else {
            ret = kvPairs.get(key);
        }

        return ret;
    }

    public boolean isLogRawPackets() {
        boolean ret = false;
        String val = getValue(null, CONFIGPARAM_RAWPACKETLOGGING);
        if (val.equalsIgnoreCase("true")) {
            ret = true;
        }
        return ret;
    }

    public boolean setLogRawPackets(boolean value) {
        String valueStr = (value) ? "true" : "false";
        setValue(null, CONFIGPARAM_RAWPACKETLOGGING, valueStr); 
        return value;
    }
}