package netcentral.transceiver.agw.accessor;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralReport;
import com.kc1vmz.netcentral.parser.util.APRSTime;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.agw.client.AGWPEClient;
import netcentral.transceiver.agw.config.APRSConfiguration;
import netcentral.transceiver.agw.config.FeatureConfiguration;
import netcentral.transceiver.agw.config.TNCConfiguration;
import netcentral.transceiver.agw.config.ThreadConfiguration;
import netcentral.transceiver.agw.object.AgwCommand;
import netcentral.transceiver.agw.object.AgwResponse2;

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

    private AGWPEClient client = null;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public void sendReport(String callsignFrom, APRSNetCentralReport obj) {
        logger.info(String.format("Sending report %s: %s", obj.getObjectName(), obj.getReportData()));
        // TODO: NOW IMPLEMENTATION!!!

        statisticsAccessor.markLastSentTime();
        statisticsAccessor.incrementMessagesSent();

    }

    public void sendMessage(String callsignFrom, String callsignTo, String messageText) {
        logger.info(String.format("Sending message from %s to %s: %s", callsignFrom, callsignTo, messageText));
        String aprsMessage = ":"+String.format("%-9s", callsignTo)+":"+messageText; 
//        String aprsMessage = String.format("%-9s", callsignTo)+":"+messageText; 
// KC1VMZ-11>APRS,qAR,W1DMR-1::WHO-15   :K1FFK{K
// KC1VMZ-11>WIDE2::WHO-15   :AB1PH{1D
// KC1VMZ-11>APRS:::WHO-15   :AB1PH{18
// KC1VMZ-11>APRS::WHO-15   :K1FFK{1A

/*
 * [0L] KC1VMZ-11>APRS:WIDE2-1:WHO-15   :AB1PH{1C
   [0H] W1FDC>APSAR,W1BKW-3,UNCAN,WIDE2*:=4413.41N/06936.40Wd[:W1FDC
 */
        //[0L] KC1VMZ-11>WHO-15:WHO-15   :AB1PH{14
//        byte [] packet = buildPacket(callsignFrom, callsignTo, messageText);
        List<String> digipeaters = new ArrayList<>();
        digipeaters.add("WIDE1-1");
        digipeaters.add("WIDE2-1");
        writeLock.lock();
        try {
            if (client != null) {
                AgwCommand cmd = new AgwCommand((byte) (tncConfiguration.getChannel()), (byte)'K', callsignFrom, "APZ1241", aprsMessage, digipeaters);
                client.writeV(cmd);
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
        String aprsMessage = callsignFrom + ">APRS::"+bulletinId+"     :"+messageText;
        List<String> digipeaters = new ArrayList<>();
        digipeaters.add("WIDE1-1");
        digipeaters.add("WIDE2-1");

        writeLock.lock();
        String callsignTo = bulletinId;
        try {
            if (client != null) {
                AgwCommand cmd = new AgwCommand((byte) tncConfiguration.getChannel(), (byte)'K', callsignFrom, callsignTo, aprsMessage, digipeaters);
                client.writeV(cmd);
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
        List<String> digipeaters = new ArrayList<>();
        digipeaters.add("WIDE1-1");
        digipeaters.add("WIDE2-1");
        writeLock.lock();
        try {
            if (client != null) {
                AgwCommand cmd = new AgwCommand((byte) tncConfiguration.getChannel(), (byte)'K', aprsConfiguration.getCallsign(), null, new String(aprsMessage.getBytes()), digipeaters);
                client.writeV(cmd);
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
        int giveUpCountdown = 10;

        try {
            client = new AGWPEClient();
            client.connect(tncConfiguration.getHostname(), tncConfiguration.getPort(), packetLoggerAccessor);  
            int channel = tncConfiguration.getChannel();

            Integer waitSeconds = threadConfiguration.getListenerPauseInSeconds();

            enableReception(client, (byte) 0);

            // read response
            try {
                while (aprsListenerState.isActive() && (giveUpCountdown > 0)) {
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
                            List<AgwResponse2> responses = client.listen((byte) channel);
                            for (AgwResponse2 r : responses) {
                                aprsMessageProcessor.processPacket(r);
                                statisticsAccessor.markLastReceivedTime();
                                statisticsAccessor.incrementObjectsReceived();
                            }
                        } catch (IOException | NullPointerException e) {
                            logger.error("Exception caught in AGW read loop", e);
                            giveUpCountdown--;
                        } finally {
                            writeLock.unlock();
                        }
                    } else {
                        try {
                            List<AgwResponse2> responses = client.listen((byte) channel);
                            for (AgwResponse2 r : responses) {
                                aprsMessageProcessor.processPacket(r);
                                statisticsAccessor.markLastReceivedTime();
                                statisticsAccessor.incrementObjectsReceived();
                            }
                        } catch (IOException | NullPointerException e) {
                            logger.error("Exception caught in AGW read loop", e);
                            giveUpCountdown--;
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

            if (giveUpCountdown == 0) {
                throw new InterruptedException();
            }
        } catch (InterruptedException e) {
            logger.error("Exception caught creating TNC connection", e);
            throw e;
        } catch (Exception e) {
            logger.error("Exception caught creating TNC connection", e);
        }
    }

    private static void enableReception(AGWPEClient client, byte channel) {
        try {
            client.enableReception(channel);
        } catch (IOException e) {
           logger.error("Exception caught", e);
        }
    }
}
