package netcentral.server.utils;

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

public class DateStrUtils {
    /**
     * get date string from LocalDateTime
     *
     * @param dt
     * @return Date in MM/DD/YYYY format
     */
    public static String getDateStr(LocalDateTime dt) {
        return String.format("%02d/%02d/%d", dt.getMonthValue(),dt.getDayOfMonth(), dt.getYear());
    }

    /**
     * get time string from LocalDateTime
     *
     * @param dt
     * @return hour in HH:MM format
     */
    public static String getTimeStr(LocalDateTime dt) {
        return String.format("%02d:%02d", dt.getHour(),dt.getMinute());
    }

    /**
     * get date string from ZonedDateTime
     *
     * @param dt
     * @return Date in MM/DD/YYYY format
     */
    public static String getDateStr(ZonedDateTime dt) {
        return String.format("%02d/%02d/%d", dt.getMonthValue(),dt.getDayOfMonth(), dt.getYear());
    }

    /**
     * get time string from ZonedDateTime
     *
     * @param dt
     * @return hour in HH:MM format
     */
    public static String getTimeStr(ZonedDateTime dt) {
        return String.format("%02d:%02d", dt.getHour(),dt.getMinute());
    }
}
