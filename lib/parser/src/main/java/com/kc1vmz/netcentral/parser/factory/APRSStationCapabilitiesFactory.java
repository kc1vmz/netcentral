package com.kc1vmz.netcentral.parser.factory;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSStationCapabilities;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSStationCapabilitiesFactory {
    private static final Logger logger = LogManager.getLogger(APRSStationCapabilitiesFactory.class);


    public static APRSStationCapabilities parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSStationCapabilities object");
        APRSStationCapabilities ret = new APRSStationCapabilities();

        ret.setHeader(header);
        ret.setData(data);
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));
        ret.setDti(data[0]);

        Map<String, String> pairs = new HashMap<>();

        if (data != null) {
            String dataStr = new String(data);
            String [] values = dataStr.split(",");
            for (int i = 0; i < values.length; i++) {
                String [] kv = values[i].split("=");
                if (kv.length == 1) {
                    // just a key
                    pairs.put(kv[0], null);
                } else if (kv.length == 2) {
                    // k:v pair
                    pairs.put(kv[0], kv[1]);
                } else {
                    throw new ParserException("Capability invalid value");
                }
            }
        }

        ret.setValues(pairs);

        return ret;
    }

}
