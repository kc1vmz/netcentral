package netcentral.transceiver.aprsis;


import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ShutdownEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.aprsis.accessor.APRSListenerState;
import netcentral.transceiver.aprsis.accessor.SendMessageQueueAccessor;
import netcentral.transceiver.aprsis.accessor.SendObjectQueueAccessor;
import netcentral.transceiver.aprsis.accessor.SendReportQueueAccessor;


@Singleton
public class AppShutdownEventListener implements ApplicationEventListener<ShutdownEvent> {
    @Inject
    APRSListenerState aprsListenerState;
    @Inject
    private SendMessageQueueAccessor sendMessageQueueAccessor;
    @Inject
    private SendReportQueueAccessor sendReportQueueAccessor;
    @Inject
    private SendObjectQueueAccessor sendObjectQueueAccessor;

    @Override
    public void onApplicationEvent(ShutdownEvent event) {
        aprsListenerState.setActive(false);
        sendReportQueueAccessor.shutdown();
        sendMessageQueueAccessor.shutdown();
        sendObjectQueueAccessor.shutdown();

        try {
            Thread.sleep(30000);  // may take up to 30 seconds
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

