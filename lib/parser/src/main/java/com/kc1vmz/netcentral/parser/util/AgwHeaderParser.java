package com.kc1vmz.netcentral.parser.util;

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

import com.kc1vmz.netcentral.parser.exception.ParserException;

public class AgwHeaderParser {
    private static final int LENGTH_BYTES = 36;

    public static String getCallsignFrom(byte [] data) throws NullPointerException, ParserException {
        return getCallsign(data, 8);
    }
    public static String getCallsignTo(byte [] data) throws NullPointerException, ParserException {
        return getCallsign(data, 18);
    }
    private static String getCallsign(byte [] data, int offset) throws NullPointerException, ParserException {
        if (data == null) {
            throw new NullPointerException();
        }
        if (data.length < LENGTH_BYTES) {
            throw new ParserException("Header too small");
        }

        String callsign = "";
        for (int i = 0; i < 10; i++) {
            if (data[offset+i] != 0) {
                callsign += (char) data[offset+i];
            }
        }
        return callsign;
    }
}
