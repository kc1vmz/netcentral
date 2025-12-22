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

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSAgrelo;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSAgreloFactory {
    private static final Logger logger = LogManager.getLogger(APRSStatusFactory.class);

    public static APRSAgrelo parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSAgrelo object");
        APRSAgrelo ret = new APRSAgrelo();
        ret.setHeader(header);
        byte [] bearing_bytes =  Arrays.copyOfRange(data, 1, 4);
        ret.setBearing(Integer.parseInt(new String(bearing_bytes)));
        int quality = (char) data[4]-'0';
        ret.setQuality(quality);

        ret.setDti(data[0]);
        ret.setData( Arrays.copyOfRange(data, 5, data.length));
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        return ret;
    }

}
