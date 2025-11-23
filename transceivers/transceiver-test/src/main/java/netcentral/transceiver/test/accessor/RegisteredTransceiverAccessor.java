package netcentral.transceiver.test.accessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.LoginResponse;
import com.kc1vmz.netcentral.aprsobject.common.RegisteredTransceiver;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.test.client.NetCentralRESTClient;
import netcentral.transceiver.test.config.NetCentralClientConfig;
import netcentral.transceiver.test.config.RegisteredTransceiverConfig;
import netcentral.transceiver.test.object.RegisterRequest;

@Singleton
public class RegisteredTransceiverAccessor {
    @Inject
    private NetCentralRESTClient netControlRESTClient;
    @Inject
    private NetCentralClientConfig netControlConfig;
    @Inject
    private RegisteredTransceiverConfig registeredTransceiverConfig;

    private RegisteredTransceiver registeredTransceiver = null;

    private static final Logger logger = LogManager.getLogger(RegisteredTransceiverAccessor.class);

    public String registerTransceiver(RegisterRequest messageRequest) {
        String id = null;
        try {
            if (registeredTransceiver == null) {
                LoginResponse loginResponse = netControlRESTClient.login(netControlConfig.getUsername(), netControlConfig.getPassword());
                if (loginResponse == null) {
                    return null;
                }
                registeredTransceiver = netControlRESTClient.register(loginResponse.getAccessToken(), registeredTransceiverConfig.getName(), 
                            registeredTransceiverConfig.getDescription(), registeredTransceiverConfig.getType(), 
                            registeredTransceiverConfig.getPort());
                netControlRESTClient.logout(loginResponse);
                if (registeredTransceiver != null) {
                    id = registeredTransceiver.getId();
                }
            }
        } catch (Exception e) {
            logger.error("Error caught registering transceiver", e);
        }
        return id;
    }

    public String getRegisteredTransceiverId() {
        String id = null;
        if (registeredTransceiver != null)  {
            id = registeredTransceiver.getId();
        }
        return id;
    }

}
