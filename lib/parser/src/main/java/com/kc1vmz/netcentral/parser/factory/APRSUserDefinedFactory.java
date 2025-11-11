package com.kc1vmz.netcentral.parser.factory;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSUserDefined;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSUserDefinedFactory {
    private static final Logger logger = LogManager.getLogger(APRSUserDefinedFactory.class);


    public static APRSUserDefined parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSUserDefined object");
        APRSUserDefined ret = new APRSUserDefined();

        ret.setHeader(header);
        ret.setDti(data[0]);
        char userIdChar = (char) data[1];
        ret.setUserId(""+userIdChar);
        char packetTypeChar = (char) data[2];
        ret.setPacketType(""+packetTypeChar);
        ret.setData( Arrays.copyOfRange(data, 3, data.length));
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        return ret;
    }
}
