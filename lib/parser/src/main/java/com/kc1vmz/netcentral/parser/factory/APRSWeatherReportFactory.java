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

import com.kc1vmz.netcentral.aprsobject.object.APRSWeatherReport;
import com.kc1vmz.netcentral.aprsobject.utils.CompressedDataFormatUtils;
import com.kc1vmz.netcentral.parser.exception.APRSTimeConversionException;
import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.parser.util.APRSTime;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;
import com.kc1vmz.netcentral.parser.util.StringUtils;

public class APRSWeatherReportFactory {
    private static final Logger logger = LogManager.getLogger(APRSWeatherReportFactory.class);

    public static APRSWeatherReport parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSWeatherReport object");
        APRSWeatherReport ret = new APRSWeatherReport();

        if (data == null) {
            throw new NullPointerException();
        }

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        if (data[0] == '_') {
            ret = parsePositionless(header, data, ret);
        } else if ((data[0] == '!') || (data[0] == '=')) {
            ret = parseCompleteWithoutTimestamp(header, data, ret);
        } else if ((data[0] == '/') || (data[0] == '@')) {
            ret = parseCompleteWithTimestamp(header, data, ret);
        }

        if (ret != null) {
            if (ret.getTime() == null) {
                ret.setTime("");
                ret.setLdtime(ZonedDateTime.now());
            }
        }

        return ret;
    }

    private static APRSWeatherReport parseCompleteWithTimestamp(byte[] header, byte[] data, APRSWeatherReport ret) {
        int messageIndex = 0;

        // time is the same in both
        byte [] time_bytes = Arrays.copyOfRange(data, 1, 8);
        ret.setTime(new String(time_bytes));
        try {
            ret.setLdtime(APRSTime.convertAPRSTimeToZonedDateTime(ret.getTime()));
        } catch (APRSTimeConversionException e) {
            logger.error("Exception caught", e);
        }

        if (data[17] == '_') {
            return parseCompleteWithTimestampCompressedLocation(header, data, ret);
        }

        // uncompressed location

        ret.setSymbolTableId(StringUtils.stringify(data[16]));
        ret.setSymbolTableCode(StringUtils.stringify(data[26]));

        byte [] lat = Arrays.copyOfRange(data, 8, 16);
        ret.setLat(new String(lat));
        byte [] lon = Arrays.copyOfRange(data, 17, 26);

        if ((lon[8] != 'E') && (lon[8] != 'W')) {
            lon = Arrays.copyOfRange(data, 17, 25);
            // bad lon - short one character
            String prependZero = "0"+new String(lon);
            lon = prependZero.getBytes();
            messageIndex = 26;
        } else {
            messageIndex = 27;
        }
        ret.setLon(new String(lon));

        byte [] directionAndSpeed_bytes = Arrays.copyOfRange(data, messageIndex, messageIndex+7);
        String directionAndSpeed = new String(directionAndSpeed_bytes);
        String [] dsArray = directionAndSpeed.split("/");
        if ((dsArray != null) && (dsArray.length == 2)) {
            ret.setWindDirection(Integer.parseInt(dsArray[0]));
            ret.setWindSpeed(Integer.parseInt(dsArray[1]));
        }

        messageIndex += 7; // skip wind speed and direction

        byte [] weatherData_bytes = Arrays.copyOfRange(data, messageIndex, data.length);
        parseWeatherData(weatherData_bytes, ret);

        return ret;
    }

    private static APRSWeatherReport parseCompleteWithTimestampCompressedLocation(byte[] header, byte[] data, APRSWeatherReport ret) {
        // compressed location data
        byte [] compressedData = Arrays.copyOfRange(data, 9, 17);
        String lat = CompressedDataFormatUtils.convertDecimalToDDMMSSx(CompressedDataFormatUtils.getLatitudeShort(compressedData), "NS");
        String lon = CompressedDataFormatUtils.convertDecimalToDDDMMSSx(CompressedDataFormatUtils.getLongitudeShort(compressedData), "EW");
        ret.setLat(lat);
        ret.setLon(lon);

        ret.setSymbolTableId(StringUtils.stringify(data[8]));
        ret.setSymbolTableCode(StringUtils.stringify(data[17]));

        // next is 7 bytes for wind speed/direction
        byte [] directionAndSpeed_bytes = Arrays.copyOfRange(data, 18, 21);
        String directionAndSpeed = new String(directionAndSpeed_bytes);
        ret = determineDirectionAndSpeedFromCompressed(ret, directionAndSpeed);

        byte [] weatherData_bytes = Arrays.copyOfRange(data, 21, data.length);
        parseWeatherData(weatherData_bytes, ret);

        return ret;
    }

    private static APRSWeatherReport parseCompleteWithoutTimestamp(byte[] header, byte[] data, APRSWeatherReport ret) {
        int messageIndex = 0;

        if (data[10] == '_') {
            return parseCompleteWithoutTimestampCompressedLocation(header, data, ret);
        }

        byte [] lat = Arrays.copyOfRange(data, 1, 9);
        ret.setLat(new String(lat));
        ret.setSymbolTableId(StringUtils.stringify(data[9]));
        byte [] lon = Arrays.copyOfRange(data, 10, 19);

        if ((lon[8] != 'E') && (lon[8] != 'W')) {
            lon = Arrays.copyOfRange(data, 10, 18);
            // bad lon - short one character
            String prependZero = "0"+new String(lon);
            lon = prependZero.getBytes();
            messageIndex = 18;
        } else {
            messageIndex = 19;
        }
        ret.setLon(new String(lon));

        if (data[messageIndex] != '_') {
            // this must be an underscore
            return null;
        }
        ret.setSymbolTableCode(StringUtils.stringify(data[messageIndex]));
        messageIndex++;

        // next is 7 bytes for wind speed/direction
        byte [] directionAndSpeed_bytes = Arrays.copyOfRange(data, messageIndex, messageIndex+7);
        String directionAndSpeed = new String(directionAndSpeed_bytes);
        String [] dsArray = directionAndSpeed.split("/");
        if ((dsArray != null) && (dsArray.length == 2)) {
            ret.setWindDirection(Integer.parseInt(dsArray[0]));
            ret.setWindSpeed(Integer.parseInt(dsArray[1]));
        }
        messageIndex += 7;

        byte [] weatherData_bytes = Arrays.copyOfRange(data, messageIndex, data.length);
        parseWeatherData(weatherData_bytes, ret);

        return ret;
    }

    private static APRSWeatherReport parseCompleteWithoutTimestampCompressedLocation(byte[] header, byte[] data, APRSWeatherReport ret) {
        ret.setSymbolTableId(StringUtils.stringify(data[1]));
        ret.setSymbolTableCode(StringUtils.stringify(data[10]));

        byte [] compressedData = Arrays.copyOfRange(data, 2, 10);
        String lat = CompressedDataFormatUtils.convertDecimalToDDMMSSx(CompressedDataFormatUtils.getLatitudeShort(compressedData), "NS");
        String lon = CompressedDataFormatUtils.convertDecimalToDDDMMSSx(CompressedDataFormatUtils.getLongitudeShort(compressedData), "EW");
        ret.setLat(new String(lat));
        ret.setLon(new String(lon));

        byte [] directionAndSpeed_bytes = Arrays.copyOfRange(data, 11, 14);
        String directionAndSpeed = new String(directionAndSpeed_bytes);
        ret = determineDirectionAndSpeedFromCompressed(ret, directionAndSpeed);

        byte [] weatherData_bytes = Arrays.copyOfRange(data, 14, data.length);
        parseWeatherData(weatherData_bytes, ret);

        return ret;
    }

    private static APRSWeatherReport determineDirectionAndSpeedFromCompressed(APRSWeatherReport ret, String directionAndSpeed) {

        if (directionAndSpeed.startsWith(" ")) {
            // ignore this value
            return ret;
        }

        char c = directionAndSpeed.charAt(0);
        if ((c >= '!') && (c <= 'z')) {
            // 0 - 89 after subtracting 33 from c
            // compressed course / speed
            int cInt = (int) c;
            cInt -= 33;

            ret.setWindDirection(cInt*4);

            char s = directionAndSpeed.charAt(1);
            int sInt = (int) s;
            double base = 1.08;
            double result = Math.pow(base, sInt) - 1;
            ret.setWindSpeed((int) Math.round(result));
        }

        return ret;
    }

    private static APRSWeatherReport parsePositionless(byte[] header, byte[] data, APRSWeatherReport ret) {
        // positionless weather data
        byte [] time_bytes = Arrays.copyOfRange(data, 1, 9);
        ret.setTime(new String(time_bytes));
        try {
            ret.setLdtime(APRSTime.convertAPRSTimeToZonedDateTime(ret.getTime()));
        } catch (APRSTimeConversionException e) {
            logger.error("Exception caught", e);
        }

        byte [] weatherData_bytes = Arrays.copyOfRange(data, 9, data.length);
        parseWeatherData(weatherData_bytes, ret);

        return ret;
    }

    private static void parseWeatherData(byte[] weatherData_bytes, APRSWeatherReport ret) {
        if (ultPacket(weatherData_bytes)) {
            return;
        }
        int index = 0;
        boolean seenS = false;
        boolean stop = false;

        while ((index < weatherData_bytes.length) && (!stop)) {
            char type = (char) weatherData_bytes[index];
            byte [] value_bytes;
            try {
                switch (type) {
                    case 'c': // value in next 3 bytes
                        value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+4);
                        index += 4;
                        if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                            // get the value
                            String value = new String(value_bytes);
                            ret.setWindDirection(Integer.parseInt(value));
                        }
                        break;
                    case 's': // value in next 3 bytes
                        if (!seenS) {
                            // first time through - wind speed
                            value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+4);
                            index += 4;
                            if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                                // get the value
                                String value = new String(value_bytes);
                                ret.setWindSpeed(Integer.parseInt(value));
                            }
                        } else {
                            // if seen again, maybe it is snow. APRS spec shows used for both
                            value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+4);
                            index += 4;
                            if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                                // get the value
                                String value = new String(value_bytes);
                                if (value.contains(".")) {
                                    String [] snow_parts = value.split(".");
                                    if ((snow_parts != null) && (snow_parts.length == 2)) {
                                        ret.setSnowfallLast24Hr(Integer.parseInt(snow_parts[0]));
                                    }
                                } else {
                                    ret.setSnowfallLast24Hr(Integer.parseInt(value));
                                }
                            }
                        }
                        seenS = true;
                        break;
                    case 'g': // value in next 3 bytes
                        value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+4);
                        index += 4;
                        if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                            // get the value
                            String value = new String(value_bytes);
                            ret.setGust(Integer.parseInt(value));
                        }
                        break;
                    case 't': // value in next 3 bytes
                        value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+4);
                        index += 4;
                        if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                            // get the value
                            String value = new String(value_bytes);
                            ret.setTemperature(Integer.parseInt(value));
                        }
                        break;
                    case 'r': // value in next 3 bytes
                        value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+4);
                        index += 4;
                        if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                            // get the value
                            String value = new String(value_bytes);
                            ret.setRainfallLast1Hr(Integer.parseInt(value));
                        }
                        break;
                    case 'p': // value in next 3 bytes
                        value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+4);
                        index += 4;
                        if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                            // get the value
                            String value = new String(value_bytes);
                            ret.setRainfallLast24Hr(Integer.parseInt(value));
                        }
                        break;
                    case 'P': // value in next 3 bytes
                        value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+4);
                        index += 4;
                        if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                            // get the value
                            String value = new String(value_bytes);
                            ret.setRainfallSinceMidnight(Integer.parseInt(value));
                        }
                        break;
                    case 'h': // value in next *2* bytes
                        value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+3);
                        index += 3;
                        if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                            // get the value
                            String value = new String(value_bytes);
                            ret.setHumidity(Integer.parseInt(value));
                        }
                        break;
                    case 'b': // value in next *5* bytes
                        value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+6);
                        index += 6;
                        if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                            // get the value
                            String value = new String(value_bytes);
                            ret.setBarometricPressure(Integer.parseInt(value));
                        }
                        break;
                    case 'L': // value in next 3 bytes
                        value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+4);
                        index += 4;
                        if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                            // get the value
                            String value = new String(value_bytes);
                            ret.setLuminosity(Integer.parseInt(value));
                        }
                        break;
                    case 'l': // value in next 3 bytes
                        value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+4);
                        index += 4;
                        if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                            // get the value
                            String value = new String(value_bytes);
                            ret.setLuminosity(Integer.parseInt(value)+1000);
                        }
                        break;
                    case '#': // value in next 3 bytes
                        value_bytes = Arrays.copyOfRange(weatherData_bytes, index+1, index+4);
                        index += 3;
                        if ((value_bytes[0] != ' ') && (value_bytes[0] != '.')) {
                            // get the value
                            String value = new String(value_bytes);
                            ret.setRawRainCounter(Integer.parseInt(value));
                        }
                        break;
                    default:
                        index++ ;
                        stop = true;
                        // found something we should not have - get out
                        break;
                }
            } catch (Exception e) {
                logger.error(String.format("Error parsing weather data - %s>%s %s", ret.getCallsignFrom(), ret.getCallsignTo(), new String(weatherData_bytes)), e);
                break;
            }
        }
    }

    private static boolean ultPacket(byte[] weatherData_bytes) {
        String weatherData = new String(weatherData_bytes);
        return weatherData.startsWith("ULTW");
    }

}
