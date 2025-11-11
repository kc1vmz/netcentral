package netcentral.transceiver.agw.accessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.TransceiverObject;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.agw.config.APRSConfiguration;

@Singleton
public class TransceiverObjectAccessor {
    private static final Logger logger = LogManager.getLogger(TransceiverObjectAccessor.class);
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private SendObjectQueueAccessor sendObjectQueueAccessor;
    @Inject
    private APRSConfiguration aprsConfiguration;

    public void createObject(TransceiverObject obj) {
        if ((obj == null) || (obj.getTransceiverId() == null)) {
            return;
        }

        if (!obj.getTransceiverId().equals(registeredTransceiverAccessor.getRegisteredTransceiverId())) {
            logger.warn("Wrong transceiver - " + obj.getTransceiverId());
            return;
        }

        if (obj.getCallsignFrom() == null) {
            obj.setCallsignFrom(aprsConfiguration.getCallsign());
        }

        // need to send this object out via APRS
        logger.info(String.format("object received %s (%s): %s", obj.getName(), obj.isAlive() ? "Alive" : "Dead", obj.getMessage()));
        sendObjectQueueAccessor.queueSendObject(obj);
    }
}
