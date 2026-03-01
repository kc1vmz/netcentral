package com.kc1vmz.netcentral.parser;

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
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.interfaces.APRSPacketInterface;
import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.parser.factory.APRSAgreloFactory;
import com.kc1vmz.netcentral.parser.factory.APRSItemFactory;
import com.kc1vmz.netcentral.parser.factory.APRSMaidenheadLocatorBeaconFactory;
import com.kc1vmz.netcentral.parser.factory.APRSMessageFactory;
import com.kc1vmz.netcentral.parser.factory.APRSMicEFactory;
import com.kc1vmz.netcentral.parser.factory.APRSObjectFactory;
import com.kc1vmz.netcentral.parser.factory.APRSPositionFactory;
import com.kc1vmz.netcentral.parser.factory.APRSQueryFactory;
import com.kc1vmz.netcentral.parser.factory.APRSStationCapabilitiesFactory;
import com.kc1vmz.netcentral.parser.factory.APRSStatusFactory;
import com.kc1vmz.netcentral.parser.factory.APRSTelemetryFactory;
import com.kc1vmz.netcentral.parser.factory.APRSTestFactory;
import com.kc1vmz.netcentral.parser.factory.APRSThirdPartyTrafficFactory;
import com.kc1vmz.netcentral.parser.factory.APRSUnknownFactory;
import com.kc1vmz.netcentral.parser.factory.APRSUserDefinedFactory;
import com.kc1vmz.netcentral.parser.factory.APRSWeatherReportFactory;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;


public class APRSParser {
    private static final int HEADER_LENGTH = 36;
    private static final Logger logger = LogManager.getLogger(APRSParser.class);

    public static APRSPacketInterface parseAGWPE(byte [] data) throws ParserException, NullPointerException {
        APRSPacketInterface ret = null;

        if (data == null) {
            throw new NullPointerException();
        } else if (data.length < HEADER_LENGTH) {
            logger.warn(String.format("Data buffer length only %d bytes", data.length));
            throw new ParserException("Insufficient data provided");
        }

        byte [] header = Arrays.copyOfRange(data, 0, HEADER_LENGTH);
        byte [] user_data = Arrays.copyOfRange(data, HEADER_LENGTH, data.length);

        List<byte []> lines = getLines(user_data);
        if (!lines.isEmpty()) {
            for (int c= 0; c < lines.size(); c++) {
                logger.info(String.format("Line %d - %s", c, new String(lines.get(c))));
            }
        }
        if (lines.size() > 1) {
            byte[] packetData = lines.get(1);
            ret = parseData(header, packetData);
        } else if (lines.size() == 1) {
            byte[] packetData = lines.get(0);
            ret = parseData(header, packetData);
        }

        return ret;
    }

    public static APRSPacketInterface parseTest(String callsignFrom, String callsignTo, byte [] data) throws NullPointerException, ParserException {
        APRSPacketInterface ret;

        if (data == null) {
            throw new NullPointerException();
        } 

        String dataStr = new String(data);
        if (dataStr.length() == 0) {
            throw new ParserException("0 length packet");
        }

        byte [] header = buildHeader(callsignFrom, callsignTo, data.length);

        ret = parseData(header, data);

        return ret;
    }

    public static APRSPacketInterface parseKenwood(byte [] data) throws NullPointerException, ParserException {
        APRSPacketInterface ret;

        if (data == null) {
            throw new NullPointerException();
        } 

        String dataStr = new String(data);
        if (dataStr.length() == 0) {
            throw new ParserException("0 length packet");
        }

        int callSep;
        {
            // if the packet data start with ">" we have to look for the sep after it
            String after = dataStr.substring(1);
            callSep = after.indexOf(">");
            if (callSep != -1) {
                callSep++;
            }
        }

        if (callSep == -1) {
            throw new ParserException(String.format("Message missing > - \"%s\"", dataStr));
        }

        String callsignFrom = dataStr.substring(1, callSep);
        String remaining = dataStr.substring(callSep+1);
        int indexEndDigi = remaining.indexOf(":");
        if (indexEndDigi == -1) {
            throw new ParserException(String.format("Message missing : - \"%s\"", dataStr));
        }

        /* recreate a AX.25 header that Kenwood non-raw will not give us */
        String callStringTo = null;
        int indexEndFirst = remaining.indexOf(",");
        if (indexEndFirst != -1) {
            callStringTo = remaining.substring(0, indexEndFirst);
        }

        String packetData = remaining.substring(indexEndDigi+1);

        byte [] header = buildHeader(callsignFrom, callStringTo, data.length);
        ret = parseData(header, packetData.getBytes());

        return ret;
    }


    private static byte [] buildHeader(String callsignFrom, String callsignTo, int dataLength) {
        byte [] header = new byte[36];

        for (int i = 0; i < header.length; i++) {
            header[i] = 0;
        }

        header[4] = 'T';  // AX.25 packet type is 'T'

        if (callsignFrom != null) {
            byte [] callsignFrom_bytes = callsignFrom.getBytes();
            for (int i = 0; i < callsignFrom_bytes.length; i++) {
                header[8+i] = callsignFrom_bytes[i];
            }
        }
        if (callsignTo != null) {
            byte [] callsignTo_bytes = callsignTo.getBytes();
            for (int i = 0; i < callsignTo_bytes.length; i++) {
                header[18+i] = callsignTo_bytes[i];
            }
        }

        // write length
        header[28] = (byte)(dataLength & 0xff);
        header[29] = (byte)((dataLength >> 8) & 0xff);
        header[30] = (byte)(dataLength >> 16 & 0xff);
        header[31] = (byte)((dataLength >> 24) & 0xff);
    
        return header;
    }

    public static APRSPacketInterface parseData(byte [] header, byte [] data) throws ParserException, NullPointerException {
        APRSPacketInterface ret = null;

        // check if this is a BEACON or ID*
        String callsignTo = AgwHeaderParser.getCallsignTo(header);
        if (("BEACON".equalsIgnoreCase(callsignTo)) || (("ID".equalsIgnoreCase(callsignTo)) )) {
            if (data[0] != '>') {
                // beat this beacon into submission as a status
                byte [] newdata = new byte[data.length+1];
                newdata[0] = '>';
                System.arraycopy(data, 0, newdata, 1, data.length);
                data = newdata;
                logger.debug("BEACON / ID packet found - transmogrifying into a message");
            }
        } else {
            filterDTIDoNotUse(data);
            filterDTIUnused(data);
            logger.debug(String.format("Parsing packet with DTI = %d", data[0]));
        }


        if (data[0] == '}') {
            // this is third-party traffic - strip encapsulation and send down the packet
            String encapsulatedPacket = new String(data).substring(1); // skip }
            int fromEnd = encapsulatedPacket.indexOf(">");
            if (fromEnd == -1) {
                ret = APRSUnknownFactory.parse(header, data);
                logger.warn(String.format("Unknown data packet [%s]", new String(data)));
                return ret;
            }
            String origFrom = encapsulatedPacket.substring(0, fromEnd);
            updateHeaderCallsignFrom(header, origFrom.getBytes());

            int indexEnd = encapsulatedPacket.indexOf(":");
            if (indexEnd == -1) {
                ret = APRSUnknownFactory.parse(header, data);
                logger.warn(String.format("Unknown data packet [%s]", new String(data)));
                return ret;
            }
            data = Arrays.copyOfRange(encapsulatedPacket.getBytes(), indexEnd+1, data.length);
            logger.info("New data after unencapsulation = " +  new String (data));
        }

        // DTI switch
        switch (data[0]) {
            case '\'': // Old Mic-E Data (but Current data for TM-D700)
            case 0x1d: // old MIC-E data
            case '`': // Current Mic-E Data (not used in TM-D700)
            case 0x1c: // MIC-E data
                ret = APRSMicEFactory.parse(header, data);
                break;
            case '<': // status
                ret = APRSStationCapabilitiesFactory.parse(header, data);
                break;
            case '>': // status
                ret = APRSStatusFactory.parse(header, data);
                break; 
            case '?': // query
                ret = APRSQueryFactory.parse(header, data);
                break;
            case '%': // Agrelo DFJr / MicroFinder
                ret = APRSAgreloFactory.parse(header, data);
                break;
            case 'T': // telemetry data
                ret = APRSTelemetryFactory.parse(header, data);
                break; 
            case '[': // Maidenhead grid locator beacon (obsolete)
                ret = APRSMaidenheadLocatorBeaconFactory.parse(header, data);
                break;
            case ')': // Item
                ret = APRSItemFactory.parse(header, data);
                break;
            case '#': // Peet Bros U-II Weather Station
            case '$': // Raw GPS data or Ultimeter 2000
            case '_': // Weather Report (without position)
            case '*': // Peet Bros U-II Weather Station 
                ret = APRSWeatherReportFactory.parse(header, data);
                break;
            case '&': // [Reserved — Map Feature] 
            case '+': // [Reserved — Shelter data with time] 
            case '.': // [Reserved — Space weather]
                break;
            case ',': // Invalid data or test data
                ret = APRSTestFactory.parse(header, data);
                break;
            case '{': // User-Defined APRS packet format
                ret = APRSUserDefinedFactory.parse(header, data);
                break;
            case '@': // Position with timestamp (with APRS messaging)
            case '/': // Position with timestamp (no APRS messaging)
            case '=': // Position without timestamp (with APRS messaging), or Ultimeter 2000 WX Station
            case '!': // Position without timestamp (no APRS messaging), or Ultimeter 2000 WX Station
                ret = APRSPositionFactory.parse(header, data);
                break;
            case '}': // Third-party traffic
                ret = APRSThirdPartyTrafficFactory.parse(header, data);
                break;
            case ':': // message
                ret = APRSMessageFactory.parse(header, data);
                break;
            case ';': // object
                ret = APRSObjectFactory.parse(header, data);
                break;
            default:
                ret = APRSUnknownFactory.parse(header, data);
                logger.warn(String.format("Unknown data packet [%s]", new String(data)));
                break;
        }

        return ret;
    }

    private static void updateHeaderCallsignFrom(byte[] header, byte[] newCall) {
        // header has callFrom starting at byte 8
        for (int i = 0; i < 9; i++) {
            header[i+8] = 0;
            if (i < newCall.length) {
                header[i+8] = newCall[i];  // copy value from new call
            }
        }
    }

    private static void filterDTIDoNotUse(byte[] data) throws ParserException {
        logger.debug("DTI check - "+ new String(data));
        // unused items
        if ((data[0] >= 'A') && (data[0] <= 'S')) {
            throw new ParserException(String.format("DTI provided that should not be used - %d %c", data[0], (char) data[0]));
        }
        if ((data[0] >= 'U') && (data[0] <= 'Z')) {
            throw new ParserException(String.format("DTI provided that should not be used - %d %c", data[0], (char) data[0]));
        }
        if ((data[0] >= '0') && (data[0] <= '9')) {
            throw new ParserException(String.format("DTI provided that should not be used - %d %c", data[0], (char) data[0]));
        }
        if ((data[0] >= 'a') && (data[0] <= 'z')) {
            throw new ParserException(String.format("DTI provided that should not be used - %d %c", data[0], (char) data[0]));
        }
        if ((data[0] == '|') || (data[0] == '~')) {
            throw new ParserException(String.format("DTI provided that should not be used - %d %c", data[0], (char) data[0]));
        }
    }

    private static void filterDTIUnused(byte[] data) throws ParserException {
        // unused items
        if ((data[0] == '"') || (data[0] == '(') || (data[0] == '\\') || (data[0] == ']') || (data[0] == '^') || (data[0] == '-')){
            throw new ParserException(String.format("DTI provided is unused - %d %c", data[0], (char) data[0]));
        }
    }

    private static List<byte[]> getLines(byte[] array) {
        byte[] temp = new byte[array.length];
        int tempSize = 0;

        List<byte[]> byteArrays = new LinkedList<>();

        for (int c = 0; c < array.length; c++) {
            if (array[c] == '\r') {
                if (tempSize > 0) {
                    byteArrays.add(Arrays.copyOfRange(temp, 0, tempSize));
                }
                tempSize = 0;
            } else if (array[c] == (byte) 13) {
                // do nothing
            } else {
                temp[tempSize] = array[c];
                tempSize++;
            }
        }
        if (tempSize > 0) {
            byteArrays.add(Arrays.copyOfRange(temp, 0, tempSize));
        }
        return byteArrays;
    }
}
