package netcentral.transceiver.test.accessor;

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
