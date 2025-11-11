package netcentral.server.utils;

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
