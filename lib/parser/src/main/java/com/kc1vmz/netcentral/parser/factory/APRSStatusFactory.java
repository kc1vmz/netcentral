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

import java.time.ZonedDateTime;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.APRSTimeConversionException;
import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSStatus;
import com.kc1vmz.netcentral.parser.util.APRSTime;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSStatusFactory {
    private static final Logger logger = LogManager.getLogger(APRSStatusFactory.class);


    public static APRSStatus parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSStatus object");
        APRSStatus ret = new APRSStatus();
        ret.setHeader(header);
        ret.setDti(data[0]);
        ret.setData(data);
        if ((data == null) || (data.length < 1)) {
            throw new ParserException("Not an APRSStatus object");
        }
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        int statusTextIndex = 1;
        byte [] dhmTime_bytes = Arrays.copyOfRange(data, statusTextIndex, statusTextIndex+7);
        String dhmTime = new String(dhmTime_bytes);
        if (APRSTime.isTime(dhmTime)) {
            statusTextIndex += dhmTime.length();
            ret.setTime(dhmTime);
            try {
                ret.setLdtime(APRSTime.convertAPRSTimeToZonedDateTime(dhmTime));
            } catch (APRSTimeConversionException e) {
                logger.error(String.format("Cannot convert %s to LocalDateTime", dhmTime));
            }
        } else {
            ret.setLdtime(ZonedDateTime.now());
        }
        byte [] status_bytes = Arrays.copyOfRange(data, statusTextIndex, data.length);
        String status = new String(status_bytes);
        ret.setStatus(status);

        return ret;
    }

}
