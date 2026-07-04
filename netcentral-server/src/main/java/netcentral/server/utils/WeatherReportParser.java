package netcentral.server.utils;

/*
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

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

public class WeatherReportParser {
    public static final char WEATHERREPORT_FIELD_WIND_DIRECTION = 'c';
    public static final char WEATHERREPORT_FIELD_WIND_SPEED = 's';
    public static final char WEATHERREPORT_FIELD_WIND_GUST = 'g';
    public static final char WEATHERREPORT_FIELD_TEMPERATURE = 't';
    public static final char WEATHERREPORT_FIELD_RAIN_LAST_HOUR = 'r';
    public static final char WEATHERREPORT_FIELD_RAIN_LAST_24HOUR = 'p';
    public static final char WEATHERREPORT_FIELD_RAIN_SINCE_MIDNIGHT = 'P';
    public static final char WEATHERREPORT_FIELD_HUMIDITY = 'h';
    public static final char WEATHERREPORT_FIELD_BAROMETRIC_PRESSURE = 'b';
    public static final char WEATHERREPORT_FIELD_LUMOSITY = 'L';
    public static final char WEATHERREPORT_FIELD_LUMOSITY_LARGE = 'l';
    public static final char WEATHERREPORT_FIELD_RAIN_RAW = '#';

    public static final int INDEX_WEATHERREPORT_FIELD_WIND_DIRECTION = 0;
    public static final int INDEX_WEATHERREPORT_FIELD_WIND_SPEED = 1;
    public static final int INDEX_WEATHERREPORT_FIELD_WIND_GUST = 2;
    public static final int INDEX_WEATHERREPORT_FIELD_TEMPERATURE = 3;
    public static final int INDEX_WEATHERREPORT_FIELD_RAIN_LAST_HOUR = 4;
    public static final int INDEX_WEATHERREPORT_FIELD_RAIN_LAST_24HOUR = 5;
    public static final int INDEX_WEATHERREPORT_FIELD_RAIN_SINCE_MIDNIGHT = 6;
    public static final int INDEX_WEATHERREPORT_FIELD_HUMIDITY = 7;
    public static final int INDEX_WEATHERREPORT_FIELD_BAROMETRIC_PRESSURE = 8;
    public static final int INDEX_WEATHERREPORT_FIELD_LUMOSITY = 9;
    public static final int INDEX_WEATHERREPORT_FIELD_LUMOSITY_LARGE = 10;
    public static final int INDEX_WEATHERREPORT_FIELD_RAIN_RAW = 11;

    public static final int INDEX_WEATHERREPORT_MAX = 11;

    public static final String WEATHERREPORT_ULTW = "ULTW";

    public static boolean [] createBitmask() {
        return new boolean [INDEX_WEATHERREPORT_MAX+1];
    }
}
