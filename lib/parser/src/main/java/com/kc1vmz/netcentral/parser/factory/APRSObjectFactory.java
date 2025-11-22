package com.kc1vmz.netcentral.parser.factory;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.utils.CompressedDataFormatUtils;
import com.kc1vmz.netcentral.parser.exception.APRSTimeConversionException;
import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.parser.util.APRSTime;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;
import com.kc1vmz.netcentral.parser.util.Stripper;

public class APRSObjectFactory {
    private static final Logger logger = LogManager.getLogger(APRSObjectFactory.class);


    public static APRSObject parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSObject object");
        APRSObject ret = new APRSObject();

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);

        byte [] objectNameBytes = Arrays.copyOfRange(data, 1, 10);
        String objectName = new String (objectNameBytes);
        objectName = Stripper.stripWhitespace(objectName);
        ret.setCallsignFrom((objectName.length()> 0) ? objectName : AgwHeaderParser.getCallsignFrom(header));

        switch (data[10]) {
            case '*' -> { ret.setAlive(true); }
            case '_' -> { ret.setAlive(false); }
            default -> {
                throw new ParserException("Packet does not properly describe alive or dead");
            }
        }

        byte [] time_bytes = Arrays.copyOfRange(data, 11, 18);
        ret.setTime(new String(time_bytes));
        try {
            ret.setLdtime(APRSTime.convertAPRSTimeToZonedDateTime(ret.getTime()));
        } catch (APRSTimeConversionException e) {
            logger.error("******** Bad time received - " + ret.getTime() + " **********");
            logger.error("Exception caught", e);
        }

        if (data[18] == '/') {
            // compressed data format
            byte [] compressedData = Arrays.copyOfRange(data, 18, 31);
            String lat = CompressedDataFormatUtils.convertDecimalToDDMMSSx(CompressedDataFormatUtils.getLatitude(compressedData), "NS");
            ret.setLat(lat);
            String lon = CompressedDataFormatUtils.convertDecimalToDDDMMSSx(CompressedDataFormatUtils.getLongitude(compressedData), "EW");
            ret.setLon(lon);

            byte [] comment = Arrays.copyOfRange(data, 30, data.length);
            ret.setComment(new String(comment));
        } else {
            // regular format
            byte [] lat = Arrays.copyOfRange(data, 18, 26);
            ret.setLat(new String(lat));
            //byte symTableId = data[messageIndex+8];
            byte [] lon = Arrays.copyOfRange(data, 28, 37);

            int messageStart = 38;
            if ((lon[8] != 'E') && (lon[8] != 'W')) {
                lon = Arrays.copyOfRange(data, 28, 36);
                messageStart--;
            }
            ret.setLon(new String(lon));

            byte [] comment = Arrays.copyOfRange(data, messageStart, data.length);
            ret.setComment(new String(comment));
        }

        return ret;
    }

}
