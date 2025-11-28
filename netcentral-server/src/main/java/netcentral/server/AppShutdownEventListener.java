package netcentral.server;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ShutdownEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.accessor.NetParticipantReminderAccessor;
import netcentral.server.accessor.NetQuestionReminderAccessor;
import netcentral.server.accessor.NetSchedulerAccessor;
import netcentral.server.accessor.ObjectBeaconAccessor;
import netcentral.server.accessor.ReportCleanupAccessor;
import netcentral.server.utils.APRSCreateObjectQueue;


@Singleton
public class AppShutdownEventListener implements ApplicationEventListener<ShutdownEvent> {
    @Inject
    ReportCleanupAccessor reportCleanupAccessor;
    @Inject
    private NetSchedulerAccessor netSchedulerAccessor;
    @Inject
    private ObjectBeaconAccessor priorityObjectBeaconAccessor;
    @Inject
    private NetParticipantReminderAccessor netParticipantReminderAccessor;
    @Inject
    private APRSCreateObjectQueue aprsCreateObjectQueue;
    @Inject
    private NetQuestionReminderAccessor netQuestionReminderAccessor;

    @Override
    public void onApplicationEvent(ShutdownEvent event) {
        reportCleanupAccessor.shutdown();
        netSchedulerAccessor.shutdown();
        priorityObjectBeaconAccessor.shutdownBeacon();
        netParticipantReminderAccessor.shutdown();
        aprsCreateObjectQueue.shutdown();
        netQuestionReminderAccessor.shutdown();

        try {
            Thread.sleep(30000);  // may take up to 30 seconds
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

