package netcentral.transceiver.aprsis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.aprsis.accessor.APRSListenerAccessor;
import netcentral.transceiver.aprsis.accessor.APRSListenerState;
import netcentral.transceiver.aprsis.accessor.RegisteredTransceiverAccessor;
import netcentral.transceiver.aprsis.accessor.SendMessageQueueAccessor;
import netcentral.transceiver.aprsis.accessor.SendObjectQueueAccessor;
import netcentral.transceiver.aprsis.accessor.SendReportQueueAccessor;
import netcentral.transceiver.aprsis.accessor.StatisticsAccessor;


@Singleton
public class AppEventListener implements ApplicationEventListener<StartupEvent> {
    private static final Logger logger = LogManager.getLogger(AppEventListener.class);
    @Inject
    APRSListenerAccessor aprsListenerAccessor;
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
        new Thread(() -> {
            statisticsAccessor.setLastHeartBeatName2("Send messages");
            sendMessageQueueAccessor.sendMessages();
        }).start();
        new Thread(() -> {
            statisticsAccessor.setLastHeartBeatName3("Send objects");
            sendObjectQueueAccessor.sendObjects();
        }).start();
        new Thread(() -> {
            statisticsAccessor.setLastHeartBeatName4("Send reports");
            sendReportQueueAccessor.sendReports();
        }).start();
    }
}

