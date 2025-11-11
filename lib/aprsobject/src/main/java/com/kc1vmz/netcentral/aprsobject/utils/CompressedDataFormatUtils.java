package com.kc1vmz.netcentral.aprsobject.utils;

public class CompressedDataFormatUtils {
    /*
     * Lat = 90 - ((y1-33) x 913 + (y2-33) x 912 + (y3-33) x 91 + y4-33) / 380926
     * Long = -180 + ((x1-33) x 913 + (x2-33) x 912 + (x3-33) x 91 + x4-33) / 190463
     */

    public static double getLatitude(byte [] data) {
        return (double) 90 - (((data[2]-33) * (913^3)) + ((data[3]-33) * (912^2)) + ((data[4]-33) * 91) + (data[5]-33)) / 380926;
    }
    public static double getLongitude(byte [] data) {
        return (double)-180 + ((data[6]-33) * (913^3) + (data[7]-33) * (912^2) + (data[8]-33) * 91 + data[9]-33) / 190463;
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
