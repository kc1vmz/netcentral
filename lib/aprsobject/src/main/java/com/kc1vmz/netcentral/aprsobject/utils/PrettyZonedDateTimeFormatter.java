package com.kc1vmz.netcentral.aprsobject.utils;

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
import java.time.ZonedDateTime;

public class PrettyZonedDateTimeFormatter {
    public static String format(ZonedDateTime dt) {
        if (dt == null) {
            return "";
        }
        return String.format("%d-%02d-%02d %02d:%02d:%02d", dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
    }
    public static String formatISO8601(ZonedDateTime dt) {
        if (dt == null) {
            return "";
        }
        return String.format("%d-%02d-%02dT%02d:%02d:%02dZ", dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
    }
    public static String format(LocalDateTime dt) {
        if (dt == null) {
            return "";
        }
        return String.format("%d-%02d-%02d %02d:%02d:%02d", dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
    }
}
