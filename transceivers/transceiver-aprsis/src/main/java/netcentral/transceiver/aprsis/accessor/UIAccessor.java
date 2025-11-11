package netcentral.transceiver.aprsis.accessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.aprsis.client.NetControlRESTClient;
import netcentral.transceiver.aprsis.config.NetControlClientConfig;
import netcentral.transceiver.aprsis.object.RegisterRequest;
import netcentral.transceiver.aprsis.object.SendMessageRequest;
import netcentral.transceiver.aprsis.object.User;

@Singleton
public class UIAccessor {
    @Inject
    private NetControlRESTClient netControlRESTClient;
    @Inject
    private NetControlClientConfig netControlConfig;
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;

    private static final Logger logger = LogManager.getLogger(UIAccessor.class);

    public void sendMessage(SendMessageRequest messageRequest) {
        try {
            String sourceId = registeredTransceiverAccessor.getRegisteredTransceiverId();
            if (sourceId == null) {
                return;
            }
            User loginResponse = netControlRESTClient.login(netControlConfig.getUsername(), netControlConfig.getPassword());
            netControlRESTClient.sendMessage(loginResponse.getAccessToken(), sourceId, messageRequest.callsign(), messageRequest.to_callsign(), messageRequest.message());
            netControlRESTClient.logout(loginResponse);
        } catch (Exception e) {
            logger.error("Error caught sending message", e);
        }
    }

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
