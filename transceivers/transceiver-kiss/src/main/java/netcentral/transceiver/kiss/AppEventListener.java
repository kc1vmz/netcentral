package netcentral.transceiver.kiss;

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
import netcentral.transceiver.kiss.accessor.APRSSerialListenerAccessor;
import netcentral.transceiver.kiss.accessor.APRSTCPIPListenerAccessor;
import netcentral.transceiver.kiss.accessor.APRSListenerState;
import netcentral.transceiver.kiss.accessor.RegisteredTransceiverAccessor;
import netcentral.transceiver.kiss.accessor.SendMessageQueueAccessor;
import netcentral.transceiver.kiss.accessor.SendObjectQueueAccessor;
import netcentral.transceiver.kiss.accessor.SendReportQueueAccessor;
import netcentral.transceiver.kiss.accessor.StatisticsAccessor;
import netcentral.transceiver.kiss.config.TNCConfiguration;


@Singleton
public class AppEventListener implements ApplicationEventListener<StartupEvent> {
    private static final Logger logger = LogManager.getLogger(AppEventListener.class);
    @Inject
    APRSSerialListenerAccessor aprsSerialListenerAccessor;
    @Inject
    APRSTCPIPListenerAccessor aprsTCPIPListenerAccessor;
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
    @Inject
    private TNCConfiguration tncConfiguration;


    @Override
    public void onApplicationEvent(StartupEvent event) {
        statisticsAccessor.markStartTime();
        aprsListenerState.setActive(true);
        startListenerThread();
    }

    void startListenerThread() {
        statisticsAccessor.setLastHeartBeatName1("Kiss Listener");
        String port = tncConfiguration.getPort();
        boolean serial = true;
        try {
            int portNumber = Integer.parseInt(port);
            if (portNumber > 0) {
                serial = false;
            }
        } catch (Exception e) {
        }

        if (serial) {
            new Thread(() -> {
                try {
                    statisticsAccessor.markLastHeartBeatTime1();
                    registeredTransceiverAccessor.registerTransceiver();
                    aprsSerialListenerAccessor.connectAndListen();
                } catch (InterruptedException e) {
                    logger.error("Exception caught in serial listener thread", e);;
                }
            }).start();
        } else {
            new Thread(() -> {
                try {
                    statisticsAccessor.markLastHeartBeatTime1();
                    registeredTransceiverAccessor.registerTransceiver();
                    aprsTCPIPListenerAccessor.connectAndListen();
                } catch (InterruptedException e) {
                    logger.error("Exception caught in TCPIP listener thread", e);;
                }
            }).start();
        }
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

