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

public class LatLonConverter {

    public static String convertLatitudeAPRS(String value) {
        value = Stripper.stripWhitespace(value);
        if (!value.contains(" ")) {
            if ((value.endsWith("N")) || (value.endsWith("S")) || (value.endsWith("E")) || (value.endsWith("W"))) {
                // already APRS
                return value;
            }
            // does not have a space in it - it is Decimal Degrees  ([-]XX.YYYY)
            return convertLatitudeDD(value);
        }

        if ((value.contains("°")) && (value.contains("'")) && (value.contains("\"")))  {
            // has spaces and a degree and minute and second - it is Degree Minutes Seconds
            return convertLatitudeDMS(value);
        }

        if ((value.contains("°")) && (value.contains("'")))  {
            // has spaces and a degree and minute - it is Degree Decimal Minutes
            return convertLatitudeDDM(value);
        }

        return value;
    }

    public static String convertLongitudeAPRS(String value) {
        value = Stripper.stripWhitespace(value);
        if (!value.contains(" ")) {
            if ((value.endsWith("N")) || (value.endsWith("S")) || (value.endsWith("E")) || (value.endsWith("W"))) {
                // already APRS
                return value;
            }
            // does not have a space in it - it is Decimal Degrees  -DDD.dddd
            return convertLongitudeDD(value);
        }

        if (value.contains(".")) {
            // has spaces and a period - it is Degree Decimal Minutes
            return convertLongitudeDDM(value);
        }

        return convertLongitudeDMS(value);
    }

    private static String convertLatitudeDD(String value) {
        return convertDD(value, "N", "S", false);
    }

    private static String convertLongitudeDD(String value) {
        return convertDD(value, "E", "W", true);
    }

    private static String convertDD(String value, String directionPositive, String directionNegative, boolean isThreeDigitDegrees) {
        if ((value == null) || (directionPositive == null) || (directionNegative == null)) {
            return "";
        }

        String ret = "";
        String direction = directionPositive;
        String [] part = value.split("\\.");
        if (part.length != 2) {
            return "";
        }

        int degrees = Integer.parseInt(part[0]);
        double decimal = 0;
        if (part[1].length() > 4) {
            decimal = Integer.parseInt(part[1].substring(0, 4));
        } else if (part[1].length() == 4) {
            decimal = Integer.parseInt(part[1]);
        } else {
            decimal = Integer.parseInt(part[1]);
            for (int i = part.length; i < 4; i++) {
                decimal *= 10;
            }
        }
        decimal /= 10000;

        if (degrees < 0) {
            direction = directionNegative;
            degrees *= -1;
        }
        Double minutesDouble = decimal*60;
        int minutes = minutesDouble.intValue();
        Double secondsDouble = (decimal*3600)-(60*minutes);
        Double percentDouble = (secondsDouble*100) / 60;
        int percent = percentDouble.intValue();

        if (percentDouble - percent >= 0.5) {
            percent++;
        }

        if (isThreeDigitDegrees) {
            ret = String.format("%03d%02d.%02d%s", degrees, minutes, percent, direction);
        } else {
            ret = String.format("%02d%02d.%02d%s", degrees, minutes, percent, direction);
        }
        return ret;
    }



    private static String convertLatitudeDDM(String value) {
        return convertDDM(value, false);
    }

    private static String convertLongitudeDDM(String value) {
        return convertDDM(value, true);
    }

    private static String convertDDM(String value, boolean isThreeDigitDegrees) {
        if (value == null) {
            return "";
        }

        String ret = "";
        String [] part = value.split("[ °']");
        if (part.length < 3) {
            return "";
        }

        int partIndex = 0;
        String part0 = part[partIndex];
        partIndex++;
        while (part[partIndex].length() == 0) {
            if (partIndex == part.length) {
                return "";  // could not assemble
            }
            partIndex++;
        }
        String part1 = part[partIndex];
        partIndex++;
        while (part[partIndex].length() == 0) {
            if (partIndex == part.length) {
                return "";  // could not assemble
            }
            partIndex++;
        }
        String direction = part[partIndex];

        int degrees = Integer.parseInt(part0);
        Double decimal = Double.parseDouble(part1);
        decimal /= 60;

        Double minutesDouble = decimal*60;
        int minutes = minutesDouble.intValue();
        Double secondsDouble = (decimal*3600)-(60*minutes);
        Double percentDouble = (secondsDouble*100) / 60;
        int percent = percentDouble.intValue();

        if (percentDouble - percent >= 0.5) {
            percent++;
        }

        if (isThreeDigitDegrees) {
            ret = String.format("%03d%02d.%02d%s", degrees, minutes, percent, direction);
        } else {
            ret = String.format("%02d%02d.%02d%s", degrees, minutes, percent, direction);
        }

        return ret;
    }


    private static String convertLatitudeDMS(String value) {
        return convertDMS(value, false);
    }

    private static String convertLongitudeDMS(String value) {
        return convertDMS(value, true);
    }

    private static String convertDMS(String value, boolean isThreeDigitDegrees) {
        if (value == null) {
            return "";
        }

        String ret = "";
        String [] part = value.split("[ °'\"]");
        if (part.length < 4) {
            return "";
        }

        int partIndex = 0;
        String degreeString = part[partIndex];
        partIndex++;
        while (part[partIndex].length() == 0) {
            if (partIndex == part.length) {
                return "";  // could not assemble
            }
            partIndex++;
        }
        String minuteString = part[partIndex];
        partIndex++;
        while (part[partIndex].length() == 0) {
            if (partIndex == part.length) {
                return "";  // could not assemble
            }
            partIndex++;
        }
        String secondsString = part[partIndex];
        partIndex++;
        while (part[partIndex].length() == 0) {
            if (partIndex == part.length) {
                return "";  // could not assemble
            }
            partIndex++;
        }
        String direction = part[partIndex];

        int degrees = Integer.parseInt(degreeString);
        Double decimalMinutes = Double.parseDouble(minuteString);
        decimalMinutes /= 60;
        Double decimalSeconds = Double.parseDouble(secondsString);
        decimalSeconds /= 3600;

        Double decimal = decimalMinutes + decimalSeconds;
        Double minutesDouble = decimal*60;
        int minutes = minutesDouble.intValue();
        Double secondsDouble = (decimal*3600)-(60*minutes);
        Double percentDouble = (secondsDouble*100) / 60;
        int percent = percentDouble.intValue();

        if (percentDouble - percent >= 0.5) {
            percent++;
        }

        if (isThreeDigitDegrees) {
            ret = String.format("%03d%02d.%02d%s", degrees, minutes, percent, direction);
        } else {
            ret = String.format("%02d%02d.%02d%s", degrees, minutes, percent, direction);
        }

        return ret;
    }
}
