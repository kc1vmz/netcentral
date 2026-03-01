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

public class CompressedDataFormatUtils {
    /*
     * Lat = 90 - ((y1-33) x 913 + (y2-33) x 912 + (y3-33) x 91 + y4-33) / 380926
     * Long = -180 + ((x1-33) x 913 + (x2-33) x 912 + (x3-33) x 91 + x4-33) / 190463
     */

    // expecting YYYYXXXX
    public static double getLatitudeShort(byte [] data) {
        double a = (double) data[0];
        double b = (double) data[1];
        double c = (double) data[2];
        double d = (double) data[3];
        return getLatitude(a, b, c, d);
    }

    // expecting /YYYYXXXX
    public static double getLatitude(byte [] data) {
        double a = (double) data[1];
        double b = (double) data[2];
        double c = (double) data[3];
        double d = (double) data[4];
        return getLatitude(a, b, c, d);
    }

    private static double getLatitude(double a, double b, double c, double d) {
        double c1 = 91;
        double c2 = 33;
        double c3 = 380926;
        return (double) 90 - (((a-c2) * (c1*c1*c1)) + ((b-c2) * (c1*c1)) + ((c-c2) * c1) + (d-c2)) / c3;
    }

    // expecting /YYYYXXXX
    public static double getLongitude(byte [] data) {
        double a = (double) data[5];
        double b = (double) data[6];
        double c = (double) data[7];
        double d = (double) data[8];
        return getLongitude(a, b, c, d) ;
    }

    // expecting YYYYXXXX
    public static double getLongitudeShort(byte [] data) {
        double a = (double) data[4];
        double b = (double) data[5];
        double c = (double) data[6];
        double d = (double) data[7];
        return getLongitude(a, b, c, d);
    }

    public static double getLongitude(double a, double b, double c, double d) {
        double c1 = 91;
        double c2 = 33;
        double c3 = 190463;
        return (double) -180 + (((a-c2) * (c1*c1*c1)) + ((b-c2) * (c1*c1)) + ((c-c2) * c1) + (d-c2)) / c3;
    }

    public static String convertDecimalToDDMMSSx(double value, String suffixValuesPosNeg) {
        return convertDecimalToFormat(value, suffixValuesPosNeg, "%02d%02d.%02d%c");
    }

    public static String convertDecimalToDDDMMSSx(double value, String suffixValuesPosNeg) {
        return convertDecimalToFormat(value, suffixValuesPosNeg, "%03d%02d.%02d%c");
    }

    private static String convertDecimalToFormat(double value, String suffixValuesPosNeg, String format) {
        double absolute = Math.abs(value);
        double degrees = Math.floor(absolute);
        double minutesNotTruncated = (absolute - degrees) * 60;
        double minutes = Math.floor(minutesNotTruncated);
        double seconds = Math.floor((minutesNotTruncated - minutes) * 60);
        char suffix;
        if (value < 0) {
            suffix = suffixValuesPosNeg.charAt(1);
        } else {
            suffix = suffixValuesPosNeg.charAt(0);
        }
        return String.format(format, (int)degrees, (int)minutes,(int)seconds,suffix);
    }

    public static String convertDDMMSSxToAPRSDDMMSSx(String value) {
        String newValue = value;
        if (value != null) {
            int index = value.indexOf(".");
            if (index != -1) {
                String prefix = value.substring(0, index+1); // take DDDMM.
                String seconds = value.substring(index+1, index+3); // take 2 digits
                String direction = value.substring(value.length()-1);
                try {
                    int secondsNumber = Integer.parseInt(seconds);
                    secondsNumber /= 60; // convert from seconds to hundreths of a minute
                    newValue = prefix+secondsNumber+direction;
                } catch (Exception e) {
                    // seconds was not a number - just return original string
                }
            }
        }
        return newValue;
    }
}
