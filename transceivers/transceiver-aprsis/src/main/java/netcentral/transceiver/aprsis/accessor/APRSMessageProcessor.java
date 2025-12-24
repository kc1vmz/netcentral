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
import com.kc1vmz.netcentral.common.exception.LoginFailureException;
import com.kc1vmz.netcentral.common.object.NetCentralServerUser;
import com.kc1vmz.netcentral.parser.APRSParser;
import com.kc1vmz.netcentral.parser.util.Stripper;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.ab0oo.aprs.parser.APRSPacket;
import net.ab0oo.aprs.parser.Digipeater;
import netcentral.transceiver.aprsis.client.NetCentralRESTClient;
import netcentral.transceiver.aprsis.config.APRSConfiguration;
import netcentral.transceiver.aprsis.config.NetCentralClientConfig;
import netcentral.transceiver.aprsis.config.RegisteredTransceiverConfig;
import netcentral.transceiver.aprsis.config.ThreadConfiguration;

@Singleton
public class APRSMessageProcessor {
    private static final Logger logger = LogManager.getLogger(APRSMessageProcessor.class);

    private NetCentralServerUser loginResponse = null;
    private RegisteredTransceiver registeredTransceiver = null;

    @Inject
    private ThreadConfiguration threadConfiguration;
    @Inject 
    private APRSMessageAccessor aprsMessageAccessor;
    @Inject 
    private APRSConfiguration aprsConfiguration;
    @Inject
    private NetCentralRESTClient netControlRESTClient;
    @Inject
    private NetCentralClientConfig netControlClientConfig;
    @Inject
    private RegisteredTransceiverConfig registeredTransceiverConfig;
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;


    private ExecutorService executorService = null;

    private synchronized RegisteredTransceiver getRegisteredTransceiver() {
        if (loginResponse == null) {
            try {
                loginResponse = netControlRESTClient.login(netControlClientConfig.getUsername(), netControlClientConfig.getPassword());
                if (loginResponse == null) {
                    // failed login
                    return null;
                }

                registeredTransceiver = netControlRESTClient.register(loginResponse.getAccessToken(), registeredTransceiverConfig.getName(), registeredTransceiverConfig.getDescription(),
                                                                    registeredTransceiverConfig.getType(), registeredTransceiverConfig.getPort());
            } catch (Exception e) {
                logger.error("Login failed to Net Control");
            }
        } else {
            if (registeredTransceiver == null) {
                try {
                    registeredTransceiver = netControlRESTClient.register(loginResponse.getAccessToken(), registeredTransceiverConfig.getName(), registeredTransceiverConfig.getDescription(),
                                                                    registeredTransceiverConfig.getType(), registeredTransceiverConfig.getPort());
                } catch (Exception e) {
                    logger.error("Error registering transceiver");
                }
            }
        }
        return registeredTransceiver;
    }

    public void processPacket(APRSPacket packet) {
        if (executorService == null) {
            logger.debug("Initializing thread pool for scan execution");
            executorService = Executors.newFixedThreadPool(threadConfiguration.getCount());
        }
        getRegisteredTransceiver();
        executorService.submit(() -> processPacketThread(packet));
    }


    private boolean processPacketThread(APRSPacket packet) {
        if (packet == null) {
            logger.debug("Null packet received");
            return true;
        }
        if (packet.getAprsInformation().hasFault()) {
            logger.warn(String.format("Packet received from %s has a fault: %s", packet.getSourceCall(), packet.getFaultReason()));
            return false;
        }

        logger.debug("Packet received from "+packet.getSourceCall());
        APRSPacketInterface parsedPacket;

        String pack = "";
        try {
            pack = packet.getDti()+packet.getOriginalString();
            parsedPacket = APRSParser.parseKenwood(pack.getBytes());
        } catch (Exception e) {
            logger.error("Cannot parse data: " + pack, e);
            return false;
        }

        ZonedDateTime heardTime = ZonedDateTime.now();

        processGenericObject(packet.getSourceCall(), packet.getDestinationCall(),parsedPacket, heardTime, packet.getDigipeaters(), packet.getIgate());

        return true;
    }

    private void processGenericObject(String callsignFrom, String callsignTo, APRSPacketInterface parsedPacket, ZonedDateTime heardTime, ArrayList<Digipeater> digipeaters, String iGate) {
        if ((callsignFrom == null) || (callsignTo == null) || (parsedPacket == null)) {
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

        if (getRegisteredTransceiver() == null) {
            logger.error("REGISTERED TRANSCEIVER IS NULL - THIS IS BAD");
            registeredTransceiverAccessor.resetRegisteredTransceiver();
            registeredTransceiverAccessor.registerTransceiver();  // reregister internally, here will get again
            return;
        }
        objectResource.setSource(getRegisteredTransceiver().getId());
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
            if ((objectResourceCreated == null) || (!objectResourceCreated.getId().isPresent())) {
                logger.error(String.format("Object not created %s to %s", callsignFrom, callsignTo));
            } else {
                logger.debug(String.format("Object with id %s created", objectResourceCreated.getId().get()));
            }
        } catch (LoginFailureException e) {
            logger.error("LoginFailureException caught - resetting registered transceiver", e);
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
            if (parsedPacket.getCallsignTo().equals(aprsConfiguration.getUsername())) {
                statisticsAccessor.incrementAcksRequested();
                // it was sent to us - we need to ack it
                String message = Stripper.stripWhitespace(parsedPacket.getMessageNumber());
                aprsMessageAccessor.sendAckMessage(parsedPacket.getCallsignTo(), parsedPacket.getCallsignFrom(), "ack"+message);
            }
        }
    }

    private List<String> processDigipeaters(ArrayList<Digipeater> digipeaters) {
        List<String> ret = new ArrayList<>();
        if (digipeaters != null) {
            for (Digipeater digipeater : digipeaters) {
/*                if ((digipeater.getCallsign().startsWith("WIDE1")) || (digipeater.getCallsign().startsWith("WIDE2")) || (digipeater.getCallsign().startsWith("WIDE3")) || (digipeater.getCallsign().isEmpty()) ||
                    (digipeater.getCallsign().startsWith("WIDE4")) || (digipeater.getCallsign().startsWith("WIDE5")) || (digipeater.getCallsign().startsWith("WIDE6")) || (digipeater.getCallsign().startsWith("WIDE7")) ||
                    (digipeater.getCallsign().startsWith("TCPIP")) ) {
                    continue;
                } */
                ret.add(digipeater.getCallsign());
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
