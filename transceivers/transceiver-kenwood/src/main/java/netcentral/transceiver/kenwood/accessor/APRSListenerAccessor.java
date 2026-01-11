package netcentral.transceiver.kenwood.accessor;

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
import net.ab0oo.aprs.parser.APRSPacket;
import net.ab0oo.aprs.parser.Parser;
import netcentral.transceiver.kenwood.client.TNCClient;
import netcentral.transceiver.kenwood.config.APRSConfiguration;
import netcentral.transceiver.kenwood.config.FeatureConfiguration;
import netcentral.transceiver.kenwood.config.TNCConfiguration;
import netcentral.transceiver.kenwood.config.ThreadConfiguration;

@Singleton
public class APRSListenerAccessor {
    private static final Logger logger = LogManager.getLogger(APRSListenerAccessor.class);

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

    private TNCClient client = null;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public void sendReport(String callsignFrom, APRSNetCentralReport obj) {
        char end = 0x03;
        String data = new String(obj.getBytes());
        logger.info(String.format("Sending report %s: %s", obj.getObjectName(), data));
        String aprsMessage = String.format("%c%c%c%s\r",
                                NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_APRS_COMMAND,
                                NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_USER_ID,
                                NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_TYPE, 
                                data);        
        writeLock.lock();
        try {
            if (client != null) {
                client.write("convers\r".getBytes());
                pause(500);
                byte [] resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                client.write(aprsMessage.getBytes());
                pause(500);
                resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                String endStr = ""+end;
                client.write(endStr.getBytes());
                pause(500);
                resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementMessagesSent();
            } else {
                logger.error("client is null - cannot send");
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void sendMessage(String callsignFrom, String callsignTo, String messageText) {
        char end = 0x03;
        logger.info(String.format("Sending message from %s to %s: %s", callsignFrom, callsignTo, messageText));
        String aprsMessage = ":"+String.format("%-9s", callsignTo)+":"+messageText+"\r"; 
        writeLock.lock();
        try {
            if (client != null) {
                client.write("convers\r".getBytes());
                pause(500);
                byte [] resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                client.write(aprsMessage.getBytes());
                pause(500);
                resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                String endStr = ""+end;
                client.write(endStr.getBytes());
                pause(500);
                resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementMessagesSent();
            } else {
                logger.error("client is null - cannot send");
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void pause(int i) {
        try {
            Thread.sleep(i);
        } catch (Exception e) {
        }
    }

    public void sendBulletin(String callsignFrom, String bulletinId, String messageText) {
        logger.info(String.format("Sending bulletin from %s to %s: %s", callsignFrom, bulletinId, messageText));
        char end = 0x03;

        String aprsMessage = ":"+bulletinId+"     :"+messageText+"\r";

        writeLock.lock();
        try {
            if (client != null) {
                client.write("convers\r".getBytes());
                pause(500);
                byte [] resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                client.write(aprsMessage.getBytes());
                pause(500);
                resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                String endStr = ""+end;
                client.write(endStr.getBytes());
                pause(500);
                resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementMessagesSent();
            } else {
                logger.error("client is null - cannot send");
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void sendObject(String objectName, String messageText, boolean alive, String lat, String lon) {
        logger.info(String.format("Sending object %s: %s", objectName,  messageText));
        String aprsMessage = null;
        char end = 0x03;

        String ud = "";
        if (alive){
            ud = "*";
        } else {
            ud = "_";
        }

        // time[7]lat[8]sym[/]lon[9]sym[>]meta[7]comment[36]
        String time = APRSTime.convertZonedDateTimeToDDHHMM(ZonedDateTime.now());

        aprsMessage = String.format(";%s%s%s%s/%s%s%s\r", String.format("%-9s", objectName), ud, time, lat, lon, "c", messageText);
        writeLock.lock();
        try {
            if (client != null) {
                client.write("convers\r".getBytes());
                pause(500);
                byte [] resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                client.write(aprsMessage.getBytes());
                pause(500);
                resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                String endStr = ""+end;
                client.write(endStr.getBytes());
                pause(500);
                resp = client.read(); // clear the buffer
                if (resp != null) {
                    logger.info("Message cleared - " + new String (resp));
                }
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementObjectsSent();
            } else {
                logger.error("client is null - cannot send");
            }
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
     * connect to the Kenwood TNC and process messages
     *
     * @param server
     * @param auth
     * @param query
     */
    private void connectAndListenMain()  throws InterruptedException {
        logger.info("ListenerAccessor connecting");
        boolean readLockEnabled = featureConfiguration.isListenerReadLock();
        boolean sleepEnabled = featureConfiguration.isListenerSleep();

        try {
            client = new TNCClient();
            client.connect(tncConfiguration.getPort(), tncConfiguration.getBaudRate());  

            Integer waitSeconds = threadConfiguration.getListenerPauseInSeconds();

            enableReceptionKenwood(client, (byte) 0, aprsConfiguration.getCallsign());

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
                        } finally {
                            writeLock.unlock();
                        }
                    } else {
                        // do without the locking
                        byte[] bytes = client.read();
                        if ((bytes == null) || (bytes.length == 0)) {

                        } else {
                            String line = new String (bytes);
                            packetLoggerAccessor.savePacket(line);
                            aprsMessageProcessor.processRawData(line);

                            APRSPacket packet = parsePacket(line);
                            if (packet != null) {
                                aprsMessageProcessor.processPacket(packet);
                                statisticsAccessor.markLastReceivedTime();
                                statisticsAccessor.incrementObjectsReceived();
                            }
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


    /**
     * parse received packet
     *
     * @param line text representing APRS packet
     * @return APRSPacket or null
     */
    private APRSPacket parsePacket(String line) {
        if ((line == null) || (line.isEmpty())) {
            return null;
        }

        if (line.startsWith("#")) {
            return null;
        }

        int startIndex = 0;
        while (startIndex < line.length()) {
            if (line.charAt(startIndex) == '*') {
                startIndex++;
            } else if (line.substring(startIndex).startsWith("cmd:")) {
                startIndex+=4;
            } else {
                break;
            }
        }
        if (startIndex >= line.length()) {
            return null;
        }
        line = line.substring(startIndex);

        try {
            return Parser.parse(line);
        } catch (Exception e) {
            logger.error(String.format("Exception caught parsing **%s**", line), e);
        }
        return null;
    }

    private static void enableReceptionKenwood(TNCClient client, byte channel, String callsign) {
        try {
            client.enableReception(channel);
            client.enableCallsign(channel, callsign);
            client.enableUnprotoAPRS(channel,  NetCentralToCallConstant.TOCALL_NC1);
//            client.enableTransparentMode(channel);
        } catch (IOException e) {
           logger.error("Exception caught", e);
        }
    }
}
