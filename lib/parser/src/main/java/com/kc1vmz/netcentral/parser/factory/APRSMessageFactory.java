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
import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;
import com.kc1vmz.netcentral.parser.util.Stripper;

public class APRSMessageFactory {
    private static final Logger logger = LogManager.getLogger(APRSMessageFactory.class);

    public static APRSMessage parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSMessage object");
        APRSMessage ret = new APRSMessage();

        ret.setHeader(header);
        ret.setDti(data[0]);
        ret.setData(data);
        if (data[0] != ':') {
            throw new ParserException("Invalid message data packet");
        }
        String callsignFromOriginal = AgwHeaderParser.getCallsignFrom(header);
        String callsignFrom = Stripper.stripWhitespace(callsignFromOriginal);
        ret.setCallsignFrom(callsignFrom);

        byte [] addressee_bytes = Arrays.copyOfRange(data, 1, 10);
        String addressee = new String(addressee_bytes);
        addressee = Stripper.stripWhitespace(addressee);

        ret.setGroupBulletin(false);
        ret.setBulletin(false);
        ret.setAnnouncement(false);
        ret.setNWSBulletin(false);

        ret.setAddressee(addressee);
        ret.setCallsignTo(addressee);

        if (addressee.startsWith("BLN")) {
            ret.setBulletin(true);
            if (callsignFromOriginal.length() > 3) {
                char typeId = callsignFromOriginal.charAt(3);
                if ((typeId >= '0') && (typeId <= '9')) {
                    if ((callsignFromOriginal.length() > 4) && (callsignFromOriginal.charAt(4) == ' ')) {
                        // normal bulletin
                    } else {
                        ret.setGroupBulletin(true);
                    }
                } else if ((typeId >= 'A') && (typeId <= 'Z')) {
                    ret.setAnnouncement(true);
                }
            }
        } else if ((addressee.startsWith("NWS-")) || (addressee.startsWith("NWS_"))) {
            ret.setNWSBulletin(true);
            if (callsignFromOriginal.length() > 4) {
                ret.setNWSLevel(callsignFromOriginal.substring(4));
            }
        }

        byte [] message_bytes = Arrays.copyOfRange(data, 11, data.length);
        String messageString = new String(message_bytes);
        ret.setQuery(false);
        if (messageString.startsWith("?")) {
            // this is a query
            String queryType = messageString.substring(1, 6);
            if ((queryType.startsWith("APRS")) || (queryType.startsWith("PING"))  || (queryType.startsWith("IGATE"))  || (queryType.startsWith("WX"))) {
                ret.setQuery(true);
            }
        }
        if (messageString.contains("{")) {
            // has an ACK expectation
            ret.setMustAck(true);
            String [] parts = messageString.split("\\{");
            if ((parts != null) && (parts.length == 2)) {
                ret.setMessage(parts[0]);
                String messageNumber = Stripper.stripWhitespace(parts[1]);
                ret.setMessageNumber(messageNumber);
            } else {
                throw new ParserException("Invalid packet message text");
            }
        } else {
            ret.setMessage(messageString);
            ret.setMustAck(false);
        }

        return ret;
    }

    public static APRSMessage buildGeneralMessage(String callsignFrom, String callsignTo, String message) throws ParserException {
        byte [] header = buildHeader(callsignFrom, callsignTo);
        byte [] data = buildData(message);
        APRSMessage msg = parse(header, data);
        return msg;
    }

    private static byte[] buildHeader(String callsignFrom, String callsignTo) {
        byte [] header = new byte[64];
        //8
        for (int c = 0; c < callsignFrom.length() && c < 9; c++) {
            header[c+8] = (byte) callsignFrom.charAt(c);
        }
        //18
        for (int c = 0; c < callsignTo.length() && c < 9; c++) {
            header[c+18] = (byte) callsignTo.charAt(c);
        }
        return header;
    }

    private static byte[] buildData(String message) {
        return message.getBytes();
    }
}
