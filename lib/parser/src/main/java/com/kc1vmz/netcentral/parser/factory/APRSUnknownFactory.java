package com.kc1vmz.netcentral.parser.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSUnknown;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSUnknownFactory {
    private static final Logger logger = LogManager.getLogger(APRSUnknownFactory.class);


    public static APRSUnknown parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSUnknown object");
        APRSUnknown ret = new APRSUnknown();

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        return ret;
    }

}
