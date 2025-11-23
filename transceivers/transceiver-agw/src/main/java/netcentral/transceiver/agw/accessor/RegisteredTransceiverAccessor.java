package netcentral.transceiver.agw.accessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.RegisteredTransceiver;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.agw.client.NetCentralRESTClient;
import netcentral.transceiver.agw.config.NetCentralClientConfig;
import netcentral.transceiver.agw.config.RegisteredTransceiverConfig;
import netcentral.transceiver.agw.object.RegisterRequest;
import netcentral.transceiver.agw.object.User;

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

    public String registerTransceiver() {
        String id = null;
        RegisterRequest messageRequest = new RegisterRequest(netControlConfig.getServer(), String.valueOf(netControlConfig.getPort()), netControlConfig.getUsername(), netControlConfig.getPassword());
        id = registerTransceiver(messageRequest) ;

        registeredTransceiver = new RegisteredTransceiver();
        registeredTransceiver.setId(id);
        registeredTransceiver.setName(registeredTransceiverConfig.getName());
        registeredTransceiver.setDescription(registeredTransceiverConfig.getDescription());
        return id;
    }

    public String registerTransceiver(RegisterRequest messageRequest) {
        String id = null;
        try {
            if ((registeredTransceiver == null) || (registeredTransceiver.getId() == null)) {
                User loginResponse = netControlRESTClient.login(netControlConfig.getUsername(), netControlConfig.getPassword());
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

    public boolean isTransceiver(String id) {
        if ((id == null) || (registeredTransceiver == null)){
            return false;
        }
        if (id.equals(registeredTransceiver.getId())) {
            return true;
        }
        return false;
    }

    public void resetRegisteredTransceiver() {
        registeredTransceiver = null;
    }

}
