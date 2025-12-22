package netcentral.transceiver.test.accessor;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

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
