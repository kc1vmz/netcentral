package netcentral.transceiver.kiss.accessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.TransceiverReport;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kiss.config.APRSConfiguration;

@Singleton
public class TransceiverReportAccessor {
    private static final Logger logger = LogManager.getLogger(TransceiverReportAccessor.class);

    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private APRSConfiguration aprsConfiguration;
    @Inject
    private SendReportQueueAccessor sendReportQueueAccessor;


    public void create(TransceiverReport obj) {
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

        sendReportQueueAccessor.queueSendReport(obj);
    }
}
