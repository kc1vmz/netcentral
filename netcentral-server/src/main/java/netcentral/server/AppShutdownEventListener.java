package netcentral.server;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

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

