package com.kc1vmz.netcentral.parser.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSThirdPartyTraffic;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSThirdPartyTrafficFactory {
    private static final Logger logger = LogManager.getLogger(APRSThirdPartyTrafficFactory.class);


    public static APRSThirdPartyTraffic parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSThirdPartyTraffic object");
        APRSThirdPartyTraffic ret = new APRSThirdPartyTraffic();

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        return ret;
    }

}
