package netcentral.transceiver.kiss.accessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessage;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessageMany;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kiss.config.APRSConfiguration;

@Singleton
public class TransceiverMessageAccessor {
    private static final Logger logger = LogManager.getLogger(TransceiverMessageAccessor.class);

    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private APRSConfiguration aprsConfiguration;
    @Inject
    private SendMessageQueueAccessor sendMessageQueueAccessor;


    public void create(TransceiverMessage obj) {
        if ((obj == null) || (obj.getTransceiverId() == null)) {
            return;
        }
        if (!obj.getTransceiverId().equals(registeredTransceiverAccessor.getRegisteredTransceiverId())) {
            // not for us
            logger.warn("Wrong transceiver - " + obj.getTransceiverId());
            return;
        }

        if (obj.getCallsignFrom() == null) {
            obj.setCallsignFrom(aprsConfiguration.getCallsign());
        }

        sendMessageQueueAccessor.queueSendMessage(obj);
    }

    public void create(TransceiverMessageMany obj) {
        if ((obj == null) || (obj.getTransceiverId() == null)) {
            return;
        }
        if (!obj.getTransceiverId().equals(registeredTransceiverAccessor.getRegisteredTransceiverId())) {
            // not for us
            return;
        }
        if (obj.getCallsignFrom() == null) {
            obj.setCallsignFrom(aprsConfiguration.getCallsign());
        }

        sendMessageQueueAccessor.queueSendMessageMany(obj);
    }
}
