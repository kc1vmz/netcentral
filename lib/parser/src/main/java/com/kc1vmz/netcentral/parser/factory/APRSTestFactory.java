package com.kc1vmz.netcentral.parser.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSTest;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSTestFactory {
    private static final Logger logger = LogManager.getLogger(APRSTestFactory.class);


    public static APRSTest parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSTest object");
        APRSTest ret = new APRSTest();

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        return ret;
    }

}
