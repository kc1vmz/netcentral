package com.kc1vmz.netcentral.logger;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.interfaces.APRSPacketInterface;
import com.kc1vmz.netcentral.aprsobject.object.APRSAgrelo;
import com.kc1vmz.netcentral.aprsobject.object.APRSItem;
import com.kc1vmz.netcentral.aprsobject.object.APRSMaidenheadLocatorBeacon;
import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.aprsobject.object.APRSMicE;
import com.kc1vmz.netcentral.aprsobject.object.APRSMicEOld;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.object.APRSPosition;
import com.kc1vmz.netcentral.aprsobject.object.APRSQuery;
import com.kc1vmz.netcentral.aprsobject.object.APRSStationCapabilities;
import com.kc1vmz.netcentral.aprsobject.object.APRSStatus;
import com.kc1vmz.netcentral.aprsobject.object.APRSTelemetry;
import com.kc1vmz.netcentral.aprsobject.object.APRSTest;
import com.kc1vmz.netcentral.aprsobject.object.APRSThirdPartyTraffic;
import com.kc1vmz.netcentral.aprsobject.object.APRSUserDefined;
import com.kc1vmz.netcentral.aprsobject.object.APRSWeatherReport;

public class APRSObjectPrinter {
    private static final Logger logger = LogManager.getLogger(APRSObjectPrinter.class);

    public static void print(APRSPacketInterface obj) {
        if (obj instanceof APRSAgrelo) {
            APRSObjectPrinter.print((APRSAgrelo) obj);
        } else if (obj instanceof APRSItem) {
            APRSObjectPrinter.print((APRSItem) obj);
        } else if (obj instanceof APRSMaidenheadLocatorBeacon) {
            APRSObjectPrinter.print((APRSMaidenheadLocatorBeacon) obj);
        } else if (obj instanceof APRSMessage) {
            APRSObjectPrinter.print((APRSMessage) obj);
        } else if (obj instanceof APRSMicE) {
            APRSObjectPrinter.print((APRSMicE) obj);
        } else if (obj instanceof APRSMicEOld) {
            APRSObjectPrinter.print((APRSMicEOld) obj);
        } else if (obj instanceof APRSObject) {
            APRSObjectPrinter.print((APRSObject) obj);
        } else if (obj instanceof APRSPosition) {
            APRSObjectPrinter.print((APRSPosition) obj);
        } else if (obj instanceof APRSQuery) {
            APRSObjectPrinter.print((APRSQuery) obj);
        } else if (obj instanceof APRSStationCapabilities) {
            APRSObjectPrinter.print((APRSStationCapabilities) obj);
        } else if (obj instanceof APRSStatus) {
            APRSObjectPrinter.print((APRSStatus) obj);
        } else if (obj instanceof APRSTelemetry) {
            APRSObjectPrinter.print((APRSTelemetry) obj);
        } else if (obj instanceof APRSTest) {
            APRSObjectPrinter.print((APRSTest) obj);
        } else if (obj instanceof APRSThirdPartyTraffic) {
            APRSObjectPrinter.print((APRSThirdPartyTraffic) obj);
        } else if (obj instanceof APRSUserDefined) {
            APRSObjectPrinter.print((APRSUserDefined) obj);
        } else if (obj instanceof APRSWeatherReport) {
            APRSObjectPrinter.print((APRSWeatherReport) obj);
        } 
    }


    private static StringBuilder getCommon(APRSPacketInterface obj) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Type: %s\n", obj.getClass().getSimpleName()));
        builder.append(String.format("From: %s\n", obj.getCallsignFrom()));
        builder.append(String.format("To  : %s\n", obj.getCallsignTo()));
        return builder;
    }

    /* APRSAgrelo */
    public static String toString(APRSAgrelo obj) {
        StringBuilder builder = getCommon(obj);
        builder.append(String.format("Bearing: %d\n", obj.getBearing()));
        builder.append(String.format("Quality: %d\n", obj.getQuality()));
        return builder.toString();
    }
    public static void print(APRSAgrelo obj) {
        logger.info(toString(obj));
    }

    /* APRSItem */
    public static String toString(APRSItem obj) {
        StringBuilder builder = getCommon(obj);
        builder.append(String.format("Lat: %s\n", obj.getLat()));
        builder.append(String.format("Lon: %s\n", obj.getLon()));
        return builder.toString();
    }
    public static void print(APRSItem obj) {
        logger.info(toString(obj));
    }
    
    /* APRSMaidenheadLocatorBeacon */
    public static String toString(APRSMaidenheadLocatorBeacon obj) {
        StringBuilder builder = getCommon(obj);
        builder.append(String.format("Grid locator: %s\n", obj.getGridLocator()));
        builder.append(String.format("Comment: %s\n", obj.getComment()));
        return builder.toString();
    }
    public static void print(APRSMaidenheadLocatorBeacon obj) {
        logger.info(toString(obj));
    }

    /* APRSMessage */
    public static String toString(APRSMessage obj) {
        StringBuilder builder = getCommon(obj);
        builder.append(String.format("Grid locator: %s\n", obj.getAddressee()));
        builder.append(String.format("Comment: %s\n", obj.getMessage()));
        builder.append(String.format("Message number: %s\n", obj.getMessageNumber()));
        builder.append(String.format("Bulletin?: %s\n", obj.isBulletin() ? "Yes" : "No"));
        builder.append(String.format("Group bulletin?: %s\n", obj.isGroupBulletin() ? "Yes" : "No"));
        builder.append(String.format("NWS bulletin?: %s\n", obj.isNWSBulletin() ? "Yes" : "No"));
        if (obj.isNWSBulletin()) {
            builder.append(String.format("NWS bulletin level: %s\n", obj.getNWSLevel()));
        }
        builder.append(String.format("Announcement?: %s\n", obj.isAnnouncement() ? "Yes" : "No"));
        builder.append(String.format("Query?: %s\n", obj.isQuery() ? "Yes" : "No"));
        builder.append(String.format("Must Ack?: %s\n", obj.isMustAck() ? "Yes" : "No"));
        return builder.toString();
    }
    public static void print(APRSMessage obj) {
        logger.info(toString(obj));
    }

    /* APRSMicE */
    public static String toString(APRSMicE obj) {
        StringBuilder builder = getCommon(obj);
        return builder.toString();
    }
    public static void print(APRSMicE obj) {
        logger.info(toString(obj));
    }

    /* APRSMicEOld */
    public static String toString(APRSMicEOld obj) {
        StringBuilder builder = getCommon(obj);
        return builder.toString();
    }
    public static void print(APRSMicEOld obj) {
        logger.info(toString(obj));
    }

    /* APRSObject */
    public static String toString(APRSObject obj) {
        StringBuilder builder = getCommon(obj);
        builder.append(String.format("Lat: %s\n", obj.getLat()));
        builder.append(String.format("Lon: %s\n", obj.getLon()));
        builder.append(String.format("Comment: %s\n", obj.getComment()));
        builder.append(String.format("Time: %s\n", obj.getTime()));
        builder.append(String.format("Local time: %s\n", (obj.getLdtime() != null) ? obj.getLdtime().toString() : "not specified"));
        return builder.toString();
    }
    public static void print(APRSObject obj) {
        logger.info(toString(obj));
    }

    /* APRSPosition */
    public static String toString(APRSPosition obj) {
        StringBuilder builder = getCommon(obj);
        builder.append(String.format("Lat: %s\n", obj.getLat()));
        builder.append(String.format("Lon: %s\n", obj.getLon()));
        builder.append(String.format("Comment: %s\n", obj.getComment()));
        builder.append(String.format("Time: %s\n", obj.getTime()));
        builder.append(String.format("Local time: %s\n", (obj.getLdtime() != null) ? obj.getLdtime().toString() : "not specified"));
        builder.append(String.format("Directivity: %s\n", obj.getDirectivity()));
        builder.append(String.format("Gain: %d\n", obj.getGain()));
        builder.append(String.format("Height: %d\n", obj.getHeight()));
        builder.append(String.format("Power: %d\n", obj.getPower()));
        builder.append(String.format("Range: %d\n", obj.getRange()));
        builder.append(String.format("Strength: %d\n", obj.getStrength()));
        return builder.toString();
    }
    public static void print(APRSPosition obj) {
        logger.info(toString(obj));
    }

    /* APRSQuery */
    public static String toString(APRSQuery obj) {
        StringBuilder builder = getCommon(obj);
        builder.append(String.format("Lat: %s\n", obj.getLat()));
        builder.append(String.format("Lon: %s\n", obj.getLon()));
        builder.append(String.format("Query type: %s\n", obj.getQueryType()));
        builder.append(String.format("Radius: %d\n", obj.getRadius()));
        return builder.toString();
    }
    public static void print(APRSQuery obj) {
        logger.info(toString(obj));
    }

    /* APRSStationCapabilities */
    public static String toString(APRSStationCapabilities obj) {
        StringBuilder builder = getCommon(obj);
        Map<String, String> kvPairs = obj.getValues();
        builder.append(String.format("Capability K:V pairs\n"));
        for (Map.Entry<String, String> entry : kvPairs.entrySet()) {
            if (entry.getValue() == null) {
                builder.append(String.format(" %s = %s\n", entry.getKey(), entry.getValue()));

            }
        }
        return builder.toString();
    }
    public static void print(APRSStationCapabilities obj) {
        logger.info(toString(obj));
    }

    /* APRSStatus */
    public static String toString(APRSStatus obj) {
        StringBuilder builder = getCommon(obj);
        builder.append(String.format("Status: %s\n", obj.getStatus()));
        builder.append(String.format("Time: %s\n", obj.getTime()));
        builder.append(String.format("Local time: %s\n", (obj.getLdtime() != null) ? obj.getLdtime().toString() : "not specified"));
        return builder.toString();
    }
    public static void print(APRSStatus obj) {
        logger.info(toString(obj));
    }

    /* APRSTelemetry */
    public static String toString(APRSTelemetry obj) {
        StringBuilder builder = getCommon(obj);
        return builder.toString();
    }
    public static void print(APRSTelemetry obj) {
        logger.info(toString(obj));
    }

    /* APRSTest */
    public static String toString(APRSTest obj) {
        StringBuilder builder = getCommon(obj);
        return builder.toString();
    }
    public static void print(APRSTest obj) {
        logger.info(toString(obj));
    }

    /* APRSThirdPartyTraffic */
    public static String toString(APRSThirdPartyTraffic obj) {
        StringBuilder builder = getCommon(obj);
        return builder.toString();
    }
    public static void print(APRSThirdPartyTraffic obj) {
        logger.info(toString(obj));
    }

    /* APRSUserDefined */
    public static String toString(APRSUserDefined obj) {
        StringBuilder builder = getCommon(obj);
        builder.append(String.format("Packet type: %s\n", obj.getPacketType()));
        builder.append(String.format("User id: %s\n", obj.getUserId()));
        return builder.toString();
    }
    public static void print(APRSUserDefined obj) {
        logger.info(toString(obj));
    }

    /* APRSWeatherReport */
    public static String toString(APRSWeatherReport obj) {
        StringBuilder builder = getCommon(obj);
        builder.append(String.format("Packet type: %s\n", obj.getBarometricPressure()));
        builder.append(String.format("Lat: %s\n", obj.getLat()));
        builder.append(String.format("Lon: %s\n", obj.getLon()));
        builder.append(String.format("Time: %s\n", obj.getTime()));
        builder.append(String.format("Local time: %s\n", (obj.getLdtime() != null) ? obj.getLdtime().toString() : "not specified"));
        builder.append(String.format("Temperature: %d\n", obj.getTemperature()));
        builder.append(String.format("Humidity: %d\n", obj.getHumidity()));
        builder.append(String.format("Barometric pressure: %d\n", obj.getBarometricPressure()));
        builder.append(String.format("Wind gust: %d\n", obj.getGust()));
        builder.append(String.format("Wind speed: %d\n", obj.getWindSpeed()));
        builder.append(String.format("Wind direction: %d\n", obj.getWindDirection()));
        builder.append(String.format("Rainfall last 1h: %d\n", obj.getRainfallLast1Hr()));
        builder.append(String.format("Rainfall last 24h: %d\n", obj.getRainfallLast24Hr()));
        builder.append(String.format("Rainfall since midnight: %d\n", obj.getRainfallSinceMidnight()));
        builder.append(String.format("Raw rain counter: %d\n", obj.getRawRainCounter()));
        builder.append(String.format("Luminosity: %d\n", obj.getLuminosity()));
        builder.append(String.format("Snowfall (last 24h): %d\n", obj.getSnowfallLast24Hr()));
        return builder.toString();
    }
    public static void print(APRSWeatherReport obj) {
        logger.info(toString(obj));
    }
}
