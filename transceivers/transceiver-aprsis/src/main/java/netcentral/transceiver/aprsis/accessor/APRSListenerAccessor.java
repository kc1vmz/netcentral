package netcentral.transceiver.aprsis.accessor;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.constants.NetCentralToCallConstant;
import com.kc1vmz.netcentral.aprsobject.constants.NetCentralUserDefinedPacketConstant;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralReport;
import com.kc1vmz.netcentral.common.object.APRSAuthenticationInfo;
import com.kc1vmz.netcentral.common.object.InternetServer;
import com.kc1vmz.netcentral.parser.util.APRSTime;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.ab0oo.aprs.parser.APRSPacket;
import net.ab0oo.aprs.parser.Parser;
import netcentral.transceiver.aprsis.config.APRSConfiguration;
import netcentral.transceiver.aprsis.config.FeatureConfiguration;
import netcentral.transceiver.aprsis.config.ThreadConfiguration;
import netcentral.transceiver.aprsis.object.APRSServer;

@Singleton
public class APRSListenerAccessor {
    private static final Logger logger = LogManager.getLogger(APRSListenerAccessor.class);

    private PrintWriter sender = null;

    @Inject
    APRSMessageProcessor aprsMessageProcessor;
    @Inject
    APRSConfiguration aprsConfiguration;
    @Inject
    APRSListenerState aprsListenerState;
    @Inject
    APRSUtilityAccessor aprsUtilityAccessor;
    @Inject
    ThreadConfiguration threadConfiguration;
    @Inject
    FeatureConfiguration featureConfiguration;
    @Inject
    private StatisticsAccessor statisticsAccessor;
    @Inject
    private PacketLoggerAccessor packetLoggerAccessor;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();


    public void sendReport(String callsignFrom, APRSNetCentralReport obj) {
        String data = new String(obj.getBytes());
        logger.debug(String.format("Sending report %s: %s", obj.getObjectName(), data));
        String aprsMessage = String.format("%s>%s:%c%c%c%s\r\n", callsignFrom, NetCentralToCallConstant.TOCALL_NC1, 
                                NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_APRS_COMMAND,
                                NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_USER_ID,
                                NetCentralUserDefinedPacketConstant.USER_DEFINED_PACKET_TYPE, 
                                data);

        writeLock.lock();
        try {
            if (sender != null) {
                sender.write(aprsMessage);
                sender.flush();
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementMessagesSent();
            } else {
                logger.error("Sender is null - cannot send to APRS-IS server");
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void sendMessage(String callsignFrom, String callsignTo, String messageText) {
        logger.debug(String.format("Sending message from %s to %s: %s", callsignFrom, callsignTo, messageText));
        String aprsMessage = String.format("%s>%s::%-9s:%s\r\n", callsignFrom, NetCentralToCallConstant.TOCALL_NC1, callsignTo, messageText);

        writeLock.lock();
        try {
            if (sender != null) {
                sender.write(aprsMessage);
                sender.flush();
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementMessagesSent();
            } else {
                logger.error("Sender is null - cannot send to APRS-IS server");
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void sendBulletin(String callsignFrom, String bulletinId, String messageText) {
        logger.debug(String.format("Sending bulletin from %s to %s: %s", callsignFrom, bulletinId, messageText));
        String aprsMessage = callsignFrom + ">"+ NetCentralToCallConstant.TOCALL_NC1 +"::"+bulletinId+"     :"+messageText+"\r\n";

        writeLock.lock();
        try {
            if (sender != null) {
                sender.write(aprsMessage);
                sender.flush();
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementMessagesSent();
            } else {
                logger.error("Sender is null - cannot send to APRS-IS server");
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void sendObject(String objectName, String messageText, boolean alive, String lat, String lon) {
        logger.debug(String.format("Sending object %s: %s", objectName,  messageText));
        String aprsMessage = null;

        String ud = "";
        if (alive){
            ud = "*";
        } else {
            ud = "_";
        }

        // time[7]lat[8]sym[/]lon[9]sym[>]meta[7]comment[36]
        String time = APRSTime.convertZonedDateTimeToDDHHMM(ZonedDateTime.now());

        aprsMessage = String.format("%s>%s:;%s%s%s%s/%s%s%s\r\n", objectName,  NetCentralToCallConstant.TOCALL_NC1, String.format("%-9s", objectName), ud, time, lat, lon, "c", messageText);
        writeLock.lock();
        try {
            if (sender != null) {
                sender.write(aprsMessage);
                sender.flush();
                statisticsAccessor.markLastSentTime();
                statisticsAccessor.incrementObjectsSent();
            } else {
                logger.error("Sender is null - cannot send to APRS-IS server");
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void addAPRSIStoNetCentral() {
        InternetServer internetServer = new InternetServer(null, aprsConfiguration.getServer(), aprsConfiguration.getServer(), "", aprsConfiguration.getUsername(), aprsConfiguration.getQuery());
        aprsMessageProcessor.processInternetServer(internetServer);
    }

    public void connectAndListen() throws InterruptedException {
        addAPRSIStoNetCentral();
        APRSServer server = new APRSServer(aprsConfiguration.getServer(), aprsConfiguration.getPort());
        APRSAuthenticationInfo auth = new APRSAuthenticationInfo(aprsConfiguration.getUsername(), aprsConfiguration.getPassword());
        String query = aprsConfiguration.getQuery();
        if (featureConfiguration.isListener()) {
            boolean loop = true;
            while (loop) {
                try {
                    if (!aprsListenerState.isActive()) {
                        // reset this to active
                        aprsListenerState.setActive(true);
                    }
                    connectAndListen(server, auth, query);
                } catch (InterruptedException e) {
                    loop = false;
                    break;
                } catch (Exception e) {
                    logger.error("Exception caught in listener loop", e);
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
     * connect to the APRS-IS server and process messages
     *
     * @param server
     * @param auth
     * @param query
     */
    private void connectAndListen(APRSServer server, APRSAuthenticationInfo auth, String query)  throws InterruptedException {
        logger.info("ListenerAccessor connecting");
        boolean readLockEnabled = featureConfiguration.isListenerReadLock();
        boolean sleepEnabled = featureConfiguration.isListenerSleep();

        try {
            //Send Auth
            String authStr = aprsUtilityAccessor.generateAuthStr(auth, query);
            
            Socket sock = new Socket(server.getAddress(), server.getPort());
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream());
            sender = out;

            logger.info("Authenticating client with callsign: " + auth.getCallsign());
            logger.debug(authStr);

            out.println(authStr);
            out.flush();

            try {
                Thread.sleep(3000);                 //3 sec
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            //read response
            logger.info("Response received to authentication");
            String authenticationResponse = in.readLine();
            logger.info(authenticationResponse);

            Integer waitSeconds = threadConfiguration.getListenerPauseInSeconds();

            //read response
            try {
                while (aprsListenerState.isActive()) {
                    String line = null;

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
                            line = in.readLine();
                        } finally {
                            writeLock.unlock();
                        }
                    } else {
                        // do without the locking
                        line = in.readLine();
                    }

                    if (line == null) {
                        logger.error("Socket closed; line is null");
                        aprsListenerState.setActive(false);
                    } else {
                        logger.debug(line);
                        statisticsAccessor.markLastReceivedTime();
                        statisticsAccessor.incrementObjectsReceived();

                        // handle raw packet
                        packetLoggerAccessor.savePacket(line);
                        aprsMessageProcessor.processRawData(line);

                        APRSPacket packet = parsePacket(line);
                        if (packet != null) {
                             aprsMessageProcessor.processPacket(packet);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Exception caught in packet read loop", e);
            }

            //close socket
            try {
                in.close();
                out.close();
                sock.close();
            } catch (IOException e) {
                logger.error("IOException caught", e);
            }

        } catch (IOException e) {
            logger.error("IOException caught creating connection", e);
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

        try {
            return Parser.parse(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
