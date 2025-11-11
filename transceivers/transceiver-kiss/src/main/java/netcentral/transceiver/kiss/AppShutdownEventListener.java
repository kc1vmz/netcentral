package netcentral.transceiver.kiss;


import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ShutdownEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kiss.accessor.APRSListenerState;
import netcentral.transceiver.kiss.accessor.SendMessageQueueAccessor;
import netcentral.transceiver.kiss.accessor.SendObjectQueueAccessor;
import netcentral.transceiver.kiss.accessor.SendReportQueueAccessor;


@Singleton
public class AppShutdownEventListener implements ApplicationEventListener<ShutdownEvent> {
    @Inject
    private APRSListenerState aprsListenerState;
    @Inject
    private SendMessageQueueAccessor sendMessageQueueAccessor;
    @Inject
    private SendReportQueueAccessor sendReportQueueAccessor;
    @Inject
    private SendObjectQueueAccessor sendObjectQueueAccessor;

    @Override
    public void onApplicationEvent(ShutdownEvent event) {
        aprsListenerState.setActive(false);
        sendMessageQueueAccessor.shutdown();
        sendReportQueueAccessor.shutdown();
        sendObjectQueueAccessor.shutdown();

        try {
            Thread.sleep(30000);  // may take up to 30 seconds
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

