package com.kc1vmz.netcentral.parser.factory;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.APRSItem;
import com.kc1vmz.netcentral.aprsobject.utils.CompressedDataFormatUtils;
import com.kc1vmz.netcentral.parser.exception.ParserException;

public class APRSItemFactory {
    private static final Logger logger = LogManager.getLogger(APRSItemFactory.class);


    public static APRSItem parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSItem object");
        APRSItem ret = new APRSItem();

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);

        int len = 0;
        String callsign = "";
        int fixedStart = 1 + len;
        boolean found = false;
        for (; fixedStart < 11; fixedStart++) {  // starting at 1, up to 9 characters for callsign
            if (data[fixedStart] == '!') {
                found = true;
                ret.setAlive(true);
                break;
            }
            if (data[fixedStart] == '_') {
                found = true;
                ret.setAlive(false);
                break;
            }
            callsign += (char) (data[fixedStart]);
        }

        if (!found) {
            throw new ParserException(String.format("Packet from %s does not properly describe alive or dead", ret.getCallsignFrom()));
        } else {
            ret.setCallsignFrom(callsign);  // might not be the same as the header callsign
        }

        if (data[fixedStart+1] == '/') {
            // compressed data format
            byte [] compressedData = Arrays.copyOfRange(data, fixedStart+1, fixedStart+14);
            String lat = CompressedDataFormatUtils.convertDecimalToDDMMSSx(CompressedDataFormatUtils.getLatitude(compressedData), "NS");
            ret.setLat(lat);
            String lon = CompressedDataFormatUtils.convertDecimalToDDDMMSSx(CompressedDataFormatUtils.getLongitude(compressedData), "EW");
            ret.setLon(lon);

            byte [] comment = Arrays.copyOfRange(data, fixedStart+14, data.length);
            ret.setComment(new String(comment));
        } else {
            // regular format
            byte [] lat = Arrays.copyOfRange(data, fixedStart+1, fixedStart+1+8);  // take 8 characters
            ret.setLat(new String(lat));
            //byte symTableId = data[messageIndex+8];
            byte [] lon = Arrays.copyOfRange(data, fixedStart+1+8+1, fixedStart+1+8+1+9);  // take 9 characters after the separator

            int messageStart = fixedStart+1+8+9+1;
            if ((lon[8] != 'E') && (lon[8] != 'W')) {
                lon = Arrays.copyOfRange(data, fixedStart+1+8+1+9, fixedStart+1+8+1+8);  // bad lon - take only 8
                messageStart--;
            }
            ret.setLon(new String(lon));

            byte [] comment = Arrays.copyOfRange(data, messageStart, data.length);
            ret.setComment(new String(comment));
        }

        return ret;
    }

}
