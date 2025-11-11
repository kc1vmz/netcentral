package netcentral.transceiver.kenwood;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kenwood.accessor.APRSListenerAccessor;
import netcentral.transceiver.kenwood.accessor.APRSListenerState;
import netcentral.transceiver.kenwood.accessor.RegisteredTransceiverAccessor;
import netcentral.transceiver.kenwood.accessor.SendMessageQueueAccessor;
import netcentral.transceiver.kenwood.accessor.SendObjectQueueAccessor;
import netcentral.transceiver.kenwood.accessor.SendReportQueueAccessor;
import netcentral.transceiver.kenwood.accessor.StatisticsAccessor;


@Singleton
public class AppEventListener implements ApplicationEventListener<StartupEvent> {
    private static final Logger logger = LogManager.getLogger(AppEventListener.class);

    @Inject
    private APRSListenerAccessor aprsListenerAccessor;
    @Inject
    private APRSListenerState aprsListenerState;
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private SendMessageQueueAccessor sendMessageQueueAccessor;
    @Inject
    private SendReportQueueAccessor sendReportQueueAccessor;
    @Inject
    private SendObjectQueueAccessor sendObjectQueueAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;

    @Override
    public void onApplicationEvent(StartupEvent event) {
        statisticsAccessor.markStartTime();
        aprsListenerState.setActive(true);
        startListenerThread();
    }

    void startListenerThread() {
        statisticsAccessor.setLastHeartBeatName1("APRS-IS Server Listener");
        new Thread(() -> {
            try {
                statisticsAccessor.markLastHeartBeatTime1();
                registeredTransceiverAccessor.registerTransceiver();
                aprsListenerAccessor.connectAndListen();
            } catch (InterruptedException e) {
                logger.error("Exception caught in listener thread", e);;
            }
        }).start();
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

