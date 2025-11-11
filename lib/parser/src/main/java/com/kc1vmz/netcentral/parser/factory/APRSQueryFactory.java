package com.kc1vmz.netcentral.parser.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSQuery;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSQueryFactory {
    private static final Logger logger = LogManager.getLogger(APRSQueryFactory.class);


    public static APRSQuery parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSQuery object");
        APRSQuery ret = new APRSQuery();

        ret.setHeader(header);
        ret.setData(data);
        ret.setDti(data[0]);
        String message = new String(data);
        if (!message.startsWith("?")) {
            throw new ParserException("Not a valid query packet");
        }
        message = message.substring(1);
        int index = message.indexOf("?");
        if (index == -1) {
            throw new ParserException("Not a valid query packet");
        }
        String queryType = message.substring(0, index);
        ret.setQueryType(queryType);
        message = message.substring(index+1);

        if (message.length() > 0) {
            String [] values = message.split(",");
            if ((values != null) && (values.length == 3)) {
                ret.setLat(values[0]);
                ret.setLon(values[1]);
                ret.setRadius(Integer.parseInt(values[2]));
            }
        }

        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        return ret;
    }
}
