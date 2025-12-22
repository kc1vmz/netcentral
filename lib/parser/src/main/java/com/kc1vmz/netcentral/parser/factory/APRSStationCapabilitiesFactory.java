package com.kc1vmz.netcentral.parser.factory;

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

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSStationCapabilities;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSStationCapabilitiesFactory {
    private static final Logger logger = LogManager.getLogger(APRSStationCapabilitiesFactory.class);


    public static APRSStationCapabilities parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSStationCapabilities object");
        APRSStationCapabilities ret = new APRSStationCapabilities();

        ret.setHeader(header);
        ret.setData(data);
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));
        ret.setDti(data[0]);

        Map<String, String> pairs = new HashMap<>();

        if (data != null) {
            String dataStr = new String(data);
            String [] values = dataStr.split(",");
            for (int i = 0; i < values.length; i++) {
                String [] kv = values[i].split("=");
                if (kv.length == 1) {
                    // just a key
                    pairs.put(kv[0], null);
                } else if (kv.length == 2) {
                    // k:v pair
                    pairs.put(kv[0], kv[1]);
                } else {
                    throw new ParserException("Capability invalid value");
                }
            }
        }

        ret.setValues(pairs);

        return ret;
    }

}
