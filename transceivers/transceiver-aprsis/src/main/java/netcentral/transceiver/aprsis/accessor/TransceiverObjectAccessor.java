package netcentral.transceiver.aprsis.accessor;

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

import com.kc1vmz.netcentral.aprsobject.common.TransceiverObject;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.aprsis.config.APRSConfiguration;

@Singleton
public class TransceiverObjectAccessor {
    private static final Logger logger = LogManager.getLogger(TransceiverObjectAccessor.class);
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private SendQueueAccessor sendQueueAccessor;
    @Inject
    private APRSConfiguration aprsConfiguration;

    public void createObject(TransceiverObject obj) {
        if ((obj == null) || (obj.getTransceiverId() == null)) {
            return;
        }

        if (!obj.getTransceiverId().equals(registeredTransceiverAccessor.getRegisteredTransceiverId())) {
            logger.warn("Wrong transceiver - sent = " + obj.getTransceiverId() + " expecting = " + registeredTransceiverAccessor.getRegisteredTransceiverId());
            return;
        }

        if (obj.getCallsignFrom() == null) {
            obj.setCallsignFrom(aprsConfiguration.getUsername());
        }

        // need to send this object out via APRS
        logger.info(String.format("object received %s (%s): %s", obj.getName(), obj.isAlive() ? "Alive" : "Dead", obj.getMessage()));
        sendQueueAccessor.queueRequest(obj);
    }
}
