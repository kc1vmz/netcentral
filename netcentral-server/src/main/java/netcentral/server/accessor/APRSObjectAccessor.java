package netcentral.server.accessor;

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
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.RegisteredTransceiver;
import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.object.AGWRaw;
import com.kc1vmz.netcentral.aprsobject.object.APRSAgrelo;
import com.kc1vmz.netcentral.aprsobject.object.APRSItem;
import com.kc1vmz.netcentral.aprsobject.object.APRSMaidenheadLocatorBeacon;
import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.aprsobject.object.APRSMicE;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.object.APRSObjectResource;
import com.kc1vmz.netcentral.aprsobject.object.APRSPosition;
import com.kc1vmz.netcentral.aprsobject.object.APRSQuery;
import com.kc1vmz.netcentral.aprsobject.object.APRSRaw;
import com.kc1vmz.netcentral.aprsobject.object.APRSStationCapabilities;
import com.kc1vmz.netcentral.aprsobject.object.APRSStatus;
import com.kc1vmz.netcentral.aprsobject.object.APRSTelemetry;
import com.kc1vmz.netcentral.aprsobject.object.APRSTest;
import com.kc1vmz.netcentral.aprsobject.object.APRSThirdPartyTraffic;
import com.kc1vmz.netcentral.aprsobject.object.APRSUnknown;
import com.kc1vmz.netcentral.aprsobject.object.APRSUserDefined;
import com.kc1vmz.netcentral.aprsobject.object.APRSWeatherReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetAnnounceReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralObjectAnnounceReport;
import com.kc1vmz.netcentral.common.constants.NetCentralQueryType;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.RadioStyle;
import netcentral.server.enums.TrackedStationStatus;
import netcentral.server.enums.TrackedStationType;
import netcentral.server.enums.UserRole;
import netcentral.server.enums.WinlinkSessionState;
import netcentral.server.object.Callsign;
import netcentral.server.object.Net;
import netcentral.server.object.NetMessage;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.User;
import netcentral.server.object.WinlinkSessionCacheEntry;
import netcentral.server.object.request.ObjectCreateRequest;
import netcentral.server.record.aprs.AGWRawRecord;
import netcentral.server.record.aprs.APRSAgreloRecord;
import netcentral.server.record.aprs.APRSMaidenheadLocatorBeaconRecord;
import netcentral.server.record.aprs.APRSMessageRecord;
import netcentral.server.record.aprs.APRSObjectRecord;
import netcentral.server.record.aprs.APRSPositionRecord;
import netcentral.server.record.aprs.APRSQueryRecord;
import netcentral.server.record.aprs.APRSRawRecord;
import netcentral.server.record.aprs.APRSStatusRecord;
import netcentral.server.record.aprs.APRSUnknownRecord;
import netcentral.server.record.aprs.APRSWeatherReportRecord;
import netcentral.server.repository.aprs.AGWRawRepository;
import netcentral.server.repository.aprs.APRSAgreloRepository;
import netcentral.server.repository.aprs.APRSMaidenheadLocatorBeaconRepository;
import netcentral.server.repository.aprs.APRSMessageRepository;
import netcentral.server.repository.aprs.APRSObjectRepository;
import netcentral.server.repository.aprs.APRSPositionRepository;
import netcentral.server.repository.aprs.APRSQueryRepository;
import netcentral.server.repository.aprs.APRSRawRepository;
import netcentral.server.repository.aprs.APRSStatusRepository;
import netcentral.server.repository.aprs.APRSUnknownRepository;
import netcentral.server.repository.aprs.APRSWeatherReportRepository;
import netcentral.server.utils.Stripper;

@Singleton
public class APRSObjectAccessor {
    private static final Logger logger = LogManager.getLogger(APRSObjectAccessor.class);
    private static final int MAX_COMMENT_LENGTH = 200;

    @Inject
    private APRSPositionRepository aprsPositionRepository;
    @Inject
    private APRSAgreloRepository aprsAgreloRepository;
    @Inject
    private APRSMaidenheadLocatorBeaconRepository aprsMaidenheadLocatorBeaconRepository;
    @Inject
    private APRSUnknownRepository aprsUnknownRepository;
    @Inject
    private APRSWeatherReportRepository aprsWeatherReportRepository;
    @Inject
    private APRSMessageRepository aprsMessageRepository;
    @Inject
    private APRSQueryRepository aprsQueryRepository;
    @Inject
    private APRSRawRepository aprsRawRepository;
    @Inject
    private AGWRawRepository agwRawRepository;
    @Inject
    private TrackedStationAccessor trackedStationAccessor;
    @Inject
    private RadioCommandAccessor radioCommandAccessor;
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private NetAccessor netAccessor;
    @Inject
    private APRSObjectRepository aprsObjectRepository;
    @Inject
    private APRSStatusRepository aprsStatusRepository;
    @Inject
    private CallsignAccessor callsignAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;
    @Inject
    private PriorityObjectCommandAccessor priorityObjectCommandAccessor;
    @Inject
    private ToolsAccessor toolsAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private IgnoreStationAccessor ignoreStationAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;
    @Inject
    private NetCentralServerConfigAccessor netCentralServerConfigAccessor;
    @Inject
    private FederatedObjectReportIngestionAccessor federatedObjectIngestionAccessor;
    @Inject
    private ConfigParametersAccessor configParametersAccessor;
    @Inject
    private GeneralResourceObjectCommandAccessor generalResourceObjectCommandAccessor;
    @Inject
    private FederatedObjectReporterAccessor federatedObjectReporterAccessor;

    
    public APRSObjectResource create(User loggedInUser, APRSObjectResource obj) {

        statisticsAccessor.incrementObjectsReceived();
        statisticsAccessor.markLastReceivedTime();

        if (obj == null) {
            logger.debug("Object is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Object not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        if (!isTransceiverEnabledReceive(obj.getSource())) {
            logger.debug("Transceiver not enabled - "+ obj.getSource());
            throw new HttpStatusException(HttpStatus.PRECONDITION_FAILED, "Transceiver not enabled for receive");
        }

        if (obj.getInnerDigipeaters().isPresent()) {
            trackDigipeaters(loggedInUser, obj.getInnerDigipeaters().get(), obj.getHeardTime(), obj.getSource());
        }
        if (obj.getInnerIgates().isPresent()) {
            trackIgates(loggedInUser, obj.getInnerIgates().get(), obj.getHeardTime(), obj.getSource());
        }

        if (obj.getInnerAPRSAgrelo().isPresent()) {
            return createAPRSAgrelo(loggedInUser, obj.getId().get(), obj.getInnerAPRSAgrelo(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSItem().isPresent()) {
            return createAPRSItem(loggedInUser, obj.getId().get(), obj.getInnerAPRSItem(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSMaidenheadLocatorBeacon().isPresent()) {
            return createAPRSMaidenheadLocatorBeacon(loggedInUser, obj.getId().get(), obj.getInnerAPRSMaidenheadLocatorBeacon(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSMessage().isPresent()) {
            return createAPRSMessage(loggedInUser, obj.getId().get(), obj.getInnerAPRSMessage(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSMicE().isPresent()) {
            return createAPRSMicE(loggedInUser, obj.getId().get(), obj.getInnerAPRSMicE(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSObject().isPresent()) {
            return createAPRSObject(loggedInUser, obj.getId().get(), obj.getInnerAPRSObject(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSPosition().isPresent()) {
            return createAPRSPosition(loggedInUser, obj.getId().get(), obj.getInnerAPRSPosition(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSQuery().isPresent()) {
            return createAPRSQuery(loggedInUser, obj.getId().get(), obj.getInnerAPRSQuery(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSStationCapabilities().isPresent()) {
            return createAPRSStationCapabilities(loggedInUser, obj.getId().get(), obj.getInnerAPRSStationCapabilities(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSStatus().isPresent()) {
            return createAPRSStatus(loggedInUser, obj.getId().get(), obj.getInnerAPRSStatus(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSTelemetry().isPresent()) {
            return createAPRSTelemetry(loggedInUser, obj.getId().get(), obj.getInnerAPRSTelemetry(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSTest().isPresent()) {
            return createAPRSTest(loggedInUser, obj.getId().get(), obj.getInnerAPRSTest(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSThirdPartyTraffic().isPresent()) {
            return createAPRSThirdPartyTraffic(loggedInUser, obj.getId().get(), obj.getInnerAPRSThirdPartyTraffic(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSUnknown().isPresent()) {
            return createAPRSUnknown(loggedInUser, obj.getId().get(), obj.getInnerAPRSUnknown(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSUserDefined().isPresent()) {
            return createAPRSUserDefined(loggedInUser, obj.getId().get(), obj.getInnerAPRSUserDefined(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSWeatherReport().isPresent()) {
            return createAPRSWeatherReport(loggedInUser, obj.getId().get(), obj.getInnerAPRSWeatherReport(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAPRSRaw().isPresent()) {
            return createAPRSRaw(loggedInUser, obj.getId().get(), obj.getInnerAPRSRaw(), obj.getSource(), obj.getHeardTime());
        } else if (obj.getInnerAGWRaw().isPresent()) {
            return createAGWRaw(loggedInUser, obj.getId().get(), obj.getInnerAGWRaw(), obj.getSource(), obj.getHeardTime());
        }

        logger.debug("Object not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Object not created");
    }

    private void trackIgates(User loggedInUser, List<String> callsigns, ZonedDateTime heardTime, String source) {
        
        for (String callsign : callsigns) {
            if (!callsign.isBlank() && !callsign.isEmpty()) {
                trackStation(loggedInUser,  callsign, null, null, TrackedStationType.IGATE, null);
            }
        }
    }

    private void trackDigipeaters(User loggedInUser, List<String> callsigns, ZonedDateTime heardTime, String source) {
        String callsignLast = callsigns.get(callsigns.size()-1); // get the last
        trackStation(loggedInUser,  callsignLast, null, null, TrackedStationType.DIGIPEATER, null);
    }

    private boolean isTransceiverEnabledReceive(String transceiverId) {
        boolean enabled = false;
        try {
            RegisteredTransceiver registeredTransceiver = registeredTransceiverAccessor.get(null, transceiverId);
            if (registeredTransceiver != null) {
                enabled = registeredTransceiver.isEnabledReceive();
            }
        } catch (Exception e) {
            enabled = false;
        }
        return enabled;
    }

    private APRSObjectResource createAPRSWeatherReport(User loggedInUser, String id, Optional<APRSWeatherReport> innerAPRSWeatherReportOpt, String source, ZonedDateTime heardTime) {
        APRSWeatherReport innerAPRSWeatherReport = innerAPRSWeatherReportOpt.get();
        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSWeatherReport.getCallsignFrom())) {
            return null;
        }
        APRSWeatherReportRecord src = new APRSWeatherReportRecord(id, source, innerAPRSWeatherReport.getLdtime(), innerAPRSWeatherReport.getCallsignFrom(), innerAPRSWeatherReport.getLat(), innerAPRSWeatherReport.getLon(), innerAPRSWeatherReport.getTime(),
                                                                    innerAPRSWeatherReport.getWindDirection(), innerAPRSWeatherReport.getWindSpeed(),
                                                                    innerAPRSWeatherReport.getGust(), innerAPRSWeatherReport.getTemperature(),
                                                                    innerAPRSWeatherReport.getRainfallLast1Hr(), innerAPRSWeatherReport.getRainfallLast24Hr(),
                                                                    innerAPRSWeatherReport.getRainfallSinceMidnight(), innerAPRSWeatherReport.getRawRainCounter(),
                                                                    innerAPRSWeatherReport.getHumidity(), innerAPRSWeatherReport.getBarometricPressure(),
                                                                    innerAPRSWeatherReport.getLuminosity(), innerAPRSWeatherReport.getSnowfallLast24Hr());
        
        APRSWeatherReportRecord rec = aprsWeatherReportRepository.save(src);
        trackedStationAccessor.createWeatherStation(loggedInUser, rec.callsign_from(), innerAPRSWeatherReport.getLat(), innerAPRSWeatherReport.getLon());

        APRSWeatherReport weatherReport = new APRSWeatherReport(id, innerAPRSWeatherReport.getCallsignFrom(), innerAPRSWeatherReport.getCallsignTo(), innerAPRSWeatherReport.getLdtime().toString(),
                                                                innerAPRSWeatherReport.getWindDirection(), innerAPRSWeatherReport.getWindSpeed(),
                                                                innerAPRSWeatherReport.getGust(), innerAPRSWeatherReport.getTemperature(),
                                                                innerAPRSWeatherReport.getRainfallLast1Hr(), innerAPRSWeatherReport.getRainfallLast24Hr(),
                                                                innerAPRSWeatherReport.getRainfallSinceMidnight(), innerAPRSWeatherReport.getHumidity(), innerAPRSWeatherReport.getBarometricPressure(),
                                                                    innerAPRSWeatherReport.getLuminosity(), innerAPRSWeatherReport.getSnowfallLast24Hr(),
                                                                0, innerAPRSWeatherReport.getLat(), innerAPRSWeatherReport.getLon(), innerAPRSWeatherReport.getLdtime());
        changePublisherAccessor.publishWeatherReportUpdate( innerAPRSWeatherReport.getCallsignFrom(), ChangePublisherAccessor.CREATE,  weatherReport);
        return new APRSObjectResource(id, innerAPRSWeatherReport, source, heardTime);
    }

    private APRSObjectResource createAPRSUserDefined(User loggedInUser, String id, Optional<APRSUserDefined> innerAPRSUserDefinedOpt, String source, ZonedDateTime heardTime) {
        APRSUserDefined innerAPRSUserDefined = innerAPRSUserDefinedOpt.get();

        // could be for us in a federated setup
        if (netCentralServerConfigAccessor.isFederated()) {

            String dataStr = new String(innerAPRSUserDefined.getData());
            logger.info("User defined packet data - " + dataStr);

            if (federatedObjectIngestionAccessor.isFederatedUserDefinedPacket(innerAPRSUserDefined)) {
                federatedObjectIngestionAccessor.processFederatedPacket(loggedInUser, id, innerAPRSUserDefined, source, heardTime);
            }
        }

        return new APRSObjectResource(id, innerAPRSUserDefined, source, heardTime);
    }

    private APRSObjectResource createAPRSUnknown(User loggedInUser, String id, Optional<APRSUnknown> innerAPRSUnknownOpt, String source, ZonedDateTime heardTime) {
        APRSUnknown innerAPRSUnknown = innerAPRSUnknownOpt.get();
        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSUnknown.getCallsignFrom())) {
            return null;
        }
        APRSUnknownRecord src = new APRSUnknownRecord(id, source, heardTime, innerAPRSUnknown.getCallsignFrom(), innerAPRSUnknown.getCallsignTo(), new String(innerAPRSUnknown.getData()));
        aprsUnknownRepository.save(src);
        return new APRSObjectResource(id, innerAPRSUnknown, source, heardTime);
    }


    private APRSObjectResource createAPRSThirdPartyTraffic(User loggedInUser, String id, Optional<APRSThirdPartyTraffic> innerAPRSThirdPartyTrafficOpt, String source, ZonedDateTime heardTime) {
        APRSThirdPartyTraffic innerAPRSThirdPartyTraffic = innerAPRSThirdPartyTrafficOpt.get();

        return new APRSObjectResource(id, innerAPRSThirdPartyTraffic, source, heardTime);
    }


    private APRSObjectResource createAPRSTest(User loggedInUser, String id, Optional<APRSTest> innerAPRSTestOpt, String source, ZonedDateTime heardTime) {
        APRSTest innerAPRSTest = innerAPRSTestOpt.get();

        return new APRSObjectResource(id, innerAPRSTest, source, heardTime);
    }


    private APRSObjectResource createAPRSTelemetry(User loggedInUser, String id, Optional<APRSTelemetry> innerAPRSTelemetryOpt, String source, ZonedDateTime heardTime) {
        APRSTelemetry innerAPRSTelemetry = innerAPRSTelemetryOpt.get();
        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSTelemetry.getCallsignFrom())) {
            return null;
        }
        trackStation(loggedInUser,  innerAPRSTelemetry.getCallsignFrom(), null, null, TrackedStationType.UNKNOWN, null);
        return new APRSObjectResource(id, innerAPRSTelemetry, source, heardTime);
    }


    private APRSObjectResource createAPRSStatus(User loggedInUser, String id, Optional<APRSStatus> innerAPRSStatusOpt, String source, ZonedDateTime heardTime) {
        APRSStatus innerAPRSStatus = innerAPRSStatusOpt.get();
        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSStatus.getCallsignFrom())) {
            return null;
        }
        TrackedStationType type = TrackedStationType.UNKNOWN;
        if (innerAPRSStatus.getStatus() != null) {
            if (innerAPRSStatus.getStatus().toUpperCase().contains("BBS")) {
                type = TrackedStationType.BBS;
            } else if (innerAPRSStatus.getStatus().toUpperCase().contains("(D-Star)")) {
                type = TrackedStationType.DSTAR;
            } else if (innerAPRSStatus.getStatus().toUpperCase().contains("DIGI")) {
                type = TrackedStationType.DIGIPEATER;
            } else if (innerAPRSStatus.getStatus().toUpperCase().contains("MMDVM")) {
                type = TrackedStationType.MMDVM;
            }
        }
        trackStation(loggedInUser,  innerAPRSStatus.getCallsignFrom(), null, null, type, innerAPRSStatus.getStatus());

        APRSStatusRecord src = new APRSStatusRecord(id, source, heardTime, innerAPRSStatus.getCallsignFrom(), innerAPRSStatus.getTime(), 
                                                                innerAPRSStatus.getLdtime(), innerAPRSStatus.getStatus());
        aprsStatusRepository.save(src);
        return new APRSObjectResource(id, innerAPRSStatus, source, heardTime);
    }


    private APRSObjectResource createAPRSStationCapabilities(User loggedInUser, String id, Optional<APRSStationCapabilities> innerAPRSStationCapabilitiesOpt, String source, ZonedDateTime heardTime) {
        APRSStationCapabilities innerAPRSStationCapabilities = innerAPRSStationCapabilitiesOpt.get();
        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSStationCapabilities.getCallsignFrom())) {
            return null;
        }
        trackStation(loggedInUser,  innerAPRSStationCapabilities.getCallsignFrom(), null, null, TrackedStationType.UNKNOWN, null);

        return new APRSObjectResource(id, innerAPRSStationCapabilities, source, heardTime);
    }


    private APRSObjectResource createAPRSQuery(User loggedInUser, String id, Optional<APRSQuery> innerAPRSQueryOpt, String source, ZonedDateTime heardTime) {
        APRSQuery innerAPRSQuery = innerAPRSQueryOpt.get();
        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSQuery.getCallsignFrom())) {
            return null;
        }
        APRSQueryRecord src = new APRSQueryRecord(id, source, heardTime, innerAPRSQuery.getCallsignFrom(), innerAPRSQuery.getQueryType(), innerAPRSQuery.getLat(), innerAPRSQuery.getLon(), innerAPRSQuery.getRadius());
        
        aprsQueryRepository.save(src);
        trackStation(loggedInUser,  innerAPRSQuery.getCallsignFrom(), innerAPRSQuery.getLat(), innerAPRSQuery.getLon(), TrackedStationType.UNKNOWN, null);

        return new APRSObjectResource(id, innerAPRSQuery, source, heardTime);
    }

    private APRSObjectResource createAPRSPosition(User loggedInUser, String id, Optional<APRSPosition> innerAPRSPositionOpt, String source, ZonedDateTime heardTime) {
        APRSPosition innerAPRSPosition = innerAPRSPositionOpt.get();
        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSPosition.getCallsignFrom())) {
            return null;
        }
        String comment = innerAPRSPosition.getComment();
        if ((comment != null) && (comment.length() > MAX_COMMENT_LENGTH)) {
            logger.warn(String.format("Position comment too long from %s - %s", innerAPRSPosition.getCallsignFrom(), comment));
            comment = comment.substring(0, MAX_COMMENT_LENGTH);
        }
        APRSPositionRecord src = new APRSPositionRecord(id, source, heardTime, innerAPRSPosition.getCallsignFrom(), innerAPRSPosition.getLat(), innerAPRSPosition.getLon(), innerAPRSPosition.getTime(),
                                                    innerAPRSPosition.getPower(), innerAPRSPosition.getHeight(), innerAPRSPosition.getGain(), innerAPRSPosition.getRange(), 
                                                    innerAPRSPosition.getStrength(), innerAPRSPosition.getDirectivity(), comment);
        aprsPositionRepository.save(src);
        TrackedStationType type = TrackedStationType.UNKNOWN;
        if (innerAPRSPosition.isHasWeatherReport()) {
            type = TrackedStationType.WEATHER;
            storeWeatherReport(loggedInUser, innerAPRSPosition, source, heardTime);
        }
        trackStation(loggedInUser,  innerAPRSPosition.getCallsignFrom(), innerAPRSPosition.getLat(), innerAPRSPosition.getLon(), type, null);
        return new APRSObjectResource(id, innerAPRSPosition, source, heardTime);
    }


    private void storeWeatherReport(User loggedInUser, APRSPosition innerAPRSPosition, String source, ZonedDateTime heardTime) {
        Integer wind_dir = null, wind_sp=null, gust = null, temp = null, rain_1 = null, rain24 = null, rainmid = null, rainraw = null, hum = null, baro = null, lum = null, snow24 = null;
        String report = innerAPRSPosition.getWeatherReport();
        int index = 0;
        boolean bWarnNull = false;

        String checkWindSpeedDirectionFlag = report.substring(3, 4);
        if (checkWindSpeedDirectionFlag.equals("/")) {
            // weather report is prefixed with SSS/DDD
            wind_dir = getWeatherValue(report.substring(0, 3));
            wind_sp  = getWeatherValue(report.substring(4, 7));
            report = report.substring(7);  // move past this
        }

        // parse the weather report
        while (index < report.length()) {
            if (report.charAt(index) == 'c') {
                String value = report.substring(index+1, index+4);
                wind_dir = getWeatherValue(value);
                if (wind_dir == null) {
                    if (!"...".equals(value)) bWarnNull = true;
                }
                index +=4;
            } else if (report.charAt(index) == 's') {
                String value = report.substring(index+1, index+4);
                wind_sp  = getWeatherValue(value);
                if (wind_sp == null) {
                    if (!"...".equals(value)) bWarnNull = true;
                }
                index +=4;
            } else if (report.charAt(index) == 'g') {
                String value = report.substring(index+1, index+4);
                gust  = getWeatherValue(value);
                if (gust == null) {
                    if (!"...".equals(value)) bWarnNull = true;
                }
                index +=4;
            } else if (report.charAt(index) == 't') {
                String value = report.substring(index+1, index+4);
                temp  = getWeatherValue(value);
                if (temp == null) {
                    if (!"...".equals(value)) bWarnNull = true;
                }
                index +=4;
            } else if (report.charAt(index) == 'r') {
                String value = report.substring(index+1, index+4);
                rain_1  = getWeatherValue(value);
                if (rain_1 == null) {
                    if (!"...".equals(value)) bWarnNull = true;
                }
                index +=4;
            } else if (report.charAt(index) == 'p') {
                String value = report.substring(index+1, index+4);
                rain24  = getWeatherValue(value);
                if (rain24 == null) {
                    if (!"...".equals(value)) bWarnNull = true;
                }
                index +=4;
            } else if (report.charAt(index) == '#') {
                // not keeping raw rain counter
                index +=4;
            } else if (report.charAt(index) == 'b') {
                String value = report.substring(index+1, index+6);
                if ((value.startsWith("...")) || ((value.startsWith("   ")))) {
                    // skip it
                    index += 4;
                } else {
                    baro  = getWeatherValue(value);
                    if (baro == null) {
                        bWarnNull = true;
                    }
                    index +=6;
                }
            } else if (report.charAt(index) == 'L') {
                String value = report.substring(index+1, index+4);
                lum  = getWeatherValue(value);
                if (lum == null) {
                    if (!"...".equals(value)) bWarnNull = true;
                }
                index +=4;
            } else if (report.charAt(index) == 'l') {
                String value = report.substring(index+1, index+4);
                lum  = getWeatherValue(value);
                if (lum == null) {
                    if (!"...".equals(value)) bWarnNull = true;
                } else {
                    lum += 1000;
                }
                index +=4;
            } else if (report.charAt(index) == 'h') {
                String value = report.substring(index+1, index+3);
                if ((value.startsWith("..")) || ((value.startsWith("  ")))) {
                    String test = report.substring(index+1, index+4); // try 3 characters
                    if (test.equals("...")) {
                        // skip it - adhere to use of ... for unknown regardless of size constraint
                        index += 4;
                    } else if (test.startsWith("..")) {
                        // skip it - adhere to hXX size in spec
                        index += 3;
                    } else {
                        index += 3;
                    }
                } else {
                    hum  = getWeatherValue(value);
                    if (hum == null) bWarnNull = true;
                    index +=3;
                }
            } else {
                // hit an unknown value - end of weather data - one char for APRS software, rest for sofware WX Unit
                break;
            }
        }

        if (bWarnNull) {
            logger.warn(String.format("Issue with weather report from %s: %s", innerAPRSPosition.getCallsignFrom(), report));
        }

        try {
            APRSWeatherReportRecord weatherReport = new APRSWeatherReportRecord(UUID.randomUUID().toString(), source, heardTime, innerAPRSPosition.getCallsignFrom(),
                innerAPRSPosition.getLat(), innerAPRSPosition.getLon(), innerAPRSPosition.getTime(), wind_dir, wind_sp, gust, temp, rain_1, rain24, rainmid, rainraw, hum, baro, lum, snow24);
            aprsWeatherReportRepository.save(weatherReport);
        } catch (Exception e) {
            logger.error("Exception caught saving weather report", e);
        }

    }

    private Integer getWeatherValue(String value) {
        if (!value.equals("...") && !value.equals("   ")) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.error("NumberFormatException caught - " + value, e);
            } catch (Exception e) {
                logger.error("Exception caught - " + value, e);
            }
        }
        return null;           
    }

    private void trackStation(User loggedInUser, String callsign, String lat, String lon, TrackedStationType type, String description) {

        if ((callsign.startsWith("WIDE1")) || (callsign.startsWith("WIDE2")) || (callsign.startsWith("WIDE3")) || (callsign.isEmpty()) || 
            (callsign.startsWith("WIDE4")) || (callsign.startsWith("WIDE5")) || (callsign.startsWith("WIDE6")) || (callsign.startsWith("WIDE7")) || (callsign.startsWith("TCPIP"))) {
            return;
        }

        TrackedStation trackedStation = null;
        try {
            trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);
        } catch (Exception e) {
        }

        if (trackedStation == null) {
            String id = UUID.randomUUID().toString();
            trackedStation = new TrackedStation(id, type, callsign, description, callsign, lat, lon, null, null, 
                                    null, ZonedDateTime.now(), false, TrackedStationStatus.UP, null, ElectricalPowerType.UNKNOWN, ElectricalPowerType.UNKNOWN, RadioStyle.UNKNOWN, 0);
            trackedStationAccessor.create(loggedInUser, trackedStation);
        } else {
            if (lat != null) {
                trackedStation.setLat(lat);
            }
            if (lon != null) {
                trackedStation.setLon(lon);
            }
            if (description != null) {
                trackedStation.setDescription(description);
            }
            if (type != TrackedStationType.UNKNOWN) {
                trackedStation.setType(type);
            }
            trackedStation.setLastHeard(ZonedDateTime.now());
            trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);
        }

        // given a tracked station, register their root callsign
        registerCallsign(loggedInUser, trackedStation.getCallsign());
    }

    private void registerCallsign(User loggedInUser, String callsign) {
        if (callsign == null) {
            return;
        }
        String root;
        int index = callsign.indexOf("-");

        if (index == -1) {
            root = callsign;
        } else {
            root = callsign.substring(0, index);
        }

        try {
            Callsign c = new Callsign(root, null, null, null, null);
            callsignAccessor.create(loggedInUser, c);
        } catch (Exception e) {
            // ignore - could be fatal, could already exist
        }
    }

    private void trackStationFromObject(User loggedInUser, String callsign, String lat, String lon, String description) {
        TrackedStation trackedStation = null;
        TrackedStationType type = TrackedStationType.OBJECT;

        if ((callsign.startsWith("WIDE1")) || (callsign.startsWith("WIDE2")) || (callsign.startsWith("WIDE3")) || (callsign.isEmpty()) || 
            (callsign.startsWith("WIDE4")) || (callsign.startsWith("WIDE5")) || (callsign.startsWith("WIDE6")) || (callsign.startsWith("WIDE7")) || (callsign.startsWith("TCPIP"))) {
            return;
        }

        try {
            trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, callsign);
        } catch (Exception e) {
        }

        TrackedStation infoFromComment = processObjectComment(description, type);
        if (infoFromComment != null) {
            type = infoFromComment.getType();
        }

        if (trackedStation == null) {
            String id = UUID.randomUUID().toString();
            trackedStation = new TrackedStation(id, type, 
                            callsign, description, callsign, lat, lon, 
                            (infoFromComment != null) ? infoFromComment.getFrequencyTx() : null, 
                            (infoFromComment != null) ? infoFromComment.getFrequencyRx() : null, 
                            (infoFromComment != null) ? infoFromComment.getTone() : null,
                            ZonedDateTime.now(), false, TrackedStationStatus.UP, 
                            (infoFromComment != null) ? infoFromComment.getIpAddress() : null,
                            (infoFromComment != null) ? infoFromComment.getElectricalPowerType() : ElectricalPowerType.UNKNOWN,
                            (infoFromComment != null) ? infoFromComment.getBackupElectricalPowerType() : ElectricalPowerType.UNKNOWN,
                            (infoFromComment != null) ? infoFromComment.getRadioStyle() : RadioStyle.UNKNOWN,
                            (infoFromComment != null) ? infoFromComment.getTransmitPower() : 0
                            );
            trackedStationAccessor.create(loggedInUser, trackedStation);
        } else {
            if (lat != null) {
                trackedStation.setLat(lat);
            }
            if (lon != null) {
                trackedStation.setLon(lon);
            }
            if (description != null) {
                trackedStation.setDescription(description);
            }
            if ((type != null) && (type != TrackedStationType.UNKNOWN)) {
                trackedStation.setType(type);
            }
            if (infoFromComment != null) {
                if (infoFromComment.getDescription() != null) {
                    trackedStation.setDescription(infoFromComment.getDescription());
                }
                if (infoFromComment.getFrequencyTx() != null) {
                    trackedStation.setFrequencyTx(infoFromComment.getFrequencyTx());
                }
                if (infoFromComment.getFrequencyRx() != null) {
                    trackedStation.setFrequencyRx(infoFromComment.getFrequencyRx());
                }
                if (infoFromComment.getTone() != null) {
                    trackedStation.setTone(infoFromComment.getTone());
                }
                if (infoFromComment.getIpAddress() != null) {
                    trackedStation.setIpAddress(infoFromComment.getIpAddress());
                }
                if (infoFromComment.getElectricalPowerType() != ElectricalPowerType.UNKNOWN) {
                    trackedStation.setElectricalPowerType(infoFromComment.getElectricalPowerType());
                }
                if (infoFromComment.getRadioStyle() != RadioStyle.UNKNOWN) {
                    trackedStation.setRadioStyle(infoFromComment.getRadioStyle());
                }
                if (infoFromComment.getTransmitPower() != 0) {
                    trackedStation.setTransmitPower(infoFromComment.getTransmitPower());
                }
            }
            trackedStation.setLastHeard(ZonedDateTime.now());
            trackedStationAccessor.update(loggedInUser, trackedStation.getId(), trackedStation);
        }
    }

    private TrackedStation processObjectComment(String comment, TrackedStationType type) {
        if (comment == null) {
            return null;
        }
        TrackedStation ret = new TrackedStation();
        ret.setType(type);

        int index = 0; 
        String remain = "";
        while (index < comment.length()) {
            remain = comment.substring(index);
            if (remain.startsWith("/A")) {
                index += "/A=xxxxxx".length();
            } else if (remain.startsWith(" ")) {
                index++;
            } else if (remain.startsWith("RNG")) {
                index += "RNGxxxx".length();
            } else if (remain.startsWith("PHG")) {
                index += "PHGxxxx".length();
            } else {
                break;
            }
        }

        String info = remain;
        String description = null;
        // remain has more info
        if (remain.contains(",")) {
            // 70cm Voice (D-Star) 433.60000MHz +0.0000MHz, APRS for ircDDBGateway
            String [] parts = remain.split(",");
            if (parts.length == 2) {
                description = parts[1].substring(1); // skip space
                info = parts[0];
            }
        }

        // 2m Voice 145.330 MHz -0.600MHz
        // 440 Voice 449.575 -5.00 MHz
        // 70cm Voice (D-Star) 438.80000MHz +0.0000MHz

        String [] parts = info.split(" ");
        boolean voice = false;
        boolean winlink = false;

        if (parts != null) {
            for (String part : parts) {
                if ("voice".equalsIgnoreCase(part)) {
                    voice = true;
                    ret.setType(TrackedStationType.REPEATER);
                } else if ("winlink".equalsIgnoreCase(part)) {
                    winlink = true;
                    ret.setType(TrackedStationType.WINLINK_GATEWAY);
                } else if ("(D-Star)".equalsIgnoreCase(part)) {
                    ret.setType(TrackedStationType.DSTAR);  // may override voice / repeater from above
                } 
            }
        }

        if (info.toUpperCase().contains("MMDVM")) {
            ret.setType(TrackedStationType.MMDVM);
        }

        if (voice) {
            // need to parse for the frequencies and tone
        }
        if (winlink) {
            // need to parse for the frequencies and tone
        }
        if (!voice && !winlink) {
            // "BBS" could be embedded anywhere
            if (info.toUpperCase().contains("BBS")) {
                ret.setType(TrackedStationType.BBS);
            }
        }
        if (description != null) {
            ret.setDescription(description);
        } else {
            ret.setDescription(info);
        }

        return ret;
    }

    private APRSObjectResource createAPRSObject(User loggedInUser, String id, Optional<APRSObject> innerAPRSObjectOpt, String source, ZonedDateTime heardTime) {
        APRSObject innerAPRSObject = innerAPRSObjectOpt.get();
        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSObject.getCallsignFrom())) {
            return null;
        }
        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSObject.getCallsignTo())) {
            return null;
        }
        List<APRSObjectRecord> foundRecList = aprsObjectRepository.findBycallsign_to(innerAPRSObject.getCallsignTo());
        APRSObjectRecord rec;
        if ((foundRecList != null) && (!foundRecList.isEmpty())) {
            // objects get overwritten, not created new
            APRSObjectRecord first = foundRecList.get(0);
            APRSObjectRecord updated = new APRSObjectRecord(first.aprs_object_id(), source, first.callsign_from(), first.callsign_to(), heardTime, innerAPRSObject.isAlive(), innerAPRSObject.getLat(),
                                                                    innerAPRSObject.getLon(), innerAPRSObject.getTime(), innerAPRSObject.getComment(),
                                                                    (innerAPRSObject.getType() != null) ? innerAPRSObject.getType().ordinal() : first.type());
            rec = aprsObjectRepository.update(updated);
            changePublisherAccessor.publishObjectUpdate(first.callsign_to(), ChangePublisherAccessor.UPDATE, innerAPRSObject);
        } else {
            APRSObjectRecord src = new APRSObjectRecord(id, source, innerAPRSObject.getCallsignFrom(), innerAPRSObject.getCallsignTo(), heardTime, innerAPRSObject.isAlive(),
                                                        innerAPRSObject.getLat(), innerAPRSObject.getLon(), innerAPRSObject.getTime(), innerAPRSObject.getComment(), 
                                                        (innerAPRSObject.getType() == null) ? ObjectType.STANDARD.ordinal() : innerAPRSObject.getType().ordinal());
            rec = aprsObjectRepository.save(src);
            changePublisherAccessor.publishObjectUpdate(innerAPRSObject.getCallsignTo(), ChangePublisherAccessor.CREATE, innerAPRSObject);
        }

        trackStation(loggedInUser,  rec.callsign_from(), null, null, TrackedStationType.STATION, null); // create station representing from 
        trackStationFromObject(loggedInUser,  rec.callsign_to(), innerAPRSObject.getLat(), innerAPRSObject.getLon(), innerAPRSObject.getComment());

        if (source.equals("NETCENTRAL")) {
            // this is a locally created object - send out the creation report
            federatedObjectReporterAccessor.announce(loggedInUser, innerAPRSObject);
        } else {
            // determine if this heard object is from a Net Central somewhere else
            // interrogate objects for object type
            federatedObjectReporterAccessor.interrogate(loggedInUser, source, innerAPRSObject);
        }
        return new APRSObjectResource(id, innerAPRSObject, source, heardTime);
    }


    private APRSObjectResource createAPRSMicE(User loggedInUser, String id, Optional<APRSMicE> innerAPRSMicEOpt, String source, ZonedDateTime heardTime) {
        APRSMicE innerAPRSMicE = innerAPRSMicEOpt.get();
        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSMicE.getCallsignFrom())) {
            return null;
        }
        APRSPositionRecord src = new APRSPositionRecord(id, source, heardTime, innerAPRSMicE.getCallsignFrom(), innerAPRSMicE.getLat(), innerAPRSMicE.getLon(), "",
                                                    null, null, null, null, 
                                                    null, null, innerAPRSMicE.getStatus());

        APRSPositionRecord rec = aprsPositionRepository.save(src);
        TrackedStationType type = TrackedStationType.UNKNOWN;
        trackStation(loggedInUser,  rec.callsign_from(), innerAPRSMicE.getLat(), innerAPRSMicE.getLon(), type, null);
        return new APRSObjectResource(id, innerAPRSMicE, source, heardTime);
    }


    private APRSObjectResource createAPRSMessage(User loggedInUser, String id, Optional<APRSMessage> innerAPRSMessageOpt, String source, ZonedDateTime heardTime) {
        APRSMessage innerAPRSMessage = innerAPRSMessageOpt.get();
        String callsignTo = Stripper.stripWhitespace(innerAPRSMessage.getCallsignTo());

        APRSObject priorityObject = null;
        APRSObject generalResourceObject = null;
        Net net = netCentralNetMessage(loggedInUser, callsignTo);
        String completedNetId = null;
        if (net != null) {
            completedNetId = net.getCompletedNetId();
        } else {
            // if it is not a message to a net, is it a message to one of our priority objects?
            priorityObject = netCentralPriorityObject(loggedInUser, callsignTo);
            if (priorityObject == null) {
                generalResourceObject = netCentralGeneralResourceObject(loggedInUser, callsignTo);
            }
        }

        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSMessage.getCallsignFrom())) {
            return null;
        }

        String message = innerAPRSMessage.getMessage();
        if ((message != null) && (message.length() > MAX_COMMENT_LENGTH)) {
            logger.warn("Message text exceeds max length - "+message);
            message = message.substring(MAX_COMMENT_LENGTH);
        }
        APRSMessageRecord src = new APRSMessageRecord(id, completedNetId, source, heardTime, innerAPRSMessage.getCallsignFrom(), innerAPRSMessage.getCallsignTo(), message,
                                                    innerAPRSMessage.getMessageNumber(), innerAPRSMessage.isMustAck(), innerAPRSMessage.isBulletin(), 
                                                    innerAPRSMessage.isAnnouncement(), innerAPRSMessage.isGroupBulletin(), innerAPRSMessage.isNWSBulletin(),
                                                    innerAPRSMessage.getNWSLevel(), innerAPRSMessage.isQuery(), innerAPRSMessage.getQueryType());

        APRSMessageRecord rec;
        if ((innerAPRSMessage.getMessage() != null) && ((innerAPRSMessage.getMessage().startsWith("ack")) || (innerAPRSMessage.getMessage().startsWith("rej")))) {
            // dont save it but process what is here
            rec = src;
        } else if ((completedNetId != null) || (innerAPRSMessage.isNWSBulletin())) {
            rec = aprsMessageRepository.save(src);
        } else {
            // dont save it but process what is here
            rec = src;
        }

        // create a TrackedStation
        trackedStationAccessor.createStation(loggedInUser, innerAPRSMessage.getCallsignFrom());

        if (completedNetId != null) {
            // is this message for Net Central net?
            radioCommandAccessor.processMessage(loggedInUser, innerAPRSMessage, source);
        } else if (priorityObject != null) {
            // is this a message for Net Central priority object?
            priorityObjectCommandAccessor.processMessage(loggedInUser, priorityObject, innerAPRSMessage, source);
        } else if (generalResourceObject != null) {
            generalResourceObjectCommandAccessor.processMessage(loggedInUser, generalResourceObject, innerAPRSMessage, source);
        } else if ((rec.callsign_from() != null) && ((rec.callsign_from().equals("WHO-IS")) || (rec.callsign_from().equals("WHO-15")))) {
            processWHOISMessage(loggedInUser, innerAPRSMessage, source);
        } else if ((rec.callsign_from() != null) && (rec.callsign_from().equals(toolsAccessor.getWinlinkGatewayCallsign()))) {
            processWLNKMessage(loggedInUser, innerAPRSMessage, source);
        } else if ((rec.callsign_from() != null) && (!innerAPRSMessage.isMustAck())) {
            // object sent a message not needing an ack - figure out if it sent a response
            actOnObjectMessage(loggedInUser, innerAPRSMessage.getCallsignFrom(), source, heardTime, message);
        }

        return new APRSObjectResource(id, innerAPRSMessage, source, heardTime);
    }

    private void actOnObjectMessage(User loggedInUser, String callsignFrom, String source, ZonedDateTime heardTime, String message) {
        try {
            if (!message.startsWith(NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE+":")) {
                actOnObjectReportMessage(loggedInUser, callsignFrom, source, heardTime, message);
                return;
            }
            APRSObject obj = getObjectByCallsign(loggedInUser, callsignFrom);
            if (obj == null) {
                return;
            }
            String [] values = message.split(":");
            if (values.length != 2) {
                return;
            }
            ObjectType type = ObjectType.valueOf(values[1]);
            obj.setType(type);
            updateObjectFromMessage(loggedInUser, obj, source, heardTime);

        } catch (Exception e) {
        }
    }

    private void updateObjectFromMessage(User loggedInUser, APRSObject obj, String source, ZonedDateTime heardTime) {
            APRSObjectRecord updated = new APRSObjectRecord(obj.getId(), source, obj.getCallsignFrom(), obj.getCallsignTo(), heardTime, obj.isAlive(), obj.getLat(),
                                                        obj.getLon(), "", obj.getComment(), obj.getType().ordinal());
            aprsObjectRepository.update(updated);
            // need an update message
            changePublisherAccessor.publishObjectUpdate(obj.getCallsignTo(), ChangePublisherAccessor.UPDATE, obj);
    }

    private void actOnObjectReportMessage(User loggedInUser, String callsignFrom, String source, ZonedDateTime heardTime, String message) {
        APRSObject obj = getObjectByCallsign(loggedInUser, callsignFrom);
        if ((obj == null) || ((!obj.getType().equals(ObjectType.STANDARD) && (!obj.getType().equals(ObjectType.UNKNOWN))))) {
            // dont have an object yet or already has a specific type - get out - it wont ever change
            return;
        }

        try {
            APRSNetCentralNetAnnounceReport report = APRSNetCentralNetAnnounceReport.isValid(callsignFrom, message);
            if (report != null) {
                // object is a net
                obj.setType(ObjectType.NET);
                updateObjectFromMessage(loggedInUser, obj, source, heardTime);
                // create a remote net
                String completedNetId = UUID.randomUUID().toString();
                Net remoteNet = new Net(callsignFrom, report.getName(), report.getDescription(), null, heardTime, completedNetId, null, null, false, "NETCENTRAL", false, null, false, false, true);
                netAccessor.create(loggedInUser, remoteNet);

                return;
            }
        } catch (Exception e) {
        }

        try {
            APRSNetCentralObjectAnnounceReport report = APRSNetCentralObjectAnnounceReport.isValid(callsignFrom, message);
            if (report != null) {
                // object is a net
                obj.setType(report.getObjectType());
                updateObjectFromMessage(loggedInUser, obj, source, heardTime);

                return;
            }
        } catch (Exception e) {
        }

    }

    private void processWHOISMessage(User loggedInUser, APRSMessage innerAPRSMessage, String source) {
        if (innerAPRSMessage.getMessage().contains(":") && (innerAPRSMessage.getMessage().contains("/"))) {
            // new style WHO-IS/15 summary message
            int index = innerAPRSMessage.getMessage().indexOf(":");
            String callsign;
            String remain;
            if (index != -1) {
                callsign = innerAPRSMessage.getMessage().substring(0, index);
                remain = innerAPRSMessage.getMessage().substring(index+1);
                String [] info = remain.split("/");
                if (info != null) {
                    Callsign cs = callsignAccessor.getByCallsign(loggedInUser, callsign);
                    if (cs == null) {
                        return;
                    }
                    boolean changed = false;
                    if (info.length == 4) {
                        try {
                            if (cs.getCountry() == null) {
                                cs.setCountry(info[3]);
                                changed = true;
                            }
                            if (cs.getState() == null) {
                                cs.setState(info[2]);
                                changed = true;
                            }
                            if (cs.getLicense() == null) {
                                cs.setLicense(info[0]);
                                changed = true;
                            }
                            if (cs.getName() == null) {
                                cs.setName(info[1]);
                                changed = true;
                            }
                        } catch (Exception e) {
                        }
                    } else if (info.length == 3) {
                        try {
                            if (cs.getCountry() == null) {
                                cs.setCountry(info[2]);
                                changed = true;
                            }
                            if (cs.getState() == null) {
                                cs.setState(info[1]);
                                changed = true;
                            }
                            if (cs.getName() == null) {
                                cs.setName(info[0]);
                                changed = true;
                            }
                        } catch (Exception e) {
                        }
                    } else if (info.length == 2) {
                        try {
                            if (cs.getCountry() == null) {
                                cs.setCountry(info[1]);
                                changed = true;
                            }
                            if (cs.getName() == null) {
                                cs.setName(info[0]);
                                changed = true;
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (changed) {
                        callsignAccessor.update(loggedInUser, cs.getCallsign(), cs);
                    }
                }
            }
        }
    }

    private void processWLNKMessage(User loggedInUser, APRSMessage innerAPRSMessage, String source) {
        String response = innerAPRSMessage.getMessage();
        if ((response.startsWith("ack")) || (response.startsWith("rej"))) {
            return;
        }
        try {
            WinlinkSessionCacheEntry entry = toolsAccessor.getWinlinkMessageEntry(loggedInUser, innerAPRSMessage.getCallsignTo());
            if (entry != null) {
                if (entry.getState().equals(WinlinkSessionState.WAITING)) {
                    //  if in WAITING state, put three digit value in cache, determine six character response and send it; mark as CHALLENGING in cache
                    String tdv = innerAPRSMessage.getMessage().substring(7, 10);
                    entry.setThreeDigitChallenge(tdv);
                    entry.setSixDigitResponse(toolsAccessor.calculateWinlinkSixDigitResponse(entry));
                    entry.setState(WinlinkSessionState.CHALLENGING);
                    entry = toolsAccessor.updateWinlinkMessageEntry(loggedInUser, entry.getCallsign(), entry);
                    toolsAccessor.sendWinlinkChallengeMessage(loggedInUser, entry);
                } else if (entry.getState().equals(WinlinkSessionState.CHALLENGING)) {
                    //  if in CHALLENGING state, check if response starts with "Hello CALLSIGN"; if so, mark as SENDING and send message in chunks
                    String successResponse = "Hello "+entry.getCallsign();
                    String failureResponse = "Invalid login";
                    //Invalid login challenge response
                    if (response.startsWith(successResponse)) {
                        // logged in successfully
                        entry.setThreeDigitChallenge(null);
                        entry.setSixDigitResponse(null);
                        entry.setPassword(null);
                        entry.setState(WinlinkSessionState.SENDING);
                        entry = toolsAccessor.updateWinlinkMessageEntry(loggedInUser, entry.getCallsign(), entry);
                        toolsAccessor.sendWinlinkMessage(loggedInUser, entry);
                    } else if (response.startsWith(failureResponse)) {
                        // TODO: determine way to avoid 2 hour wait
                        // fail
                        toolsAccessor.deleteStatusWinlinkMessage(loggedInUser, entry.getCallsign());
                    }
                } else if (entry.getState().equals(WinlinkSessionState.SENDING)) {
                    String successResponse = "Log off successful";
                    String failureResponse = "Too many login attempts";
                    // Invalid login challenge response
                    if (response.startsWith(successResponse)) {
                        // success all messages sent
                        toolsAccessor.deleteStatusWinlinkMessage(loggedInUser, entry.getCallsign());
                    } else if (response.startsWith(failureResponse)) {
                        // fail
                        toolsAccessor.deleteStatusWinlinkMessage(loggedInUser, entry.getCallsign());
                    }
                    // can ignore - all messages already in flight
                } else if (entry.getState().equals(WinlinkSessionState.NOT_VALIDATED)) {
                    // password validation failed - ignore and it will go away
                } else if (entry.getState().equals(WinlinkSessionState.NOT_FOUND)) {
                    // do nothing
                } else if (entry.getState().equals(WinlinkSessionState.COMPLETED)) {
                    // do nothing
                }
            } else {
                // ignore - not for us
            }
        } catch (Exception e) {
        }
    }

    public void createAPRSMessageFromNetMessage(User loggedInUser, Net net, NetMessage netMessage, String source) {
        if (net == null) {
            logger.debug("Net is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net is null");
        }

        APRSMessageRecord src = new APRSMessageRecord(UUID.randomUUID().toString(), net.getCompletedNetId(), source, netMessage.getReceivedTime(), net.getCallsign(), net.getCallsign(), netMessage.getMessage(),
                                                    null, false, false, 
                                                    false, false, false,
                                                    null, false, null);

        aprsMessageRepository.save(src);
    }

    private Net netCentralNetMessage(User loggedInUser, String callsignTo) {
        Net net = null;
        try {
            net = netAccessor.getByCallsign(loggedInUser, callsignTo);
        } catch (Exception e) {
        }

        return net;
    }

    private APRSObject netCentralPriorityObject(User loggedInUser, String callsign) {
        APRSObject obj = null;

        try {
            obj = getObjectByCallsign(loggedInUser, callsign);
            if ((!obj.getType().equals(ObjectType.EOC)) && (!obj.getType().equals(ObjectType.MEDICAL)) && (!obj.getType().equals(ObjectType.SHELTER))) {
                // not a priority object
                obj = null;
            }
        } catch (Exception e) {
        }

        return obj;
    }

    private APRSObject netCentralGeneralResourceObject(User loggedInUser, String callsign) {
        APRSObject obj = null;

        try {
            obj = getObjectByCallsign(loggedInUser, callsign);
            if (!obj.getType().equals(ObjectType.RESOURCE)) {
                // not a priority object
                obj = null;
            }
        } catch (Exception e) {
        }

        return obj;
    }
    private APRSObjectResource createAPRSMaidenheadLocatorBeacon(User loggedInUser, String id, Optional<APRSMaidenheadLocatorBeacon> innerAPRSMaidenheadLocatorBeaconOpt, String source, ZonedDateTime heardTime) {
        APRSMaidenheadLocatorBeacon innerAPRSMaidenheadLocatorBeacon = innerAPRSMaidenheadLocatorBeaconOpt.get();

        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSMaidenheadLocatorBeacon.getCallsignFrom())) {
            return null;
        }

        APRSMaidenheadLocatorBeaconRecord src = new APRSMaidenheadLocatorBeaconRecord(id, source, heardTime, innerAPRSMaidenheadLocatorBeacon.getCallsignFrom(),
                                                                innerAPRSMaidenheadLocatorBeacon.getComment(), innerAPRSMaidenheadLocatorBeacon.getGridLocator());
        aprsMaidenheadLocatorBeaconRepository.save(src);
        return new APRSObjectResource(id, innerAPRSMaidenheadLocatorBeacon, source, heardTime);
    }


    private APRSObjectResource createAPRSItem(User loggedInUser, String id, Optional<APRSItem> innerAPRSItemOpt, String source, ZonedDateTime heardTime) {
        APRSItem innerAPRSItem = innerAPRSItemOpt.get();

        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSItem.getCallsignFrom())) {
            return null;
        }
        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSItem.getCallsignTo())) {
            return null;
        }

        // treat an item like an object 
        List<APRSObjectRecord> foundRecList = null; 
        
        try {
            foundRecList = aprsObjectRepository.findBycallsign_to(innerAPRSItem.getCallsignFrom());
        } catch (Exception e) {
        }

        APRSObjectRecord rec;
        if ((foundRecList != null) && (!foundRecList.isEmpty())) {
            // objects get overwritten, not created new
            APRSObjectRecord first = foundRecList.get(0);
            APRSObjectRecord updated = new APRSObjectRecord(first.aprs_object_id(), source, first.callsign_from(), first.callsign_to(), heardTime, innerAPRSItem.isAlive(), innerAPRSItem.getLat(),
                                                                    innerAPRSItem.getLon(), "", innerAPRSItem.getComment(), first.type());
            rec = aprsObjectRepository.update(updated);
        } else {
            APRSObjectRecord src = new APRSObjectRecord(id, source, innerAPRSItem.getCallsignFrom(), innerAPRSItem.getCallsignTo(), heardTime, innerAPRSItem.isAlive(),
                                                        innerAPRSItem.getLat(), innerAPRSItem.getLon(), "", innerAPRSItem.getComment(), ObjectType.ITEM.ordinal());
            rec = aprsObjectRepository.save(src);
        }

        trackStationFromObject(loggedInUser,  rec.callsign_from(), innerAPRSItem.getLat(), innerAPRSItem.getLon(), innerAPRSItem.getComment());
        return new APRSObjectResource(id, innerAPRSItem, source, heardTime);
    }


    private APRSObjectResource createAPRSAgrelo(User loggedInUser, String id, Optional<APRSAgrelo> innerAPRSAgreloOpt, String source, ZonedDateTime heardTime) {
        APRSAgrelo innerAPRSAgrelo = innerAPRSAgreloOpt.get();

        if (ignoreStationAccessor.isIgnored(loggedInUser, innerAPRSAgrelo.getCallsignFrom())) {
            return null;
        }

        APRSAgreloRecord src = new APRSAgreloRecord(id, source, heardTime, innerAPRSAgrelo.getCallsignFrom(), innerAPRSAgrelo.getBearing(), innerAPRSAgrelo.getQuality());
        aprsAgreloRepository.save(src);
        return new APRSObjectResource(id, innerAPRSAgrelo, source, heardTime);
    }

    private APRSObjectResource createAPRSRaw(User loggedInUser, String id, Optional<APRSRaw> innerAPRSRawOpt, String source, ZonedDateTime heardTime) {
        APRSRaw innerAPRSRaw = innerAPRSRawOpt.get();

        if (configParametersAccessor.isLogRawPackets()) {
            APRSRawRecord src = new APRSRawRecord(id, source, heardTime, new String(innerAPRSRaw.getData()));
            aprsRawRepository.save(src);
        }
        return new APRSObjectResource(id, innerAPRSRaw, source, heardTime);
    }

    private APRSObjectResource createAGWRaw(User loggedInUser, String id, Optional<AGWRaw> innerAGWRawOpt, String source, ZonedDateTime heardTime) {
        AGWRaw innerAGWRaw = innerAGWRawOpt.get();

        AGWRawRecord src = new AGWRawRecord(id, source, heardTime, null, innerAGWRaw.getData());
        agwRawRepository.save(src);
        return new APRSObjectResource(id, innerAGWRaw, source, heardTime);
    }

    public List<APRSMessage> getMessagesFrom(User loggedInUser, String callsignFrom) {
        List<APRSMessage> messages = new ArrayList<>();
        try {
            List<APRSMessageRecord> recs = aprsMessageRepository.findBycallsign_from(callsignFrom);
            if ((recs != null) && (!recs.isEmpty())) {
                for (APRSMessageRecord rec: recs) {
                    messages.add(new APRSMessage(rec.aprs_message_id(), rec.callsign_from(), rec.callsign_to(), rec.message(), rec.message_number(), 
                                                        rec.must_ack(), rec.heard_time(),
                                                        rec.bulletin(), rec.announcement(), rec.group_bulletin(), rec.nws_bulletin(), rec.nws_level(), 
                                                        rec.query_type(), rec.query(), rec.completed_net_id()));
                }
            }
        } catch (Exception e) {
        }

        return messages;
    }

    public List<APRSMessage> getMessagesTo(User loggedInUser, String callsignTo) {
        List<APRSMessage> messages = new ArrayList<>();
        try {
            List<APRSMessageRecord> recs = aprsMessageRepository.findBycallsign_to(callsignTo);
            if ((recs != null) && (!recs.isEmpty())) {
                for (APRSMessageRecord rec: recs) {
                    messages.add(new APRSMessage(rec.aprs_message_id(), rec.callsign_from(), rec.callsign_to(), rec.message(), rec.message_number(), rec.must_ack(), 
                                                        rec.heard_time(), rec.bulletin(), rec.announcement(), rec.group_bulletin(), rec.nws_bulletin(), 
                                                        rec.nws_level(), rec.query_type(), rec.query(), rec.completed_net_id()));
                }
            }
        } catch (Exception e) {
        }

        return messages;
    }

    public List<APRSMessage> getCompletedNetMessages(User loggedInUser, String completedNetId) {
        List<APRSMessage> messages = new ArrayList<>();
        try {
            List<APRSMessageRecord> recs = aprsMessageRepository.findBycompleted_net_id(completedNetId);
            if ((recs != null) && (!recs.isEmpty())) {
                for (APRSMessageRecord rec: recs) {
                    messages.add(new APRSMessage(rec.aprs_message_id(), rec.callsign_from(), rec.callsign_to(), rec.message(), rec.message_number(), rec.must_ack(), 
                                                        rec.heard_time(), rec.bulletin(), rec.announcement(), rec.group_bulletin(), rec.nws_bulletin(), 
                                                        rec.nws_level(), rec.query_type(), rec.query(), rec.completed_net_id()));
                }
            }
        } catch (Exception e) {
        }

        return messages;
    }

    public List<APRSWeatherReport> getWeatherReports(User loggedInUser, String callsignFrom) {
        List<APRSWeatherReport> messages = new ArrayList<>();
        try {
            List<APRSWeatherReportRecord> recs = aprsWeatherReportRepository.findBycallsign_from(callsignFrom);
            if ((recs != null) && (!recs.isEmpty())) {
                for (APRSWeatherReportRecord rec: recs) {
                    messages.add(new APRSWeatherReport(rec.aprs_weather_report_id(), rec.callsign_from(), null, rec.time(), 
                    (rec.wind_direction() == null) ? 0 : rec.wind_direction(),
                    (rec.wind_speed() == null) ? 0 : rec.wind_speed(),
                    (rec.gust() == null) ? 0 : rec.gust(),
                    (rec.temperature() == null) ? 0 : rec.temperature(),
                    (rec.rainfall_last_1hr() == null) ? 0 : rec.rainfall_last_1hr(),
                    (rec.rainfall_last_24hr() == null) ? 0 : rec.rainfall_last_24hr(),
                    (rec.rainfall_since_midnight() == null) ? 0 : rec.rainfall_since_midnight(),
                    (rec.humidity() == null) ? 0 : rec.humidity(),
                    (rec.barometric_pressure() == null) ? 0 : rec.barometric_pressure(),
                    (rec.luminosity() == null) ? 0 : rec.luminosity(),
                    (rec.snowfall_last_24hr() == null) ? 0 : rec.snowfall_last_24hr(),
                    (rec.raw_rain_counter() == null) ? 0 : rec.raw_rain_counter(),
                    rec.lat(), rec.lon(), rec.heard_time()));
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught constructing weather report");
        }

        return messages;
    }

    public APRSWeatherReport getLatestWeatherReport(User loggedInUser, String callsignFrom) {
        APRSWeatherReport report = null;
        try {
            List<APRSWeatherReportRecord> recs = aprsWeatherReportRepository.findBycallsign_from(callsignFrom);
            if ((recs != null) && (!recs.isEmpty())) {
                for (APRSWeatherReportRecord rec: recs) {
                    if ((report == null) || ((report.getLdtime().isBefore(rec.heard_time())))) {
                        report = new APRSWeatherReport(rec.aprs_weather_report_id(), rec.callsign_from(), null, rec.time(), 
                            (rec.wind_direction() == null) ? 0 : rec.wind_direction(),
                            (rec.wind_speed() == null) ? 0 : rec.wind_speed(),
                            (rec.gust() == null) ? 0 : rec.gust(),
                            (rec.temperature() == null) ? 0 : rec.temperature(),
                            (rec.rainfall_last_1hr() == null) ? 0 : rec.rainfall_last_1hr(),
                            (rec.rainfall_last_24hr() == null) ? 0 : rec.rainfall_last_24hr(),
                            (rec.rainfall_since_midnight() == null) ? 0 : rec.rainfall_since_midnight(),
                            (rec.humidity() == null) ? 0 : rec.humidity(),
                            (rec.barometric_pressure() == null) ? 0 : rec.barometric_pressure(),
                            (rec.luminosity() == null) ? 0 : rec.luminosity(),
                            (rec.snowfall_last_24hr() == null) ? 0 : rec.snowfall_last_24hr(),
                            (rec.raw_rain_counter() == null) ? 0 : rec.raw_rain_counter(),
                            rec.lat(), rec.lon(), rec.heard_time());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught getting latest weather report");
        }

        return report;
    }

    public void deleteWeatherReports(User loggedInUser, String callsignFrom) {
        try {
            List<APRSWeatherReportRecord> recs = aprsWeatherReportRepository.findBycallsign_from(callsignFrom);
            if ((recs != null) && (!recs.isEmpty())) {
                for (APRSWeatherReportRecord rec: recs) {
                    aprsWeatherReportRepository.delete(rec);
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught deleting weather report");
        }
    }

    public List<APRSStatus> getStatus(User loggedInUser, String callsignFrom) {
        List<APRSStatus> messages = new ArrayList<>();
        try {
            List<APRSStatusRecord> recs = aprsStatusRepository.findBycallsign_from(callsignFrom);
            if ((recs != null) && (!recs.isEmpty())) {
                for (APRSStatusRecord rec: recs) {
                    messages.add(new APRSStatus(rec.aprs_status_id(), rec.callsign_from(), rec.time(), rec.ldtime(), rec.status()));
                }
            }
        } catch (Exception e) {
        }

        return messages;
    }

    public APRSObjectResource delete(User loggedInUser, String id) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Object id not provided");
        }

        APRSObject object = getObject(loggedInUser, id);
        if (object == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Object not found");
        }

        if (!object.isRemote()) {
            // only speak about objects we own
            switch (object.getType()) {
                case ObjectType.EOC:
                case ObjectType.SHELTER:
                case ObjectType.MEDICAL:
                case ObjectType.RESOURCE:
                    markObjectDead(loggedInUser, object);
                    break;
                default:
                    break;
            }
        }
        Optional<APRSObjectRecord> recOpt = aprsObjectRepository.findById(id);
        aprsObjectRepository.delete(recOpt.get());
        changePublisherAccessor.publishObjectUpdate(recOpt.get().callsign_to(), ChangePublisherAccessor.DELETE, object);
        return null;
    }

    private void markObjectDead(User loggedInUser, APRSObject object) {
        if ((object.getLat() == null) || (object.getLon() == null) || object.isRemote()) {
            return;
        }
        transceiverCommunicationAccessor.sendObject(loggedInUser, object.getCallsignTo(), object.getCallsignTo(), object.getComment(), false, object.getLat(), object.getLon());
    }

    public List<APRSObject> getObjects(User loggedInUser, boolean priorityOnly, boolean generalOnly) {
        List<APRSObject> ret = new ArrayList<>();
        APRSObjectRecord badrec = null;
        try {
            List<APRSObjectRecord> recList = aprsObjectRepository.findAll();
            if (!recList.isEmpty()) {
                for (APRSObjectRecord rec : recList) {
                    boolean remote = true;
                    if (rec.source().equals("NETCENTRAL")) {
                        remote = false;
                    }
                    if (priorityOnly) {
                        badrec = rec;
                        if ((ObjectType.values()[rec.type()] == ObjectType.EOC) || (ObjectType.values()[rec.type()] == ObjectType.SHELTER) || 
                                (ObjectType.values()[rec.type()] == ObjectType.MEDICAL)) {  //|| (ObjectType.values()[rec.type()] == ObjectType.NET) - remove nets
                            ret.add(new APRSObject(rec.aprs_object_id(), rec.callsign_from(), 
                                            rec.callsign_to(), rec.alive(), rec.lat(), rec.lon(), rec.time(), rec.heard_time(), rec.comment(), 
                                            ObjectType.values()[rec.type()], remote));
                        }
                    } else if (generalOnly) {
                        if ((ObjectType.values()[rec.type()] == ObjectType.RESOURCE)) { 
                            ret.add(new APRSObject(rec.aprs_object_id(), rec.callsign_from(), 
                                            rec.callsign_to(), rec.alive(), rec.lat(), rec.lon(), rec.time(), rec.heard_time(), rec.comment(), 
                                            ObjectType.values()[rec.type()], remote));
                        }
                    } else {
                        ret.add(new APRSObject(rec.aprs_object_id(), rec.callsign_from(), 
                                            rec.callsign_to(), rec.alive(), rec.lat(), rec.lon(), rec.time(), rec.heard_time(), rec.comment(), 
                                            ObjectType.values()[rec.type()], remote));
                    }
                }
            }
        } catch (Exception e) {
            if (badrec != null) {
                logger.error("Exception caught for callsign: "+badrec.callsign_to(), e);
            }
        }

        return ret;
    }

    public List<APRSObject> getNetCentralObjects(User loggedInUser, boolean priorityOnly, boolean aliveOnly, boolean generalOnly) {
        List<APRSObject> ret = new ArrayList<>();
        try {
            List<APRSObjectRecord> recList = aprsObjectRepository.findAll();
            if (!recList.isEmpty()) {
                for (APRSObjectRecord rec : recList) {
                    if (aliveOnly && (!rec.alive())) {
                        // skip dead objects 
                        continue;
                    }
                    if ("NETCENTRAL".equals(rec.source())) {
                        if (priorityOnly && generalOnly ) {
                            if ((ObjectType.values()[rec.type()] == ObjectType.EOC) || (ObjectType.values()[rec.type()] == ObjectType.SHELTER) || 
                                    (ObjectType.values()[rec.type()] == ObjectType.MEDICAL) || (ObjectType.values()[rec.type()] == ObjectType.NET)) {
                                ret.add(new APRSObject(rec.aprs_object_id(), rec.callsign_from(), 
                                                rec.callsign_to(), rec.alive(), rec.lat(), rec.lon(), rec.time(), rec.heard_time(), rec.comment(), 
                                                ObjectType.values()[rec.type()], false));
                            } else if (ObjectType.values()[rec.type()] == ObjectType.RESOURCE) {
                                ret.add(new APRSObject(rec.aprs_object_id(), rec.callsign_from(), 
                                                rec.callsign_to(), rec.alive(), rec.lat(), rec.lon(), rec.time(), rec.heard_time(), rec.comment(), 
                                                ObjectType.values()[rec.type()], false));
                            }
                        } else if (priorityOnly) {
                            if ((ObjectType.values()[rec.type()] == ObjectType.EOC) || (ObjectType.values()[rec.type()] == ObjectType.SHELTER) || 
                                    (ObjectType.values()[rec.type()] == ObjectType.MEDICAL) || (ObjectType.values()[rec.type()] == ObjectType.NET)) {
                                ret.add(new APRSObject(rec.aprs_object_id(), rec.callsign_from(), 
                                                rec.callsign_to(), rec.alive(), rec.lat(), rec.lon(), rec.time(), rec.heard_time(), rec.comment(), 
                                                ObjectType.values()[rec.type()], false));
                            }
                        } else if (generalOnly){
                            if (ObjectType.values()[rec.type()] == ObjectType.RESOURCE) {
                                ret.add(new APRSObject(rec.aprs_object_id(), rec.callsign_from(), 
                                                rec.callsign_to(), rec.alive(), rec.lat(), rec.lon(), rec.time(), rec.heard_time(), rec.comment(), 
                                                ObjectType.values()[rec.type()], false));
                            }
                        } else {
                            ret.add(new APRSObject(rec.aprs_object_id(), rec.callsign_from(), 
                                                rec.callsign_to(), rec.alive(), rec.lat(), rec.lon(), rec.time(), rec.heard_time(), rec.comment(), 
                                                ObjectType.values()[rec.type()], false));
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        return ret;
    }

    public APRSObject getObject(User loggedInUser, String id) {
        APRSObject ret = null;
        try {
            Optional<APRSObjectRecord> recOpt = aprsObjectRepository.findById(id);
            if (!recOpt.isEmpty()) {
                APRSObjectRecord rec = recOpt.get(); // should only be one
                boolean remote = true;
                if (rec.source().equals("NETCENTRAL")) {
                    remote = false;
                }
                ret = new APRSObject(rec.aprs_object_id(), rec.callsign_from(), 
                                rec.callsign_to(), rec.alive(), rec.lat(), rec.lon(), rec.time(), rec.heard_time(), rec.comment(), 
                                ObjectType.values()[rec.type()], remote);
            }
        } catch (Exception e) {
        }

        return ret;
    }

    public APRSObject getObjectByCallsign(User loggedInUser, String callsign) {
        APRSObject ret = null;
        try {
            List<APRSObjectRecord> recList = aprsObjectRepository.findBycallsign_to(callsign);
            if ((recList != null) && (!recList.isEmpty())) {
                APRSObjectRecord rec = recList.get(0); // should only be one
                boolean remote = true;
                if (rec.source().equals("NETCENTRAL")) {
                    remote = false;
                }
                ret = new APRSObject(rec.aprs_object_id(), rec.callsign_from(), 
                                rec.callsign_to(), rec.alive(), rec.lat(), rec.lon(), rec.time(), rec.heard_time(), rec.comment(), 
                                ObjectType.values()[rec.type()], remote);
            }
        } catch (Exception e) {
        }

        return ret;
    }

    public void deleteObject(User loggedInUser, String id) {
       try {
            APRSObject object = getObject(loggedInUser, id);
            Optional<APRSObjectRecord> recOpt = aprsObjectRepository.findById(id);
            if (!recOpt.isEmpty()) {
                aprsObjectRepository.delete(recOpt.get());  // there should only be one
                changePublisherAccessor.publishObjectUpdate(recOpt.get().callsign_to(), ChangePublisherAccessor.DELETE, object);
            }
        } catch (Exception e) {
        }
    }

    public void deleteObject(User loggedInUser, ObjectCreateRequest messageRequest) {
       try {
            List<APRSObjectRecord> recOpt = aprsObjectRepository.findBycallsign_to(messageRequest.callsign());
            if ((recOpt != null) && (!recOpt.isEmpty())) {
                APRSObject object = getObjectByCallsign(loggedInUser, messageRequest.callsign());
                aprsObjectRepository.delete(recOpt.get(0));  // there should only be one
                changePublisherAccessor.publishObjectUpdate(recOpt.get(0).callsign_to(), ChangePublisherAccessor.DELETE, object);
            }
        } catch (Exception e) {
        }
    }

    public void upObject(User loggedInUser, ObjectCreateRequest messageRequest) {
       actObject(loggedInUser, messageRequest, true);
    }

    public void downObject(User loggedInUser, ObjectCreateRequest messageRequest) {
       actObject(loggedInUser, messageRequest, false);
    }

    public void actObject(User loggedInUser, ObjectCreateRequest messageRequest, boolean alive) {
        ZonedDateTime now = ZonedDateTime.now();
        APRSObject obj = new APRSObject();
        obj.setAlive(alive);
        obj.setCallsignTo(messageRequest.callsign());
        obj.setComment(messageRequest.description());
        obj.setLat(messageRequest.lat());
        obj.setLon(messageRequest.lon());
        obj.setType(ObjectType.values()[messageRequest.type()]);
        obj.setLdtime(now);
        obj.setCallsignTo(messageRequest.callsign());
        String id = UUID.randomUUID().toString();

        Optional<APRSObject> innerAPRSObjectOpt = Optional.of(obj);
        createAPRSObject(loggedInUser, id, innerAPRSObjectOpt, "NETCENTRAL", now);
    }

    public void deleteWeatherReports(User systemUser, ZonedDateTime before) {
        try {
            aprsWeatherReportRepository.deleteByHeard_timeBefore(before);
        } catch (Exception e) {
            logger.error("Exception caught deleting weather reports", e);
        }
    }

    public void deletePositionReports(User systemUser, ZonedDateTime before) {
        try {
            aprsPositionRepository.deleteByHeard_timeBefore(before);
        } catch (Exception e) {
            logger.error("Exception caught deleting position reports", e);
        }
    }

    public void deleteStatusReports(User systemUser, ZonedDateTime before) {
        try {
            aprsStatusRepository.deleteByHeard_timeBefore(before);
        } catch (Exception e) {
            logger.error("Exception caught deleting position reports", e);
        }
    }

    public void deleteObjects(User systemUser, ZonedDateTime before) {
        try {
            aprsObjectRepository.deleteByHeard_timeBefore(before);
        } catch (Exception e) {
            logger.error("Exception caught deleting position reports", e);
        }
    }

    public void deleteAllData(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (!loggedInUser.getRole().equals(UserRole.SYSADMIN)) {
            // no privs
            logger.error("No privileges to allow delete all");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "No privileges to allow delete all");
        }

        aprsWeatherReportRepository.deleteAll();
        aprsPositionRepository.deleteAll();
        aprsStatusRepository.deleteAll();
        aprsObjectRepository.deleteAll();

        agwRawRepository.deleteAll();
        aprsAgreloRepository.deleteAll();
        aprsMaidenheadLocatorBeaconRepository.deleteAll();
        aprsMessageRepository.deleteAll();
        aprsQueryRepository.deleteAll();
        aprsRawRepository.deleteAll();
        aprsUnknownRepository.deleteAll();
        aprsMessageRepository.deleteAll();
    }

    public void deleteMessages(User systemUser, ZonedDateTime before) {
        try {
            aprsMessageRepository.deleteByHeard_timeBefore(before);
        } catch (Exception e) {
            logger.error("Exception caught deleting messages", e);
        }
    }

    public List<APRSRaw> getRawPackets(User loggedInUser) {
        List<APRSRaw> ret = new ArrayList<>();

       try {
            List<APRSRawRecord> recs = aprsRawRepository.findAll();
            if ((recs != null) && (!recs.isEmpty())) {
                for (APRSRawRecord rec : recs) {
                    APRSRaw rawObj = new APRSRaw();
                    rawObj.setData(rec.data().getBytes());
                    rawObj.setHeardTime(rec.heard_time());
                    ret.add(rawObj);
                }
            }
        } catch (Exception e) {
        }

        return ret;
    }
}
