package com.kc1vmz.netcentral.parser.util;

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
