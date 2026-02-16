package netcentral.transceiver.kiss.accessor;

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

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.constants.NetCentralToCallConstant;
import com.kc1vmz.netcentral.aprsobject.constants.NetCentralUserDefinedPacketConstant;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralReport;
import com.kc1vmz.netcentral.parser.util.APRSTime;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.kiss.client.KISSTCPIPClient;
import netcentral.transceiver.kiss.config.APRSConfiguration;
import netcentral.transceiver.kiss.config.FeatureConfiguration;
import netcentral.transceiver.kiss.config.TNCConfiguration;
import netcentral.transceiver.kiss.config.ThreadConfiguration;
import netcentral.transceiver.kiss.object.KISSPacket;
import netcentral.transceiver.kiss.util.AX25PacketBuilder;
import netcentral.transceiver.kiss.util.KissPacketBuilder;

@Singleton
public class APRSTCPIPListenerAccessor {
    private static final Logger logger = LogManager.getLogger(APRSTCPIPListenerAccessor.class);

    @Inject
    APRSMessageProcessor aprsMessageProcessor;
    @Inject
    APRSListenerState aprsListenerState;
    @Inject
    ThreadConfiguration threadConfiguration;
    @Inject
    FeatureConfiguration featureConfiguration;
    @Inject
    private TNCConfiguration tncConfiguration;
    @Inject
    private APRSConfiguration aprsConfiguration;
    @Inject
    private StatisticsAccessor statisticsAccessor;
    @Inject
    private PacketLoggerAccessor packetLoggerAccessor;

    private KISSTCPIPClient client = null;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public void sendQuery(String callsignFrom, String callsignTo, String queryType) {
        logger.debug(String.format("Sending query %s: %s", callsignTo, queryType));
        sendMessage(callsignFrom, callsignTo, String.format("?%-5s", queryType));
    }

    public void sendReport(String callsignFrom, APRSNetCentralReport obj) {
        logger.info(String.format("Sending report %s: %s", obj.getObjectName(), obj.getReportData()));

        KISSPacket packet = new KISSPacket();
        packet.setCallsignFrom(callsignFrom);
        packet.setCallsignTo(obj.getObjectName());
        packet.setApplicationName(NetCentralToCallConstant.TOCALL_NC1);
        packet.setDigipeaters(getDigipeaterList());
        packet.setData(new String(obj.getBytes()));
        packet.setValid(true);
        byte [] packetBytesAX = AX25PacketBuilder.buildReportPacket(packet, ""+NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_APRS_COMMAND+
                                                                        NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_USER_ID+
                                                                        NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_TYPE);
        byte [] packetBytes = KissPacketBuilder.build(packetBytesAX, (byte) 0);

        writeLock.lock();
        try {
            if (client != null) {
                client.write(packetBytes);
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementMessagesSent();
            } else {
                logger.error("client is null - cannot send");
            }
        } catch (Exception e) {
            logger.error("Exception caught sending report", e);
        } finally {
            writeLock.unlock();
        }
    }

    private List<String> getDigipeaterList() {
        List<String> ret = new ArrayList<>();
        String digipeaters = aprsConfiguration.getDigipeaters();
        if (digipeaters.contains(",")) {
            String [] digiArray = digipeaters.split(",");
            if (digiArray != null) {
                for (String digi : digiArray) {
                    ret.add(digi);
                }
            }
        } else {
            ret.add(digipeaters);
        }
        return ret;
    }

    public void sendMessage(String callsignFrom, String callsignTo, String messageText) {
        logger.info(String.format("Sending message from %s to %s: %s", callsignFrom, callsignTo, messageText));

        KISSPacket packet = new KISSPacket();
        packet.setCallsignFrom(callsignFrom);
        packet.setCallsignTo(callsignTo);
        packet.setApplicationName(NetCentralToCallConstant.TOCALL_NC1);
        packet.setDigipeaters(getDigipeaterList());
        packet.setData(messageText);
        packet.setValid(true);
        byte [] packetBytesAX = AX25PacketBuilder.buildPacket(packet, ":");
        byte [] packetBytes = KissPacketBuilder.build(packetBytesAX, (byte) 0);

        writeLock.lock();
        try {
            if (client != null) {
                client.write(packetBytes);
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementMessagesSent();                
            } else {
                logger.error("client is null - cannot send");
            }
        } catch (Exception e) {
            logger.error("Exception caught sending message", e);
        } finally {
            writeLock.unlock();
        }
    }

    public void sendBulletin(String callsignFrom, String bulletinId, String messageText) {
        logger.info(String.format("Sending bulletin from %s to %s: %s", callsignFrom, bulletinId, messageText));
        String callsignTo = bulletinId;
        KISSPacket packet = new KISSPacket();
        packet.setCallsignFrom(callsignFrom);
        packet.setCallsignTo(callsignTo);
        packet.setApplicationName(NetCentralToCallConstant.TOCALL_NC1);
        packet.setDigipeaters(getDigipeaterList());
        packet.setData(messageText);
        packet.setValid(true);
        byte [] packetBytesAX = AX25PacketBuilder.buildPacket(packet, ":");
        byte [] packetBytes = KissPacketBuilder.build(packetBytesAX, (byte) 0);

        writeLock.lock();
        try {
            if (client != null) {
                client.write(packetBytes);
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementMessagesSent();
            } else {
                logger.error("client is null - cannot send");
            }
        } catch (Exception e) {
            logger.error("Exception caught sending message", e);
        } finally {
            writeLock.unlock();
        }
    }

    public void sendObject(String objectName, String messageText, boolean alive, String lat, String lon) {
        logger.info(String.format("Sending object %s: %s", objectName,  messageText));
        KISSPacket packet = new KISSPacket();
        packet.setCallsignFrom(objectName); // aprsConfiguration.getCallsign());
        packet.setApplicationName(NetCentralToCallConstant.TOCALL_NC1);
        packet.setDigipeaters(getDigipeaterList());
        packet.setValid(true);

        String aprsMessage = null;

        String ud = "";
        if (alive){
            ud = "*";
        } else {
            ud = "_";
        }

        // time[7]lat[8]sym[/]lon[9]sym[>]meta[7]comment[36]
        String time = APRSTime.convertZonedDateTimeToDDHHMM(ZonedDateTime.now());

        aprsMessage = String.format(";%s%s%s%s/%s%s%s", String.format("%-9s", objectName), ud, time, lat, lon, "c", messageText);


        packet.setData(aprsMessage);

        byte [] packetBytesAX = AX25PacketBuilder.buildObjectPacket(packet);
        byte [] packetBytes = KissPacketBuilder.build(packetBytesAX, (byte) 0);

        writeLock.lock();
        try {
            if (client != null) {
                client.write(packetBytes);
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementObjectsSent();
            } else {
                logger.error("client is null - cannot send");
            }
        } catch (Exception e) {
            logger.error("Exception caught sending message", e);
        } finally {
            writeLock.unlock();
        }
    }

    public void connectAndListen() throws InterruptedException {

        if (featureConfiguration.isListener()) {
            boolean loop = true;
            while (loop) {
                try {
                    if (!aprsListenerState.isActive()) {
                        // reset this to active
                        aprsListenerState.setActive(true);
                    }
                    connectAndListenMain();
                } catch (InterruptedException e) {
                    loop = false;
                    break;
                } catch (Exception e) {
                    logger.error("Exception caught in connectAndListen", e);
                }
            }
        } else {
            sleepForever();
        }
    }

    // allows for debugging UI without adding a new APRS listener
    private void sleepForever() {
        while (true) {
            try {
                Thread.sleep(10000);                 // 10 sec
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * connect to the KISS TNC and process messages
     *
     * @param server
     * @param auth
     * @param query
     */
    private void connectAndListenMain()  throws InterruptedException {
        logger.info("ListenerTCPIPAccessor connecting");
        boolean readLockEnabled = featureConfiguration.isListenerReadLock();
        boolean sleepEnabled = featureConfiguration.isListenerSleep();

        try {
            client = new KISSTCPIPClient();
            client.connect(tncConfiguration.getHostname(), Integer.parseInt(tncConfiguration.getPort()));  

            Integer waitSeconds = threadConfiguration.getListenerPauseInSeconds();

            enableReception(client);

            // read response
            try {
                while (aprsListenerState.isActive()) {
                    if (sleepEnabled) {
                        try {
                            Thread.sleep(waitSeconds*1000);
                        } catch(InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    if (readLockEnabled) {
                        writeLock.lock();
                        try {
                            KISSPacket packet = client.listen();
                            packetLoggerAccessor.savePacket(packet.getPacket());
                            aprsMessageProcessor.processRawData(packet.getPacket());
                            aprsMessageProcessor.processPacket(packet);
                            statisticsAccessor.markLastReceivedTime();
                            statisticsAccessor.incrementObjectsReceived();
                        } catch (IOException | NullPointerException e) {
                            logger.error("Exception caught in KISS read loop", e);
                        } finally {
                            writeLock.unlock();
                        }
                    } else {
                        try {
                            KISSPacket packet = client.listen();
                            packetLoggerAccessor.savePacket(packet.getPacket());
                            aprsMessageProcessor.processRawData(packet.getPacket());
                            aprsMessageProcessor.processPacket(packet);
                            statisticsAccessor.markLastReceivedTime();
                            statisticsAccessor.incrementObjectsReceived();
                        } catch (IOException | NullPointerException e) {
                            logger.error("Exception caught in KISS read loop", e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.info("Exception caught in packet read loop", e);
            }

            //close client
            try {
                client.disconnect();
            } catch (Exception e) {
                logger.error("Exception caught", e);
            }

        } catch (Exception e) {
            logger.error("Exception caught creating TNC connection", e);
        }
    }

    private static void enableReception(KISSTCPIPClient client) {
        try {
            client.enableReception();
        } catch (IOException e) {
           logger.error("Exception caught", e);
        }
    }

}
