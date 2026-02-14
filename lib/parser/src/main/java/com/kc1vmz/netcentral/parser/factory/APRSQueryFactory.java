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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.constants.APRSQueryType;
import com.kc1vmz.netcentral.aprsobject.object.APRSQuery;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSQueryFactory {
    private static final Logger logger = LogManager.getLogger(APRSQueryFactory.class);


    public static APRSQuery parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSQuery object");
        APRSQuery ret = new APRSQuery();

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);
        String message = new String(data);
        if (!message.startsWith(APRSQueryType.PREFIX)) {
            throw new ParserException("Not a valid query packet");
        }
        message = message.substring(1);
        int index = message.indexOf(APRSQueryType.PREFIX);
        if (index == -1) {
            throw new ParserException("Not a valid query packet");
        }
        String queryType = message.substring(0, index);
        ret.setQueryType(queryType);
        message = message.substring(index+1);

        if (message.length() > 0) {
            String [] values = message.split(",");
            if ((values != null) && (values.length == 3)) {
                ret.setLat(values[0]);
                ret.setLon(values[1]);
                ret.setRadius(Integer.parseInt(values[2]));
            }
        }

        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        return ret;
    }
}
