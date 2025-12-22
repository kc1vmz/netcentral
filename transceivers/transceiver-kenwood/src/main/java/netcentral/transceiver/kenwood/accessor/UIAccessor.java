package netcentral.transceiver.kenwood.accessor;

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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kenwood.config.NetCentralClientConfig;
import netcentral.transceiver.kenwood.object.RegisterRequest;

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
