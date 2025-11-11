package com.kc1vmz.netcentral.parser.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSMaidenheadLocatorBeacon;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSMaidenheadLocatorBeaconFactory {
    private static final Logger logger = LogManager.getLogger(APRSMaidenheadLocatorBeaconFactory.class);

    public static APRSMaidenheadLocatorBeacon parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSMaidenheadLocatorBeacon object");

        APRSMaidenheadLocatorBeacon ret = new APRSMaidenheadLocatorBeacon();
        ret.setHeader(header);
        ret.setDti(data[0]);
        ret.setData(data);
        if (data[0] != '[') {
            throw new ParserException("Invalid maidenhead locator beacon data packet");
        }
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));
        
        String dataStr = new String(data);
        int indexEnd = dataStr.indexOf(']');
        if (indexEnd == -1) {
            // no end found
            ret.setGridLocator(dataStr.substring(1));
            ret.setComment(null);
        } else {
            ret.setGridLocator(dataStr.substring(1, indexEnd));
            ret.setComment(dataStr.substring(indexEnd+1));
        }
        return ret;
    }
}
