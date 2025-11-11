package netcentral.server.utils;

public class ConvertLonLat {
    private static double INVALID_VALUE = 1000;

    public static double convertLatitude(String latitude) {
        double ret = INVALID_VALUE;
        if (latitude.equals("9000.00N")) {
            return ret;
        }
        String degreesStr = null;
        String minutesStr = null;
        String secondsStr = null;
        try {
            if ((latitude != null) && (latitude.length() == 8) && (latitude.charAt(4) == '.') && 
                    ((latitude.charAt(7) == 'N') || (latitude.charAt(7) == 'S'))) {
                degreesStr = latitude.substring(0, 2);
                minutesStr = latitude.substring(2, 4);
                secondsStr = latitude.substring(5, 7);
                Double degrees = convertFromString(degreesStr);
                Double minutes = convertFromString(minutesStr);
                Double seconds = convertFromString(secondsStr);
                ret = degrees + (minutes / 60) + ((seconds * 60 / 100) / 3600);
                if (latitude.charAt(7) == 'S') {
                    ret *= -1;
                }
            }
        } catch (Exception e) {
            ret = INVALID_VALUE;
        }
        return ret;
    }

    private static Double convertFromString(String valueStr) {
        double x = 0;
        Double ret = x;

        try {
            ret = Double.parseDouble(valueStr);
        } catch (Exception e) {
        }
        return ret;
    }

    public static double convertLongitude(String longitude) {
        double ret = INVALID_VALUE;
        if (longitude.equals("18000.00W")) {
             return ret;
        }
        String degreesStr = null;
        String minutesStr = null;
        String secondsStr = null;
        try {
            if ((longitude != null) && (longitude.length() == 9) && (longitude.charAt(5) == '.') && 
                ((longitude.charAt(8) == 'E') || (longitude.charAt(8) == 'W'))) {
                degreesStr = longitude.substring(0, 3);
                minutesStr = longitude.substring(3, 5);
                secondsStr = longitude.substring(6, 8);
                Double degrees = convertFromString(degreesStr);
                Double minutes = convertFromString(minutesStr);
                Double seconds = convertFromString(secondsStr);
                ret = degrees + (minutes / 60) + ((seconds * 60 / 100) / 3600);
                if (longitude.charAt(8) == 'W') {
                    ret *= -1;
                }
            } else if ((longitude != null) && (longitude.length() == 8) && (longitude.charAt(4) == '.') && 
                ((longitude.charAt(7) == 'E') || (longitude.charAt(7) == 'W'))) {
                    // bad format missing hundreds column
                degreesStr = longitude.substring(0, 2);
                minutesStr = longitude.substring(2, 4);
                secondsStr = longitude.substring(5, 7);
                Double degrees = convertFromString(degreesStr);
                Double minutes = convertFromString(minutesStr);
                Double seconds = convertFromString(secondsStr);
                ret = degrees + (minutes / 60) + ((seconds * 60 / 100) / 3600);
                if (longitude.charAt(7) == 'W') {
                    ret *= -1;
                }
            }
        } catch (Exception e) {
            ret = INVALID_VALUE;
        }
        return ret;
    }
}
