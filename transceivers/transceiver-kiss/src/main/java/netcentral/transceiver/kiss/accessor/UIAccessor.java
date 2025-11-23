package netcentral.transceiver.kiss.accessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kiss.config.NetCentralClientConfig;
import netcentral.transceiver.kiss.object.RegisterRequest;

@Singleton
public class UIAccessor {
    @Inject
    private NetCentralClientConfig netControlConfig;
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;

    private static final Logger logger = LogManager.getLogger(UIAccessor.class);

    public void register(RegisterRequest messageRequest) {
        try {
            netControlConfig.setServer(messageRequest.fqdName());
            netControlConfig.setPassword(messageRequest.password());
            netControlConfig.setPort(Integer.parseInt(messageRequest.port()));
            netControlConfig.setUsername(messageRequest.username());

            String sourceId = registeredTransceiverAccessor.registerTransceiver(messageRequest);
            logger.info("Transceiver id = " + sourceId);
        } catch (Exception e) {
            logger.error("Error caught during transceiver registration", e);
        }
    }
}
