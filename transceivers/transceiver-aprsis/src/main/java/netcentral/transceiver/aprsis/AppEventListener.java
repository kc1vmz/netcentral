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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.aprsis.accessor.APRSListenerAccessor;
import netcentral.transceiver.aprsis.accessor.APRSListenerState;
import netcentral.transceiver.aprsis.accessor.RegisteredTransceiverAccessor;
import netcentral.transceiver.aprsis.accessor.SendQueueAccessor;
import netcentral.transceiver.aprsis.accessor.StatisticsAccessor;


@Singleton
public class AppEventListener implements ApplicationEventListener<StartupEvent> {
    private static final Logger logger = LogManager.getLogger(AppEventListener.class);
    @Inject
    APRSListenerAccessor aprsListenerAccessor;
    @Inject
    APRSListenerState aprsListenerState;
    @Inject
    private SendQueueAccessor sendQueueAccessor;
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
            statisticsAccessor.setLastHeartBeatName2("Send requests");
            sendQueueAccessor.sendMessages();
        }).start();
    }
}

