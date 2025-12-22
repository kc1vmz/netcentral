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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.parser.exception.APRSTimeConversionException;

public class APRSTime {
    public static boolean isTime(String time) {
        if (isDHMTime(time) || isHMSTime(time) || (isMDHMTime(time))) {
            return true;
        }
        return false;
    }

    private static boolean isDHMTime(String time) {
        boolean ret = true;
        if ((time == null) || (time.length() != 7)) {
            return false;
        }
        if ((!time.endsWith("z")) && (!time.endsWith("/"))) {
            return false;
        }

        // are these numbers?
        for (int c = 0; c < time.length()-1; c++) {
            char digit = time.charAt(c);
            if ((digit < '0') || (digit > '9')) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    private static boolean isHMSTime(String time) {
        boolean ret = true;
        if ((time == null) || (time.length() != 7)) {
            return false;
        }
        if (!time.endsWith("h")) {
            return false;
        }

        // are these numbers?
        for (int c = 0; c < time.length()-1; c++) {
            char digit = time.charAt(c);
            if ((digit < '0') || (digit > '9')) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    private static boolean isMDHMTime(String time) {
        boolean ret = true;
        if ((time == null) || (time.length() != 8)) {
            return false;
        }
        // are these numbers?
        for (int c = 0; c < time.length(); c++) {
            char digit = time.charAt(c);
            if ((digit < '0') || (digit > '9')) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    public static LocalDateTime convertAPRSTimeToLocalTime(String time) throws APRSTimeConversionException {
        LocalDateTime ldt = null;
        if (!isTime(time)) {
            throw new APRSTimeConversionException("Not a valid APRS time: " + time);
        }
        if (isDHMTime(time)) {
            // DDHHMM - DD day of month, HH hours, MM minutes
            if (time.endsWith("z")) {
                int dayOfMonth = buildNumber(time.substring(0, 2));
                int hour = buildNumber(time.substring(2, 4));
                int minute = buildNumber(time.substring(4, 6));
                LocalDateTime l = LocalDateTime.now(ZoneId.of("Z")).withDayOfMonth(dayOfMonth).withHour(hour).withMinute(minute);
                ldt = l;
            } else {
                // TODO - how to make right when we don't know the local time zone of the sender?
                int dayOfMonth = buildNumber(time.substring(0, 2));
                int hour = buildNumber(time.substring(2, 4));
                int minute = buildNumber(time.substring(4, 6));
                LocalDateTime l = LocalDateTime.now(ZoneId.of("Z")).withDayOfMonth(dayOfMonth).withHour(hour).withMinute(minute);
                ldt = l;
            }

        } else if (isHMSTime(time)) {
                int hour = buildNumber(time.substring(0, 2));
                int minute = buildNumber(time.substring(2, 4));
                int second = buildNumber(time.substring(4, 6));
                LocalDateTime l = LocalDateTime.now(ZoneId.of("Z")).withHour(hour).withMinute(minute).withSecond(second);
                ldt = l;
        } else if (isMDHMTime(time)) {
                int month = buildNumber(time.substring(0, 2));
                int day = buildNumber(time.substring(2, 4));
                int hour = buildNumber(time.substring(4, 6));
                int minute = buildNumber(time.substring(6, 8));
                LocalDateTime l = LocalDateTime.now(ZoneId.of("Z")).withMonth(month).withDayOfMonth(day).withHour(hour).withMinute(minute);
                ldt = l;
        }
        return ldt;
    }

    public static ZonedDateTime convertAPRSTimeToZonedDateTime(String time) throws APRSTimeConversionException {
        ZonedDateTime zdt = null;
        if (!isTime(time)) {
            throw new APRSTimeConversionException("Not a valid APRS time: " + time);
        }
        try {
            if (isDHMTime(time)) {
                // DDHHMM - DD day of month, HH hours, MM minutes
                if (time.endsWith("z")) {
                    // month boundaries gets tricky - NOVEMBER 31 problem
                    int dayOfMonth = buildNumber(time.substring(0, 2));
                    int hour = buildNumber(time.substring(2, 4));
                    int minute = buildNumber(time.substring(4, 6));
                    ZonedDateTime l = ZonedDateTime.now(ZoneId.of("Z")).withDayOfMonth(dayOfMonth).withHour(hour).withMinute(minute);
                    zdt = l;
                } else {
                    // TODO - how to make right when we don't know the local time zone of the sender?
                    int dayOfMonth = buildNumber(time.substring(0, 2));
                    int hour = buildNumber(time.substring(2, 4));
                    int minute = buildNumber(time.substring(4, 6));
                    ZonedDateTime l = ZonedDateTime.now(ZoneId.of("Z")).withDayOfMonth(dayOfMonth).withHour(hour).withMinute(minute);
                    zdt = l;
                }

            } else if (isHMSTime(time)) {
                    int hour = buildNumber(time.substring(0, 2));
                    int minute = buildNumber(time.substring(2, 4));
                    int second = buildNumber(time.substring(4, 6));
                    ZonedDateTime l = ZonedDateTime.now(ZoneId.of("Z")).withHour(hour).withMinute(minute).withSecond(second);
                    zdt = l;
            } else if (isMDHMTime(time)) {
                    int month = buildNumber(time.substring(0, 2));
                    int day = buildNumber(time.substring(2, 4));
                    int hour = buildNumber(time.substring(4, 6));
                    int minute = buildNumber(time.substring(6, 8));
                    ZonedDateTime l = ZonedDateTime.now(ZoneId.of("Z")).withMonth(month).withDayOfMonth(day).withHour(hour).withMinute(minute);
                    zdt = l;
            }
        } catch (Exception e) {
            throw new APRSTimeConversionException("Could not convert time - "+time, e);
        }
        return zdt;
    }

    private static int buildNumber(String value) {
        int ret = 0;
        ret = Integer.parseInt(value);
        return ret;
    }

    public static String convertZonedDateTimeToDDHHMM(ZonedDateTime time) {
        return String.format("%02d%02d%02dz", time.getDayOfMonth(), time.getHour(), time.getMinute());
    }
}
