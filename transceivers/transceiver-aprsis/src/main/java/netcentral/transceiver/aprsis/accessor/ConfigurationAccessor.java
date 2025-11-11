package netcentral.transceiver.aprsis.accessor;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.aprsis.config.APRSConfiguration;

@Singleton
public class ConfigurationAccessor {
    private static final Logger logger = LogManager.getLogger(ConfigurationAccessor.class);

    @Inject
    private APRSConfiguration aprsConfiguration;

    public Map<String, String> getConfigParameters() {
        Map<String, String> ret = new HashMap<>();
        ret.put("APRSQuery", aprsConfiguration.getQuery());
        return ret;
    }
    public void setConfigParameters( Map<String, String> values) {
        if (values == null) {
            logger.warn("No configuration parameters provided");
            return;
        }
        String query = values.get("APRSQuery");
        aprsConfiguration.setQuery(query);
    }
}
