package netcentral.transceiver.aprsis.accessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import jakarta.inject.Singleton;
import netcentral.transceiver.aprsis.object.APRSAuthenticationInfo;
import netcentral.transceiver.aprsis.object.SoftwareIdentity;

@Singleton
public class APRSUtilityAccessor {
    private static final Logger logger = LogManager.getLogger(APRSUtilityAccessor.class);

    public String generateGroupQuery(String group) {
        return String.format("g/%s", group);
    }

    /**
     * generate authentication string for APRS-IS server
     *
     * @param auth authentication information
     * @param filter filter to use on connect to receive packets
     * @return  authentication string
     */
    public String generateAuthStr(APRSAuthenticationInfo auth, String filter) {
        // user mycall[-ss] pass passcode[ vers softwarename softwarevers[ UDP udpport][ servercommand]]
        String ret;
        if ((filter != null) && (!filter.isEmpty())) {
            ret = String.format("user %s pass %s vers %s %s filter %s", auth.getCallsign(), auth.getPassword(), SoftwareIdentity.NAME, SoftwareIdentity.VERSION, filter);
        } else {
            ret = String.format("user %s pass %s vers %s %s", auth.getCallsign(), auth.getPassword(), SoftwareIdentity.NAME, SoftwareIdentity.VERSION);
        }
        logger.debug(String.format("auth string generated: %s",ret));
        return ret;
    }

}
