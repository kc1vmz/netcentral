package com.kc1vmz.netcentral.aprsobject.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class PrettyZonedDateTimeFormatter {
    public static String format(ZonedDateTime dt) {
        if (dt == null) {
            return "";
        }
        return String.format("%d-%02d-%02d %02d:%02d:%02d", dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
    }
    public static String format(LocalDateTime dt) {
        if (dt == null) {
            return "";
        }
        return String.format("%d-%02d-%02d %02d:%02d:%02d", dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
    }
}
