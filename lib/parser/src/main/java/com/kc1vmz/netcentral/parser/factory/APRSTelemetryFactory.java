package com.kc1vmz.netcentral.parser.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSTelemetry;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSTelemetryFactory {
    private static final Logger logger = LogManager.getLogger(APRSTelemetryFactory.class);

    public static APRSTelemetry parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSTelemetry object");
        APRSTelemetry ret = new APRSTelemetry();

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        return ret;
    }

}
