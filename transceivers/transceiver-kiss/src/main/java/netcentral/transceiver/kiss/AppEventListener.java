package netcentral.transceiver.kiss;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kiss.accessor.APRSSerialListenerAccessor;
import netcentral.transceiver.kiss.accessor.APRSTCPIPListenerAccessor;
import netcentral.transceiver.kiss.accessor.APRSListenerState;
import netcentral.transceiver.kiss.accessor.RegisteredTransceiverAccessor;
import netcentral.transceiver.kiss.accessor.SendMessageQueueAccessor;
import netcentral.transceiver.kiss.accessor.SendObjectQueueAccessor;
import netcentral.transceiver.kiss.accessor.SendReportQueueAccessor;
import netcentral.transceiver.kiss.accessor.StatisticsAccessor;
import netcentral.transceiver.kiss.config.TNCConfiguration;


@Singleton
public class AppEventListener implements ApplicationEventListener<StartupEvent> {
    private static final Logger logger = LogManager.getLogger(AppEventListener.class);
    @Inject
    APRSSerialListenerAccessor aprsSerialListenerAccessor;
    @Inject
    APRSTCPIPListenerAccessor aprsTCPIPListenerAccessor;
    @Inject
    APRSListenerState aprsListenerState;
    @Inject
    private SendMessageQueueAccessor sendMessageQueueAccessor;
    @Inject
    private SendReportQueueAccessor sendReportQueueAccessor;
    @Inject
    private SendObjectQueueAccessor sendObjectQueueAccessor;
    @Inject
    RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;
    @Inject
    private TNCConfiguration tncConfiguration;


    @Override
    public void onApplicationEvent(StartupEvent event) {
        statisticsAccessor.markStartTime();
        aprsListenerState.setActive(true);
        startListenerThread();
    }

    void startListenerThread() {
        statisticsAccessor.setLastHeartBeatName1("Kiss Listener");
        String port = tncConfiguration.getPort();
        boolean serial = true;
        try {
            int portNumber = Integer.parseInt(port);
            if (portNumber > 0) {
                serial = false;
            }
        } catch (Exception e) {
        }

        if (serial) {
            new Thread(() -> {
                try {
                    statisticsAccessor.markLastHeartBeatTime1();
                    registeredTransceiverAccessor.registerTransceiver();
                    aprsSerialListenerAccessor.connectAndListen();
                } catch (InterruptedException e) {
                    logger.error("Exception caught in serial listener thread", e);;
                }
            }).start();
        } else {
            new Thread(() -> {
                try {
                    statisticsAccessor.markLastHeartBeatTime1();
                    registeredTransceiverAccessor.registerTransceiver();
                    aprsTCPIPListenerAccessor.connectAndListen();
                } catch (InterruptedException e) {
                    logger.error("Exception caught in TCPIP listener thread", e);;
                }
            }).start();
        }
        statisticsAccessor.setLastHeartBeatName2("Send messages");
        new Thread(() -> {
            sendMessageQueueAccessor.sendMessages();
        }).start();
        statisticsAccessor.setLastHeartBeatName3("Send objects");
        new Thread(() -> {
            sendObjectQueueAccessor.sendObjects();
        }).start();
        statisticsAccessor.setLastHeartBeatName4("Send reports");
        new Thread(() -> {
            sendReportQueueAccessor.sendReports();
        }).start();
    }
}

