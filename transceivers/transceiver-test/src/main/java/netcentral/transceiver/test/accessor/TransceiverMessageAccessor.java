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

import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessage;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessageMany;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class TransceiverMessageAccessor {
    private static final Logger logger = LogManager.getLogger(TransceiverMessageAccessor.class);

    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    public void create(TransceiverMessage obj) {
        if ((obj == null) || (obj.getTransceiverId() == null)) {
            return;
        }
        if (!obj.getTransceiverId().equals(registeredTransceiverAccessor.getRegisteredTransceiverId())) {
            // not for us
            return;
        }
        logger.info(String.format("Message received from %s to %s: %s", obj.getCallsignFrom(), obj.getCallsignTo(), obj.getMessage()));
    }
    public void create(TransceiverMessageMany obj) {
        if ((obj == null) || (obj.getTransceiverId() == null)) {
            return;
        }
        if (!obj.getTransceiverId().equals(registeredTransceiverAccessor.getRegisteredTransceiverId())) {
            // not for us
            return;
        }
        for (String message : obj.getMessages()) {
            logger.info(String.format("Message received from %s to %s: %s", obj.getCallsignFrom(), obj.getCallsignTo(), message));
        }
    }
}
