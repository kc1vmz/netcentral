package netcentral.transceiver.aprsis;

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
import netcentral.transceiver.aprsis.accessor.APRSListenerState;
import netcentral.transceiver.aprsis.accessor.SendQueueAccessor;



@Singleton
public class AppShutdownEventListener implements ApplicationEventListener<ShutdownEvent> {
    @Inject
    private APRSListenerState aprsListenerState;
    @Inject
    private SendQueueAccessor sendQueueAccessor;

    @Override
    public void onApplicationEvent(ShutdownEvent event) {
        aprsListenerState.setActive(false);
        sendQueueAccessor.shutdown();

        try {
            Thread.sleep(30000);  // may take up to 30 seconds
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

