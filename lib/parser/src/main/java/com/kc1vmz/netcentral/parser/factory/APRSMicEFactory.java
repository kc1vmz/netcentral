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
import com.kc1vmz.netcentral.aprsobject.enums.MicECommentType;
import com.kc1vmz.netcentral.aprsobject.object.APRSMicE;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;
import com.kc1vmz.netcentral.parser.util.MicELatDigitProcessor;
import com.kc1vmz.netcentral.parser.util.MicELonDegreeProcessor;
import com.kc1vmz.netcentral.parser.util.MicELonMinuteProcessor;

public class APRSMicEFactory {
    private static final Logger logger = LogManager.getLogger(APRSMicEFactory.class);


    public static APRSMicE parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSMicE object");
        APRSMicE ret = new APRSMicE();

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        String destField = AgwHeaderParser.getCallsignTo(header);

        ret.setCommentType(determineCommentType(destField));

        byte [] lonBytes = Arrays.copyOfRange(data, 1, 4);
        ret.setLat(determineLatitude(destField));
        ret.setLon(determineLongitude(destField, lonBytes, (ret.getLat().contains("  "))));

        ret.setStatus(new String (Arrays.copyOfRange(data, 9, data.length)));

        return ret;
    }


    private static String determineLongitude(String destField, byte[] lonBytes, boolean isAmbiguous) {
        boolean longOffset = determineLongOffset(destField);
        if ((destField == null) || (destField.length() < 6)) {
            return "";
        }
        if ((lonBytes == null) || (lonBytes.length != 3)) {
            return "";
        }
        MicELatDigitProcessor d5 = new MicELatDigitProcessor(destField.charAt(5));
        MicELatDigitProcessor d4 = new MicELatDigitProcessor(destField.charAt(4));

        MicELonDegreeProcessor degrees = new MicELonDegreeProcessor(lonBytes[0], longOffset);
        int degreesAdjusted = degrees.getValue();
        if (d4.getLongOffset() == 100) {
            degreesAdjusted -= 100;
        }
        MicELonMinuteProcessor minutes = new MicELonMinuteProcessor(lonBytes[1]);
        String ret = "";
        if (isAmbiguous) {
            ret = String.format("%03d%02d.  %s", degreesAdjusted, minutes.getValue(), ((d5.isWest() ? "W" : "E")));
        } else {
            ret = String.format("%03d%02d.%02d%s", degreesAdjusted, minutes.getValue(), lonBytes[2]-28, ((d5.isWest() ? "W" : "E")));
        }
        return ret;
    }


    private static boolean determineLongOffset(String destField) {
        MicELatDigitProcessor d1 = new MicELatDigitProcessor(destField.charAt(4));
        return (d1.getLongOffset() == 100);
    }


    private static String determineLatitude(String destField) {
        String lat = "";
        if ((destField == null) || (destField.length() < 6)) {
            return lat;
        }

        MicELatDigitProcessor d1 = new MicELatDigitProcessor(destField.charAt(0));
        MicELatDigitProcessor d2 = new MicELatDigitProcessor(destField.charAt(1));
        MicELatDigitProcessor d3 = new MicELatDigitProcessor(destField.charAt(2));
        MicELatDigitProcessor d4 = new MicELatDigitProcessor(destField.charAt(3));

        if (destField.endsWith("ZZ")) {
            lat = d1.getDigit()+d2.getDigit()+d3.getDigit()+d4.getDigit()+".  "+((d1.isNorth() ? "N" : "S"));
            lat = String.format("%c%c%c%c.  %s", d1.getDigit(),d2.getDigit(),d3.getDigit(),d4.getDigit(), (d4.isNorth() ? "N" : "S"));
        } else {
            MicELatDigitProcessor d5 = new MicELatDigitProcessor(destField.charAt(4));
            MicELatDigitProcessor d6 = new MicELatDigitProcessor(destField.charAt(5));
            lat = String.format("%c%c%c%c.%c%c%s", d1.getDigit(),d2.getDigit(),d3.getDigit(),d4.getDigit(),d5.getDigit(),d6.getDigit(), (d4.isNorth() ? "N" : "S"));
        }
        return lat;
    }


    private static MicECommentType determineCommentType(String destField) {
        MicECommentType ret = MicECommentType.UNKNOWN;
        if ((destField == null) || (destField.length() < 6)) {
            return ret;
        }
        MicELatDigitProcessor d1 = new MicELatDigitProcessor(destField.charAt(0));
        MicELatDigitProcessor d2 = new MicELatDigitProcessor(destField.charAt(1));
        MicELatDigitProcessor d3 = new MicELatDigitProcessor(destField.charAt(2));

        if (d1.isStandardComment() && d2.isStandardComment() && d3.isStandardComment()) {
            if (d1.isHasComment() && d2.isHasComment() && d3.isHasComment()) {
                ret = MicECommentType.OFF_DUTY;
            } else if (d1.isHasComment() && d2.isHasComment() && !d3.isHasComment()) {
                ret = MicECommentType.EN_ROUTE;
            } else if (d1.isHasComment() && !d2.isHasComment() && d3.isHasComment()) {
                ret = MicECommentType.IN_SERVICE;
            } else if (d1.isHasComment() && !d2.isHasComment() && !d3.isHasComment()) {
                ret = MicECommentType.RETURNING;
            } else if (!d1.isHasComment() && d2.isHasComment() && d3.isHasComment()) {
                ret = MicECommentType.COMMITTED;
            } else if (!d1.isHasComment() && d2.isHasComment() && !d3.isHasComment()) {
                ret = MicECommentType.SPECIAL;
            } else if (!d1.isHasComment() && !d2.isHasComment() && d3.isHasComment()) {
                ret = MicECommentType.PRIORITY;
            } else if (!d1.isHasComment() && !d2.isHasComment() && !d3.isHasComment()) {
                ret = MicECommentType.EMERGENCY;
            }
        } else if (!d1.isStandardComment() && !d2.isStandardComment() && !d3.isStandardComment()) {
            if (d1.isHasComment() && d2.isHasComment() && d3.isHasComment()) {
                ret = MicECommentType.CUSTOM_0;
            } else if (d1.isHasComment() && d2.isHasComment() && !d3.isHasComment()) {
                ret = MicECommentType.CUSTOM_1;
            } else if (d1.isHasComment() && !d2.isHasComment() && d3.isHasComment()) {
                ret = MicECommentType.CUSTOM_2;
            } else if (d1.isHasComment() && !d2.isHasComment() && !d3.isHasComment()) {
                ret = MicECommentType.CUSTOM_3;
            } else if (!d1.isHasComment() && d2.isHasComment() && d3.isHasComment()) {
                ret = MicECommentType.CUSTOM_4;
            } else if (!d1.isHasComment() && d2.isHasComment() && !d3.isHasComment()) {
                ret = MicECommentType.CUSTOM_5;
            } else if (!d1.isHasComment() && !d2.isHasComment() && d3.isHasComment()) {
                ret = MicECommentType.CUSTOM_6;
            } else if (!d1.isHasComment() && !d2.isHasComment() && !d3.isHasComment()) {
                ret = MicECommentType.EMERGENCY;
            }
        }

        return ret;
    }

}
