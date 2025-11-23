package netcentral.transceiver.agw.accessor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.RegisteredTransceiver;
import com.kc1vmz.netcentral.aprsobject.interfaces.APRSPacketInterface;
import com.kc1vmz.netcentral.aprsobject.object.APRSAgrelo;
import com.kc1vmz.netcentral.aprsobject.object.APRSItem;
import com.kc1vmz.netcentral.aprsobject.object.APRSMaidenheadLocatorBeacon;
import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.aprsobject.object.APRSMicE;
import com.kc1vmz.netcentral.aprsobject.object.APRSMicEOld;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.object.APRSObjectResource;
import com.kc1vmz.netcentral.aprsobject.object.APRSPosition;
import com.kc1vmz.netcentral.aprsobject.object.APRSQuery;
import com.kc1vmz.netcentral.aprsobject.object.APRSRaw;
import com.kc1vmz.netcentral.aprsobject.object.APRSStationCapabilities;
import com.kc1vmz.netcentral.aprsobject.object.APRSStatus;
import com.kc1vmz.netcentral.aprsobject.object.APRSTelemetry;
import com.kc1vmz.netcentral.aprsobject.object.APRSThirdPartyTraffic;
import com.kc1vmz.netcentral.aprsobject.object.APRSUnknown;
import com.kc1vmz.netcentral.aprsobject.object.APRSUserDefined;
import com.kc1vmz.netcentral.aprsobject.object.APRSWeatherReport;
import com.kc1vmz.netcentral.parser.APRSParser;
import com.kc1vmz.netcentral.parser.util.Stripper;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.agw.auth.SessionAccessor;
import netcentral.transceiver.agw.client.NetCentralRESTClient;
import netcentral.transceiver.agw.config.APRSConfiguration;
import netcentral.transceiver.agw.config.NetCentralClientConfig;
import netcentral.transceiver.agw.config.RegisteredTransceiverConfig;
import netcentral.transceiver.agw.config.ThreadConfiguration;
import netcentral.transceiver.agw.exception.LoginFailureException;
import netcentral.transceiver.agw.object.AgwResponse2;
import netcentral.transceiver.agw.object.User;

@Singleton
public class APRSMessageProcessor {
    private static final Logger logger = LogManager.getLogger(APRSMessageProcessor.class);

    private User loginResponse = null;
    private RegisteredTransceiver registeredTransceiver = null;

    @Inject
    private ThreadConfiguration threadConfiguration;
    @Inject 
    private APRSMessageAccessor aprsMessageAccessor;
    @Inject
    private SessionAccessor sessionAccessor;
    @Inject
    private NetCentralRESTClient netControlRESTClient;
    @Inject
    private NetCentralClientConfig netControlClientConfig;
    @Inject
    private RegisteredTransceiverConfig registeredTransceiverConfig;
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private APRSConfiguration aprsConfiguration;
    @Inject
    private StatisticsAccessor statisticsAccessor;

    private ExecutorService executorService = null;

    private User systemUser = null;


    public void processPacket(AgwResponse2 packet) {
        if (systemUser == null) {
            systemUser = sessionAccessor.getSystemUser();
        }
        if (executorService == null) {
            logger.debug("Initializing thread pool for scan execution");
            executorService = Executors.newFixedThreadPool(threadConfiguration.getCount());
        }
        if (loginResponse == null) {
            try {
                loginResponse = netControlRESTClient.login(netControlClientConfig.getUsername(), netControlClientConfig.getPassword());
                if (loginResponse == null) {
                    // failed login
                    return;
                }

                registeredTransceiver = netControlRESTClient.register(loginResponse.getAccessToken(), registeredTransceiverConfig.getName(), registeredTransceiverConfig.getDescription(),
                                                                    registeredTransceiverConfig.getType(), registeredTransceiverConfig.getPort());
            } catch (Exception e) {
                logger.error("Login failed to Net Control");
            }
        }
        executorService.submit(() -> processPacketThread(packet));
    }

    private boolean processPacketThread(AgwResponse2 packet) {
        if (packet == null) {
            logger.debug("Null packet received");
            return true;
        }

        try {
            APRSPacketInterface parsedPacket;
            parsedPacket = APRSParser.parseAGWPE(packet.getPacketBytes());
            if (parsedPacket != null) {
                ZonedDateTime heardTime = ZonedDateTime.now();

                List<String> digipeaters = packet.getDigipeaters();

                processGenericObject(parsedPacket.getCallsignFrom(), parsedPacket.getCallsignTo(), parsedPacket, heardTime, digipeaters, null);
            }
        } catch (Exception e) {
            logger.error("Cannot parse data: " + new String(packet.getPacketBytes()), e);
            return false;
        }

        return true;
    }

    private void processGenericObject(String callsignFrom, String callsignTo, APRSPacketInterface parsedPacket, ZonedDateTime heardTime, List<String> digipeaters, String iGate) {
        if ((callsignFrom == null) || (parsedPacket == null)) { // || (callsignTo == null) 
            return;
        }

        APRSObjectResource objectResource = new APRSObjectResource();
        objectResource.setHeardTime(heardTime); 

        if (parsedPacket instanceof APRSItem) {
            objectResource.setInnerAPRSItem((APRSItem) parsedPacket);
        } else if (parsedPacket instanceof APRSMessage) {
            ackIfOurs((APRSMessage) parsedPacket);
            objectResource.setInnerAPRSMessage((APRSMessage) parsedPacket);
        } else if (parsedPacket instanceof APRSObject) {
            objectResource.setInnerAPRSObject((APRSObject) parsedPacket);
        } else if (parsedPacket instanceof APRSPosition) {
            objectResource.setInnerAPRSPosition((APRSPosition) parsedPacket);
        } else if (parsedPacket instanceof APRSQuery) {
            objectResource.setInnerAPRSQuery((APRSQuery) parsedPacket);
        } else if (parsedPacket instanceof APRSStationCapabilities) {
            objectResource.setInnerAPRSStationCapabilities((APRSStationCapabilities) parsedPacket);
        } else if (parsedPacket instanceof APRSStatus) {
            objectResource.setInnerAPRSStatus((APRSStatus) parsedPacket);
        } else if (parsedPacket instanceof APRSTelemetry) {
            objectResource.setInnerAPRSTelemetry((APRSTelemetry) parsedPacket);
        } else if (parsedPacket instanceof APRSThirdPartyTraffic) {
            objectResource.setInnerAPRSThirdPartyTraffic((APRSThirdPartyTraffic) parsedPacket);
        } else if (parsedPacket instanceof APRSUserDefined) {
            objectResource.setInnerAPRSUserDefined((APRSUserDefined) parsedPacket);
        } else if (parsedPacket instanceof APRSWeatherReport) {
            objectResource.setInnerAPRSWeatherReport((APRSWeatherReport) parsedPacket);
        } else if (parsedPacket instanceof APRSAgrelo) {
            objectResource.setInnerAPRSAgrelo((APRSAgrelo) parsedPacket);
        } else if (parsedPacket instanceof APRSMaidenheadLocatorBeacon) {
            objectResource.setInnerAPRSMaidenheadLocatorBeacon((APRSMaidenheadLocatorBeacon) parsedPacket);
        } else if (parsedPacket instanceof APRSMicE) {
            objectResource.setInnerAPRSMicE((APRSMicE) parsedPacket);
        } else if (parsedPacket instanceof APRSMicEOld) {
            objectResource.setInnerAPRSMicEOld((APRSMicEOld) parsedPacket);
        } else if (parsedPacket instanceof APRSRaw) {
            objectResource.setInnerAPRSRaw((APRSRaw) parsedPacket);
        } else if (parsedPacket instanceof APRSUnknown) {
            objectResource.setInnerAPRSUnknown((APRSUnknown) parsedPacket);
        }

        objectResource.setSource(registeredTransceiver.getId());
        List<String> iGateList = processIGate(iGate);
        if ((iGateList != null) && (!iGateList.isEmpty())) {
            objectResource.setInnerIgates(iGateList);
        }
        List<String> digipeaterList = processDigipeaters(digipeaters);
        if ((digipeaterList != null) && (!digipeaterList.isEmpty())) {
            objectResource.setInnerDigipeaters(digipeaterList);
        }

        try {
            APRSObjectResource objectResourceCreated = netControlRESTClient.create(objectResource, loginResponse.getAccessToken());
            if ((objectResourceCreated != null) && (objectResourceCreated.getId().isPresent())) {
                logger.debug(String.format("Object with id %s created", objectResourceCreated.getId().get()));
            } else {
                logger.error("Object not created");
            }
        } catch (LoginFailureException e) {
            loginResponse = null;
            registeredTransceiver = null;
        }

        if (registeredTransceiver == null) {
            registeredTransceiverAccessor.resetRegisteredTransceiver();
            registeredTransceiverAccessor.registerTransceiver();  // reregister internally, here will get again
        }
    }

    private void ackIfOurs(APRSMessage parsedPacket) {
        if (parsedPacket.isMustAck() && (parsedPacket.getMessageNumber() != null)) {
            if (parsedPacket.getCallsignTo().equals(aprsConfiguration.getCallsign())) {
                statisticsAccessor.incrementAcksRequested();
                // it was sent to us - we need to ack it
                String message = Stripper.stripWhitespace(parsedPacket.getMessageNumber());
                aprsMessageAccessor.sendAckMessage(parsedPacket.getCallsignTo(), parsedPacket.getCallsignFrom(), "ack"+message);
            }
        }
    }

    private List<String> processDigipeaters(List<String> digipeaters) {
        List<String> ret = new ArrayList<>();
        if (digipeaters != null) {
            for (String digipeater : digipeaters) {
/*                if ((digipeater.startsWith("WIDE1")) || (digipeater.startsWith("WIDE2")) || (digipeater.startsWith("WIDE3")) || (digipeater.isEmpty()) ||
                    (digipeater.startsWith("WIDE4")) || (digipeater.startsWith("WIDE5")) || (digipeater.startsWith("WIDE6")) || (digipeater.startsWith("WIDE7")) ||
                    (digipeater.startsWith("TCPIP"))) {
                    continue;
                } */ 
                ret.add(digipeater);
            }
            return ret;
        }
        return null;
    }

    private List<String> processIGate(String igate) {
        if (igate != null) {
            List<String> ret = new ArrayList<>();
            ret.add(igate);
            return ret;
        }
        return null;
    }
}
