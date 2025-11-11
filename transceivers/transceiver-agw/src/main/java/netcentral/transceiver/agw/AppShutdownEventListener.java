package netcentral.transceiver.agw;


import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ShutdownEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.agw.accessor.APRSListenerState;
import netcentral.transceiver.agw.accessor.SendMessageQueueAccessor;
import netcentral.transceiver.agw.accessor.SendObjectQueueAccessor;
import netcentral.transceiver.agw.accessor.SendReportQueueAccessor;


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

