package com.kc1vmz.netcentral.parser.factory;

import java.time.ZonedDateTime;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.exception.APRSTimeConversionException;
import com.kc1vmz.netcentral.parser.exception.ParserException;
import com.kc1vmz.netcentral.aprsobject.object.APRSStatus;
import com.kc1vmz.netcentral.parser.util.APRSTime;
import com.kc1vmz.netcentral.parser.util.AgwHeaderParser;

public class APRSStatusFactory {
    private static final Logger logger = LogManager.getLogger(APRSStatusFactory.class);


    public static APRSStatus parse(byte [] header, byte [] data) throws ParserException {
        logger.debug("Parsing APRSStatus object");
        APRSStatus ret = new APRSStatus();
        ret.setHeader(header);
        ret.setDti(data[0]);
        ret.setData(data);
        if ((data == null) || (data.length < 1)) {
            throw new ParserException("Not an APRSStatus object");
        }
        ret.setCallsignFrom(AgwHeaderParser.getCallsignFrom(header));

        int statusTextIndex = 1;
        byte [] dhmTime_bytes = Arrays.copyOfRange(data, statusTextIndex, statusTextIndex+7);
        String dhmTime = new String(dhmTime_bytes);
        if (APRSTime.isTime(dhmTime)) {
            statusTextIndex += dhmTime.length();
            ret.setTime(dhmTime);
            try {
                ret.setLdtime(APRSTime.convertAPRSTimeToZonedDateTime(dhmTime));
            } catch (APRSTimeConversionException e) {
                logger.error(String.format("Cannot convert %s to LocalDateTime", dhmTime));
            }
        } else {
            ret.setLdtime(ZonedDateTime.now());
        }
        byte [] status_bytes = Arrays.copyOfRange(data, statusTextIndex, data.length);
        String status = new String(status_bytes);
        ret.setStatus(status);

        return ret;
    }

}
