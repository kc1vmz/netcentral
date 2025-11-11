package netcentral.transceiver.agw;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.agw.accessor.APRSListenerAccessor;
import netcentral.transceiver.agw.accessor.APRSListenerState;
import netcentral.transceiver.agw.accessor.RegisteredTransceiverAccessor;
import netcentral.transceiver.agw.accessor.SendMessageQueueAccessor;
import netcentral.transceiver.agw.accessor.SendObjectQueueAccessor;
import netcentral.transceiver.agw.accessor.SendReportQueueAccessor;
import netcentral.transceiver.agw.accessor.StatisticsAccessor;

@Singleton
public class AppEventListener implements ApplicationEventListener<StartupEvent> {
    private static final Logger logger = LogManager.getLogger(AppEventListener.class);

    @Inject
    private APRSListenerAccessor aprsListenerAccessor;
    @Inject
    private APRSListenerState aprsListenerState;
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
        statisticsAccessor.setLastHeartBeatName1("AGW Listener");
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

