package com.kc1vmz.netcentral.parser.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSMicEOld;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSMicEOldFactory {
    private static final Logger logger = LogManager.getLogger(APRSMicEOldFactory.class);


    public static APRSMicEOld parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSMicEOld object");
        APRSMicEOld ret = new APRSMicEOld();

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        return ret;
    }

}
