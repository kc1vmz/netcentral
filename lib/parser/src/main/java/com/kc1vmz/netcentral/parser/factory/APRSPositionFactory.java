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

import com.kc1vmz.netcentral.aprsobject.object.APRSPosition;
import com.kc1vmz.netcentral.aprsobject.utils.CompressedDataFormatUtils;
import com.kc1vmz.netcentral.parser.exception.APRSTimeConversionException;
import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.parser.util.APRSTime;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSPositionFactory {
    private static final Logger logger = LogManager.getLogger(APRSPositionFactory.class);

    // BAD:  W1OT-2:!/<93ZxC2[[A9Q

    public static APRSPosition parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSPosition object");
        APRSPosition ret = new APRSPosition();

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);
        if ((data == null) || (data.length < 1)) {
            throw new ParserException("Not an APRSPosition object");
        }
        if ((data[0] != '!') && (data[0] != '=') && (data[0] != '/') && (data[0] != '@')) {
            throw new ParserException("Not an APRSPosition object");
        }
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        int messageIndex = 0;
        byte [] time_bytes;
        String lat;
        String lon;

        String original = new String(data);
        logger.debug("Position packet: "+original);

        if ((data[messageIndex] == '!') || (data[messageIndex] == '=')) {
            // no timestamp
            messageIndex += 1; // move down buffer
        } else {
            messageIndex += 1; // move down buffer
            time_bytes = Arrays.copyOfRange(data, messageIndex+0, messageIndex+7);
            messageIndex += 7; // account for timestamp
            ret.setTime(new String(time_bytes));
            try {
                ret.setLdtime(APRSTime.convertAPRSTimeToZonedDateTime(ret.getTime()));
            } catch (APRSTimeConversionException e) {
                logger.error("Exception caught", e);
            }
        }

        byte symbolCode = 0;

        if ((data[messageIndex] < '0') || (data[messageIndex] > '9')) { // if it is not a digit, then it is compressed
            // compressed location data
            symbolCode = data[messageIndex+9];
            byte [] compressedData = Arrays.copyOfRange(data, messageIndex-1, messageIndex+13-1);
            lat = CompressedDataFormatUtils.convertDecimalToDDMMSSx(CompressedDataFormatUtils.getLatitude(compressedData), "NS");
            lon = CompressedDataFormatUtils.convertDecimalToDDDMMSSx(CompressedDataFormatUtils.getLongitude(compressedData), "EW");
            messageIndex += 13; // compressed is 13 bytes
            logger.info(String.format("**** COMPRESSED LAT LON %s %s **** ", lat, lon));
        } else {
            byte [] latByte = Arrays.copyOfRange(data, messageIndex+0, messageIndex+8);
            messageIndex += 9;
            byte [] lonByte = Arrays.copyOfRange(data, messageIndex+0, messageIndex+9);
            if ((lonByte[8] != 'E') && (lonByte[8] != 'W')) {
                lonByte = Arrays.copyOfRange(data, messageIndex+0, messageIndex+8);
                messageIndex +=8;
                // bad lon - fixed up
            } else {
                messageIndex +=9;
                // good lon
            }
            symbolCode = data[messageIndex];
            messageIndex++;
            lat = new String(latByte);
            lon = new String(lonByte);
        }

        byte [] comment = Arrays.copyOfRange(data, messageIndex, data.length);
        String commentStr = new String(comment);
        if (symbolCode == '_') {
            // have a weather report
            ret.setHasWeatherReport(true);
            ret.setWeatherReport(commentStr);
        } else if (commentStr.startsWith("PHG")) {
            // supports PHG
            ret.setPower(calculatePower(comment[3]));
            ret.setHeight(calculateHeight(comment[4]));
            ret.setGain(calculateGain(comment[5]));
            ret.setDirectivity(calculateDirectivity(comment[6]));
            commentStr = commentStr.substring(7);  // skip PHG data
        } else if (commentStr.startsWith("RNG")) {
            // supports RNG
            ret.setRange(calculateRange(comment[3], comment[4], comment[5], comment[6]));
        } else if (commentStr.startsWith("DFS")) {
            // supports DFS
            ret.setStrength(calculateStrength(comment[3]));
            ret.setHeight(calculateHeight(comment[4]));
            ret.setGain(calculateGain(comment[5]));
            ret.setDirectivity(calculateDirectivity(comment[6]));
            commentStr = commentStr.substring(7);  // skip PHG data
        } else if ((commentStr.length() == 7) && (commentStr.charAt(3) == '/')) {
            // weather report
            commentStr = commentStr.substring(7);  // skip 
        }
        ret.setComment(commentStr);
        ret.setLat(lat);
        ret.setLon(lon);

        return ret;
    }

    private static int calculateStrength(byte b) {
        int ret = b - '0';
        return ret;
    }

    private static int calculateRange(byte thou, byte hun, byte ten, byte one) {
        int thousands = (thou - '0')*1000;
        int hundreds = (hun - '0')*100;
        int tens = (ten - '0')*10;
        int ones = (one - '0');

        return thousands + hundreds + tens + ones;
    }

    private static int calculatePower(byte b) {
        int ret = -1;
        int [] powers = { 0, 1, 4, 9, 16, 25, 36, 49, 64, 81 };
        int index = b - '0';
        if ((index > 0) && (index < powers.length)) {
            ret = powers[index];
        }
        return ret;
    }

    private static int calculateHeight(byte b) {
        int ret = 10;
        int index = b - '0';
        while (index > 1) {
            ret += ret;
            index--;
        }
        return ret;
    }

    private static int calculateGain(byte b) {
        int ret = b - '0';
        return ret;
    }

    private static String calculateDirectivity(byte b) {
        String ret = "";
        String [] powers = { "Omni", "45 NE", "90 E","135 SE","180 S","225 SW","270 W","315 NW", "360 N" };
        
        int index = b - '0';
        if ((index > 0) && (index < powers.length)) {
            ret = powers[index];
        }
        return ret;
    }

}
