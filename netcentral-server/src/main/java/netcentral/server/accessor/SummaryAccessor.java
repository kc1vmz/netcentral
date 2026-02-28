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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.RegisteredTransceiver;
import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralEOCContactReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralEOCMobilizationReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralShelterStatusReport;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.auth.SessionCacheAccessor;
import netcentral.server.enums.ObjectEOCStatus;
import netcentral.server.enums.ObjectShelterState;
import netcentral.server.enums.ObjectShelterStatus;
import netcentral.server.enums.TrackedStationType;
import netcentral.server.object.Callsign;
import netcentral.server.object.CompletedNet;
import netcentral.server.object.CompletedParticipant;
import netcentral.server.object.IgnoreStation;
import netcentral.server.object.Net;
import netcentral.server.object.Participant;
import netcentral.server.object.RenderedMapItem;
import netcentral.server.object.ScheduledNet;
import netcentral.server.object.SummaryDashboard;
import netcentral.server.object.SummaryMapPoints;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.User;
import netcentral.server.object.report.ObjectEOCConsolidatedReport;
import netcentral.server.object.report.ObjectGeneralResourceStatusReport;
import netcentral.server.object.report.ObjectShelterStatusReport;

@Singleton
public class SummaryAccessor {
    private static final Logger logger = LogManager.getLogger(SummaryAccessor.class);

    @Inject
    private NetAccessor netAccessor;
    @Inject
    private ScheduledNetAccessor scheduledNetAccessor;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;
    @Inject
    private ParticipantAccessor participantAccessor;
    @Inject
    private CallsignAccessor callsignAccessor;
    @Inject
    private TrackedStationAccessor trackedStationAccessor;
    @Inject
    private CompletedNetAccessor completedNetAccessor;
    @Inject
    private CompletedParticipantAccessor completedParticipantAccessor;
    @Inject
    private UserAccessor userAccessor;
    @Inject
    private SessionCacheAccessor sessionCacheAccessor;
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;
    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private ReportAccessor reportAccessor;
    @Inject
    private IgnoreStationAccessor ignoreStationAccessor;
    @Inject
    private NetCentralServerConfigAccessor netCentralServerConfigAccessor;
    @Inject
    private ResourceObjectKVAccessor resourceObjectKVAccessor;


    public SummaryDashboard getDashboardSummary(User loggedInUser) {
        logger.debug("Get dashboard summary");
        SummaryDashboard ret = new SummaryDashboard();

        try {
            List<Net> nets = netAccessor.getAll(loggedInUser, null);
            if (nets != null) {
                ret.setActiveNets(nets.size());

                int participants = 0;

                for (Net net : nets) {
                    try {
                        List<Participant> p = netParticipantAccessor.getAllParticipants(loggedInUser, net);
                        if (p != null) {
                            participants += p.size();
                        }
                    } catch (Exception e) {
                    }
                }
                ret.setActiveNetParticipants(participants);
            }
        } catch (Exception e) {
        }

        try {
            List<ScheduledNet> nets = scheduledNetAccessor.getAll(loggedInUser, null);
            if (nets != null) {
                ret.setScheduledNets(nets.size());
            }
        } catch (Exception e) {
        }

        try {
            List<CompletedNet> nets = completedNetAccessor.getAll(loggedInUser, null);
            if (nets != null) {
                ret.setClosedNets(nets.size());

                int participants = 0;

                for (CompletedNet net : nets) {
                    try {
                        List<CompletedParticipant> p = completedParticipantAccessor.getAllByCompletedNetId(loggedInUser, net.getCompletedNetId());
                        if (p != null) {
                            participants += p.size();
                        }
                    } catch (Exception e) {
                    }
                }
                ret.setClosedNetParticipants(participants);
            }
        } catch (Exception e) {
        }

        try {
            List<Callsign> callsigns = callsignAccessor.getAll(loggedInUser, null);
            if (callsigns != null) {
                ret.setCallsignsHeard(callsigns.size());
            }
        } catch (Exception e) {
        }

        int trackedStations = 0;
        int weatherStationsHeard = 0;
        int iGatesHeard = 0;
        int digipeatersHeard = 0;
        int winlinkGatewaysHeard = 0;
        int internetServersHeard = 0;
        int othersHeard = 0;
        int repeatersHeard = 0;
        int bbsHeard = 0;
        int mmdvmHeard = 0;

        try {
            List<TrackedStation> stations = trackedStationAccessor.getAll(loggedInUser, null, null, null);
            if (stations != null) {
                ret.setStationsHeard(stations.size());
                for (TrackedStation station : stations) {
                    if (station.getType().equals(TrackedStationType.DIGIPEATER)) {
                        digipeatersHeard++;
                    } else if (station.getType().equals(TrackedStationType.IGATE)) {
                        iGatesHeard++;
                    } else if (station.getType().equals(TrackedStationType.WEATHER)) {
                        weatherStationsHeard++;
                    } else if (station.getType().equals(TrackedStationType.WINLINK_GATEWAY)) {
                        winlinkGatewaysHeard++;
                    } else if (station.getType().equals(TrackedStationType.IS)) {
                        internetServersHeard++;
                    } else if (station.getType().equals(TrackedStationType.REPEATER)) {
                        repeatersHeard++;
                    } else if (station.getType().equals(TrackedStationType.BBS)) {
                        bbsHeard++;
                    } else if (station.getType().equals(TrackedStationType.MMDVM)) {
                        mmdvmHeard++;
                    } else {
                        othersHeard++;
                    }

                    if (station.isTrackingActive()) {
                        trackedStations++;
                    }
                }
            }
        } catch (Exception e) {
        }

        ret.setDigipeatersHeard(digipeatersHeard);
        ret.setiGatesHeard(iGatesHeard);
        ret.setWeatherStationsHeard(weatherStationsHeard);
        ret.setTrackedStations(trackedStations);
        ret.setWinlinkGatewaysHeard(winlinkGatewaysHeard);
        ret.setInternetServersHeard(internetServersHeard);
        ret.setRepeatersHeard(repeatersHeard);
        ret.setBbsHeard(bbsHeard);
        ret.setMmdvmHeard(mmdvmHeard);
        ret.setOthersHeard(othersHeard);

        try {
            List<User> users = userAccessor.getAll(loggedInUser);
            if (users != null) {
                ret.setUsers(users.size());
            }
        } catch (Exception e) {
        }

        try {
            ret.setLoggedInUsers(sessionCacheAccessor.sessions());
        } catch (Exception e) {
        }

        try {
            List<RegisteredTransceiver> registeredTransceivers = registeredTransceiverAccessor.getAll(loggedInUser);
            int transmit = 0;
            int receive = 0;
            if (registeredTransceiverAccessor != null) {
                ret.setRegisteredTransceiversTotal(registeredTransceivers.size());
                for (RegisteredTransceiver t : registeredTransceivers) {
                    if (t.isEnabledReceive()) receive++;
                    if (t.isEnabledTransmit()) transmit++;
                }
            }
            ret.setRegisteredTransceiversReceive(receive);
            ret.setRegisteredTransceiversTransmit(transmit);
        } catch (Exception e) {
        }
        return ret;
    }


    public SummaryMapPoints getNetMapPoints(User loggedInUser, String callsign, Boolean includeTrackedStations, Boolean includeInfrastructure, Boolean includeObjects, Boolean includePriorityObjects) {
        SummaryMapPoints ret = new SummaryMapPoints(netCentralServerConfigAccessor.getLatitudeMin(), netCentralServerConfigAccessor.getLatitudeMax(), netCentralServerConfigAccessor.getLongitudeMin(), netCentralServerConfigAccessor.getLongitudeMax());
        List<RenderedMapItem> items = new ArrayList<>();

        // fix up inputs
        if (includeInfrastructure == null) {
            includeInfrastructure = false;
        }
        if (includeTrackedStations == null) {
            includeTrackedStations = false;
        }
        if (includeObjects == null) {
            includeObjects = false;
        }
        if (includePriorityObjects == null) {
            includePriorityObjects = false;
        }
        // get the participants
        try {
            Net net = netAccessor.getByCallsign(loggedInUser, callsign);
            if (net != null) {
                List<Participant> p = netParticipantAccessor.getAllParticipants(loggedInUser, net);
                if (p != null) {
                    for (Participant participant : p) {
                        Participant liveParticipant = participantAccessor.get(loggedInUser, participant.getCallsign());
                        liveParticipant.setStartTime(participant.getStartTime());
                        liveParticipant.setTacticalCallsign(participant.getTacticalCallsign());

                        // get curent location
                        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, participant.getCallsign());
                        liveParticipant.setLat(trackedStation.getLat());
                        liveParticipant.setLon(trackedStation.getLon());
                        liveParticipant.setElectricalPowerType(trackedStation.getElectricalPowerType());
                        liveParticipant.setBackupElectricalPowerType(trackedStation.getBackupElectricalPowerType());
                        liveParticipant.setRadioStyle(trackedStation.getRadioStyle());
                        liveParticipant.setTransmitPower(trackedStation.getTransmitPower());

                        if ((liveParticipant.getLat() != null) && (liveParticipant.getLon() != null)) {
                            String name = String.format("Callsign: %s Status: %s Voice: %s", liveParticipant.getCallsign(), 
                                (liveParticipant.getStatus() == null) ? "Unknown" : liveParticipant.getStatus(), 
                                (liveParticipant.getVoiceFrequency() == null) ? "Unknown" : liveParticipant.getVoiceFrequency());
                            RenderedMapItem item = new RenderedMapItem(
                                liveParticipant.getLon(), liveParticipant.getLat(), name, liveParticipant.getCallsign(), liveParticipant);
                            if (item.isValid()) {
                                items.add(item);
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.error("Exception caught building net map points", e);
        }

        // get the optional infrastructure
        if (includeInfrastructure) {
            List<TrackedStation> trackedStations = new ArrayList<>();
            try {
                List<TrackedStation> trackedStationsPartial = trackedStationAccessor.getAll(loggedInUser, null, null, TrackedStationType.DIGIPEATER.name());
                if (trackedStationsPartial != null) {
                    trackedStations.addAll(trackedStationsPartial);
                }
            } catch (Exception e) {
            }
            try {
                List<TrackedStation> trackedStationsPartial = trackedStationAccessor.getAll(loggedInUser, null, null, TrackedStationType.IGATE.name());
                if (trackedStationsPartial != null) {
                    trackedStations.addAll(trackedStationsPartial);
                }
            } catch (Exception e) {
            }
            try {
                List<TrackedStation> trackedStationsPartial = trackedStationAccessor.getAll(loggedInUser, null, null, TrackedStationType.IS.name());
                if (trackedStationsPartial != null) {
                    trackedStations.addAll(trackedStationsPartial);
                }
            } catch (Exception e) {
            }
            try {
                List<TrackedStation> trackedStationsPartial = trackedStationAccessor.getAll(loggedInUser, null, null, TrackedStationType.REPEATER.name());
                if (trackedStationsPartial != null) {
                    trackedStations.addAll(trackedStationsPartial);
                }
            } catch (Exception e) {
            }
            try {
                List<TrackedStation> trackedStationsPartial = trackedStationAccessor.getAll(loggedInUser, null, null, TrackedStationType.BBS.name());
                if (trackedStationsPartial != null) {
                    trackedStations.addAll(trackedStationsPartial);
                }
            } catch (Exception e) {
            }
            try {
                List<TrackedStation> trackedStationsPartial = trackedStationAccessor.getAll(loggedInUser, null, null, TrackedStationType.MMDVM.name());
                if (trackedStationsPartial != null) {
                    trackedStations.addAll(trackedStationsPartial);
                }
            } catch (Exception e) {
            }
            try {
                List<TrackedStation> trackedStationsPartial = trackedStationAccessor.getAll(loggedInUser, null, null, TrackedStationType.WEATHER.name());
                if (trackedStationsPartial != null) {
                    trackedStations.addAll(trackedStationsPartial);
                }
            } catch (Exception e) {
            }
            try {
                List<TrackedStation> trackedStationsPartial = trackedStationAccessor.getAll(loggedInUser, null, null, TrackedStationType.WINLINK_GATEWAY.name());
                if (trackedStationsPartial != null) {
                    trackedStations.addAll(trackedStationsPartial);
                }
            } catch (Exception e) {
            }

            for (TrackedStation trackedStation : trackedStations) {
                if ((trackedStation.getLat() != null) && (trackedStation.getLon() != null)) {
                    if ((!includeTrackedStations) || ((includeTrackedStations) && (trackedStation.isTrackingActive()))) {
                        RenderedMapItem item = new RenderedMapItem(trackedStation.getLon(), trackedStation.getLat(), trackedStation.getCallsign(), trackedStation.getType().name(), trackedStation);
                        if (item.isValid()) {
                            item.setInfrastructure(true);
                            items.add(item);
                        }
                    }
                }
            }
        }

        // get the optional objects
        if (includeObjects) {
            List<APRSObject> objects = new ArrayList<>();
            try {
                objects = aprsObjectAccessor.getObjects(loggedInUser, false, false);
            } catch (Exception e) {
            }

            for (APRSObject object : objects) {
                if ((object.getLat() != null) && (object.getLon() != null)) {
                    Object renderObject = object;
                    if (object.getType().equals(ObjectType.SHELTER)) {
                        renderObject = getShelterInformation(loggedInUser, object);
                    } else if (object.getType().equals(ObjectType.EOC)) {
                        renderObject = getEOCInformation(loggedInUser, object);
                    } else if (object.getType().equals(ObjectType.MEDICAL)) {
                        renderObject = getMedicalInformation(loggedInUser, object);
                    }
                    RenderedMapItem item = new RenderedMapItem(object.getLon(), object.getLat(), object.getComment(), object.getCallsignTo(), renderObject);
                    if (item.isValid()) {
                        item.setObject(true);
                        items.add(item);
                    }
                }
            }
        }

        // get the optional objects
        if (includePriorityObjects) {
            List<APRSObject> objects = new ArrayList<>();
            try {
                objects = aprsObjectAccessor.getObjects(loggedInUser, true, true);
            } catch (Exception e) {
            }

            for (APRSObject object : objects) {
                if ((object.getLat() != null) && (object.getLon() != null)) {
                    Object renderObject = object;
                    if (object.getType().equals(ObjectType.SHELTER)) {
                        renderObject = getShelterInformation(loggedInUser, object);
                    } else if (object.getType().equals(ObjectType.EOC)) {
                        renderObject = getEOCInformation(loggedInUser, object);
                    } else if (object.getType().equals(ObjectType.MEDICAL)) {
                        renderObject = getMedicalInformation(loggedInUser, object);
                    }
                    RenderedMapItem item = new RenderedMapItem(object.getLon(), object.getLat(), object.getComment(), object.getCallsignTo(), renderObject);
                    if (item.isValid()) {
                        item.setObject(true);
                        items.add(item);
                    }
                }
            }
        }

        if (!items.isEmpty()) {
            // determine the center and bounds
            double lonLow = 1000;
            double lonHigh = -1000;
            double latLow = 1000;
            double latHigh = -1000;

            for (RenderedMapItem item : items) {
                if (item.getLongitude() < lonLow) {
                    lonLow = item.getLongitude();
                }
                if (item.getLongitude() > lonHigh) {
                    lonHigh = item.getLongitude();
                }
                if (item.getLatitude() < latLow) {
                    latLow = item.getLatitude();
                }
                if (item.getLatitude() > latHigh) {
                    latHigh = item.getLatitude();
                }
            }

            ret.setCenterLongitude(lonLow + ((lonHigh - lonLow) / 2));
            ret.setCenterLatitude(latLow + ((latHigh - latLow) / 2));
            ret.setMinLatitude(latLow);
            ret.setMaxLatitude(latHigh);
            ret.setMinLongitude(lonLow);
            ret.setMaxLongitude(lonHigh);
        }

        ret.setItems(items);
        return ret;
    }

    public SummaryMapPoints getObjectTypeMapPoints(User loggedInUser, String objectType) {
        SummaryMapPoints ret = new SummaryMapPoints(netCentralServerConfigAccessor.getLatitudeMin(), netCentralServerConfigAccessor.getLatitudeMax(), netCentralServerConfigAccessor.getLongitudeMin(), netCentralServerConfigAccessor.getLongitudeMax());
        List<RenderedMapItem> items = new ArrayList<>();

        if (objectType == null) {
            // fall through
        } else if (objectType.equalsIgnoreCase("callsign")) {
            // no map points
        } else if (objectType.equalsIgnoreCase("object")) {
            try {
                List<APRSObject> objects = aprsObjectAccessor.getObjects(loggedInUser, false, false);
                if (objects != null) {
                    for (APRSObject object : objects) {
                        Object renderObject = object;
                        if (object.getType().equals(ObjectType.SHELTER)) {
                            renderObject = getShelterInformation(loggedInUser, object);
                        } else if (object.getType().equals(ObjectType.EOC)) {
                            renderObject = getEOCInformation(loggedInUser, object);
                        } else if (object.getType().equals(ObjectType.MEDICAL)) {
                            renderObject = getMedicalInformation(loggedInUser, object);
                        } else if (object.getType().equals(ObjectType.RESOURCE)) {
                            renderObject = getGeneralResourceInformation(loggedInUser, object);
                        }
                        if ((object.getLat() != null) && (object.getLon() != null)) {
                            RenderedMapItem item = new RenderedMapItem(
                                object.getLon(), object.getLat(), getNameField(object), getTitleField(object), renderObject);
                            if (item.isValid()) {
                                item.setObject(true);
                                items.add(item);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Exception caught building object type map points", e);
            }
        } else if (objectType.equalsIgnoreCase("priorityobject")) {
            try {
                List<APRSObject> objects = aprsObjectAccessor.getObjects(loggedInUser, true, false);
                if (objects != null) {
                    for (APRSObject object : objects) {
                        Object renderObject = object;
                        if (object.getType().equals(ObjectType.SHELTER)) {
                            renderObject = getShelterInformation(loggedInUser, object);
                        } else if (object.getType().equals(ObjectType.EOC)) {
                            renderObject = getEOCInformation(loggedInUser, object);
                        } else if (object.getType().equals(ObjectType.MEDICAL)) {
                            renderObject = getMedicalInformation(loggedInUser, object);
                        }
                        if ((object.getLat() != null) && (object.getLon() != null)) {
                            RenderedMapItem item = new RenderedMapItem(
                                object.getLon(), object.getLat(), getNameField(object), getTitleField(object), renderObject);
                            if (item.isValid()) {
                                item.setObject(true);
                                items.add(item);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Exception caught building object type map points", e);
            }
        } else if (objectType.equalsIgnoreCase("generalresource")) {
            try {
                List<APRSObject> objects = aprsObjectAccessor.getObjects(loggedInUser, false, true);
                if (objects != null) {
                    for (APRSObject object : objects) {
                        Object renderObject = object;
                        if (object.getType().equals(ObjectType.RESOURCE)) {
                            renderObject = getGeneralResourceInformation(loggedInUser, object);
                        }
                        if ((object.getLat() != null) && (object.getLon() != null)) {
                            RenderedMapItem item = new RenderedMapItem(
                                object.getLon(), object.getLat(), getNameField(object), getTitleField(object), renderObject);
                            if (item.isValid()) {
                                item.setObject(true);
                                items.add(item);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Exception caught building object type map points", e);
            }
        } else if (objectType.equalsIgnoreCase("ignore")) {
            try {
                List<IgnoreStation> objects = ignoreStationAccessor.getAll(loggedInUser, null);
                if (objects != null) {
                    for (IgnoreStation object : objects) {
                        Object renderObject = object;
                        if ((object.getLat() != null) && (object.getLon() != null)) {
                            RenderedMapItem item = new RenderedMapItem(
                                object.getLon(), object.getLat(), object.getCallsign(), object.getType().name(), renderObject);
                            if (item.isValid()) {
                                item.setObject(true);
                                items.add(item);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Exception caught building object type map points", e);
            }
        } else {
            try {
                List<TrackedStation> trackedStations = trackedStationAccessor.getAll(loggedInUser, null, null, objectType);
                if (trackedStations != null) {
                    for (TrackedStation trackedStation : trackedStations) {
                        if ((trackedStation.getLat() != null) && (trackedStation.getLon() != null)) {
                            String name = trackedStation.getCallsign();
                            RenderedMapItem item = new RenderedMapItem(
                                trackedStation.getLon(), trackedStation.getLat(), name, trackedStation.getCallsign(), trackedStation);
                            if (item.isValid()) {
                                items.add(item);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Exception caught building object type map points", e);
            }
        }

        if (!items.isEmpty()) {
            // determine the center and bounds
            double lonLow = 1000;
            double lonHigh = -1000;
            double latLow = 1000;
            double latHigh = -1000;

            for (RenderedMapItem item : items) {
                if (item.getLongitude() < lonLow) {
                    lonLow = item.getLongitude();
                }
                if (item.getLongitude() > lonHigh) {
                    lonHigh = item.getLongitude();
                }
                if (item.getLatitude() < latLow) {
                    latLow = item.getLatitude();
                }
                if (item.getLatitude() > latHigh) {
                    latHigh = item.getLatitude();
                }
            }

            ret.setCenterLongitude(lonLow + ((lonHigh - lonLow) / 2));
            ret.setCenterLatitude(latLow + ((latHigh - latLow) / 2));
            ret.setMinLatitude(latLow);
            ret.setMaxLatitude(latHigh);
            ret.setMinLongitude(lonLow);
            ret.setMaxLongitude(lonHigh);
        }
        ret.setItems(items);
        return ret;
    }


    public Object getMedicalInformation(User loggedInUser, APRSObject object) {
        return object;
    }


    public Object getEOCInformation(User loggedInUser, APRSObject object) {
        ObjectEOCConsolidatedReport report = new ObjectEOCConsolidatedReport();
        report.setCallsign(object.getCallsignTo());
        report.setDescription(object.getComment());
        report.setLat(object.getLat());
        report.setLon(object.getLon());
        report.setAlive(object.isAlive());
        report.setType(ObjectType.EOC);

        // get latest values
        try {
            APRSNetCentralEOCContactReport contactReport = reportAccessor.getEOCContactInformation(loggedInUser, report.getCallsign());
            if (contactReport != null) {
                report.setDirectorName(contactReport.getDirectorName());
                report.setIndicentCommanderName(contactReport.getIncidentCommanderName());
            }
        } catch (Exception e) {
        }

        try {
            APRSNetCentralEOCMobilizationReport mobilizationReport = reportAccessor.getEOCMobilizationInformation(loggedInUser, report.getCallsign());
            if (mobilizationReport != null) {
                report.setLevel(mobilizationReport.getLevel());
                report.setStatus(ObjectEOCStatus.values()[mobilizationReport.getStatus()]);
            }
        } catch (Exception e) {
        }
        return report;
    }


    public Object getShelterInformation(User loggedInUser, APRSObject object) {
        ObjectShelterStatusReport report = new ObjectShelterStatusReport();
        report.setCallsign(object.getCallsignTo());
        report.setDescription(object.getComment());
        report.setLat(object.getLat());
        report.setLon(object.getLon());
        report.setAlive(object.isAlive());
        report.setType(ObjectType.SHELTER);

        try {
            APRSNetCentralShelterStatusReport subReport = reportAccessor.getLatestShelterStatusReport(loggedInUser, report.getCallsign());
            if (subReport != null) {
                report.setStatus(ObjectShelterStatus.values()[subReport.getStatus()]);
                report.setState(ObjectShelterState.values()[subReport.getState()]);
                report.setMessage(subReport.getMessage());
                report.setLastReportedTime(subReport.getDateReported());
            }
        } catch (Exception e) {
        }

        return report;
    }

    public Object getGeneralResourceInformation(User loggedInUser, APRSObject object) {
        ObjectGeneralResourceStatusReport report = new ObjectGeneralResourceStatusReport();
        report.setCallsign(object.getCallsignTo());
        report.setDescription(object.getComment());
        report.setLat(object.getLat());
        report.setLon(object.getLon());
        report.setAlive(object.isAlive());
        report.setType(ObjectType.RESOURCE);
        report.setMessage(object.getComment());
        report.setLastReportedTime(object.getLdtime());

        String resourceType = resourceObjectKVAccessor.get(loggedInUser, object.getId(), "type");
        report.setResourceType(resourceType);

        return report;
    }

    private String getTitleField(APRSObject object) {
        return object.getCallsignTo();
    }

    private String getNameField(APRSObject object) {
        return object.getCallsignTo();
    }
}
