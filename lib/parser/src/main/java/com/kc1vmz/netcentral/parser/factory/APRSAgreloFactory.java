package com.kc1vmz.netcentral.parser.factory;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSAgrelo;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSAgreloFactory {
    private static final Logger logger = LogManager.getLogger(APRSStatusFactory.class);

    public static APRSAgrelo parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSAgrelo object");
        APRSAgrelo ret = new APRSAgrelo();
        ret.setHeader(header);
        byte [] bearing_bytes =  Arrays.copyOfRange(data, 1, 4);
        ret.setBearing(Integer.parseInt(new String(bearing_bytes)));
        int quality = (char) data[4]-'0';
        ret.setQuality(quality);

        ret.setDti(data[0]);
        ret.setData( Arrays.copyOfRange(data, 5, data.length));
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        return ret;
    }

}
