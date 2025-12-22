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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.RadioStyle;
import netcentral.server.enums.TrackedStationStatus;
import netcentral.server.enums.TrackedStationType;
import netcentral.server.enums.UserRole;
import netcentral.server.object.RenderedMapItem;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.User;
import netcentral.server.record.TrackedStationRecord;
import netcentral.server.repository.TrackedStationRepository;

@Singleton
public class TrackedStationAccessor {
    private static final Logger logger = LogManager.getLogger(TrackedStationAccessor.class);

    @Inject
    private TrackedStationRepository trackedStationRepository;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;

    public List<TrackedStation> getAll(User loggedInUser, String name, String callsign, String type) {
        List<TrackedStationRecord> recs;
        
        if (type == null) {
            recs = trackedStationRepository.findAll();
        } else {
            recs = trackedStationRepository.findBytype(TrackedStationType.valueOf(type).ordinal());
        }
        List<TrackedStation> ret = new ArrayList<>();

        if (!recs.isEmpty()) {
            for (TrackedStationRecord rec : recs) {
                if ((callsign != null) && (rec.callsign() != null) && (!callsign.startsWith(rec.callsign()))) {
                    continue;
                }
                if ((name != null) && (!rec.name().equals(name))) {
                    continue;
                }

                ret.add(new TrackedStation(rec.tracked_station_id(), TrackedStationType.values()[rec.type()], rec.name(), rec.description(), rec.callsign(), rec.lat(),
                            rec.lon(), rec.frequency_tx(), rec.frequency_rx(), rec.tone(), rec.last_heard_time(), rec.tracking_active(),
                            TrackedStationStatus.values()[rec.status()], 
                            rec.ip_address(), ElectricalPowerType.values()[rec.electrical_power_type()], ElectricalPowerType.values()[rec.backup_electrical_power_type()],
                            RadioStyle.values()[rec.radio_style()], rec.transmit_power()));
            }
        }

        Collections.sort(ret, new Comparator<TrackedStation>() {
            @Override
            public int compare(TrackedStation obj1, TrackedStation obj2) {
                return obj1.getCallsign().compareTo(obj2.getCallsign());
            }
        });

        return ret;
    }

    public List<TrackedStation> getByRoot(User loggedInUser, String rootcallsign) {
        List<TrackedStation> ret = new ArrayList<>();
        if (rootcallsign == null) {
            return ret;
        }
        
        try {
            List<TrackedStationRecord> recs = trackedStationRepository.findAll();
            if (recs != null) {
                for (TrackedStationRecord rec : recs) {
                    if (rec.callsign().startsWith(rootcallsign)) {
                        ret.add(new TrackedStation(rec.tracked_station_id(), TrackedStationType.values()[rec.type()], rec.name(), rec.description(), rec.callsign(), rec.lat(),
                                    rec.lon(), rec.frequency_tx(), rec.frequency_rx(), rec.tone(), rec.last_heard_time(), rec.tracking_active(),
                                    TrackedStationStatus.values()[rec.status()], 
                                    rec.ip_address(), ElectricalPowerType.values()[rec.electrical_power_type()], ElectricalPowerType.values()[rec.backup_electrical_power_type()],
                                    RadioStyle.values()[rec.radio_style()], rec.transmit_power()));
                    }
                }
            }
        } catch (Exception e) {
        }

        return ret;
    }

    public TrackedStation get(User loggedInUser, String id) {
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station id not provided");
        }
        Optional<TrackedStationRecord> recOpt = trackedStationRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station not found");
        }
        TrackedStationRecord rec = recOpt.get();
        return new TrackedStation(rec.tracked_station_id(), TrackedStationType.values()[rec.type()], rec.name(), rec.description(), rec.callsign(), rec.lat(),
                            rec.lon(), rec.frequency_tx(), rec.frequency_rx(), rec.tone(), rec.last_heard_time(), rec.tracking_active(),
                            TrackedStationStatus.values()[rec.status()], rec.ip_address(), ElectricalPowerType.values()[rec.electrical_power_type()], ElectricalPowerType.values()[rec.backup_electrical_power_type()],
                            RadioStyle.values()[rec.radio_style()], rec.transmit_power());
    }

    public TrackedStation getByCallsign(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("Net not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station not provided");
        }
        try {
            List<TrackedStationRecord> recOpt = trackedStationRepository.findBycallsign(callsign);
            if (recOpt != null) {
                TrackedStationRecord rec = recOpt.get(0); // should only be one
                if (rec == null) {
                    throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station not found");
                }
                return new TrackedStation(rec.tracked_station_id(), TrackedStationType.values()[rec.type()], rec.name(), rec.description(), rec.callsign(), rec.lat(),
                            rec.lon(), rec.frequency_tx(), rec.frequency_rx(), rec.tone(), rec.last_heard_time(), rec.tracking_active(),
                            TrackedStationStatus.values()[rec.status()], rec.ip_address(), ElectricalPowerType.values()[rec.electrical_power_type()], ElectricalPowerType.values()[rec.backup_electrical_power_type()],
                            RadioStyle.values()[rec.radio_style()], rec.transmit_power());
            }
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station not found");

        } catch (Exception e) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station not found");
        }
    }

    public TrackedStation create(User loggedInUser, TrackedStation obj) {
        if (obj == null) {
            logger.debug("Station is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        try {
            List<TrackedStationRecord> existingList = trackedStationRepository.findByname(obj.getName());
            if ((existingList != null) && (!existingList.isEmpty())) {
                logger.debug("Station name already exists");
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station already exists");
            }
        } catch (Exception e) {
        }

        String id = UUID.randomUUID().toString(); // pre-assign instance id for completed net
        TrackedStationRecord src = new TrackedStationRecord(id, obj.getCallsign(), obj.getLat(), obj.getLon(), obj.getName(),
                                                obj.getDescription(), obj.getFrequencyTx(), obj.getFrequencyRx(), obj.getTone(), obj.getLastHeard(),
                                                obj.isTrackingActive(), obj.getStatus().ordinal(), obj.getIpAddress(), obj.getType().ordinal(),
                                                obj.getElectricalPowerType().ordinal(), obj.getBackupElectricalPowerType().ordinal(), obj.getRadioStyle().ordinal(), obj.getTransmitPower());

        TrackedStationRecord rec = trackedStationRepository.save(src);
        if (rec != null) {
            TrackedStation trackedStationFinal = get(loggedInUser, id);
            changePublisherAccessor.publishTrackedStationUpdate(obj.getCallsign(), "Create", trackedStationFinal);
            return trackedStationFinal;
        }

        logger.debug("Station not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station not created");
    }

    public TrackedStation update(User loggedInUser, String id, TrackedStation obj) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station id not provided");
        }
        if (obj == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station not provided");
        }
        if ((obj.getName() == null) || (obj.getName().isEmpty()) || (obj.getName().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid name");
        }

        Optional<TrackedStationRecord> recOpt = trackedStationRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station not found");
        }
        TrackedStationRecord rec = recOpt.get();
        TrackedStationRecord updatedRec = new TrackedStationRecord(rec.tracked_station_id(), obj.getCallsign(), 
                                                (obj.getLat() != null) ? obj.getLat() : rec.lat(), 
                                                (obj.getLon() != null) ? obj.getLon() : rec.lon(), 
                                                obj.getName(),
                                                obj.getDescription(), obj.getFrequencyTx(), obj.getFrequencyRx(), obj.getTone(), obj.getLastHeard(),
                                                obj.isTrackingActive(), obj.getStatus().ordinal(), obj.getIpAddress(),
                                                (obj.getType().ordinal() != TrackedStationType.UNKNOWN.ordinal()) ? obj.getType().ordinal() : rec.type(),
                                                (obj.getElectricalPowerType().ordinal() != ElectricalPowerType.UNKNOWN.ordinal()) ? obj.getElectricalPowerType().ordinal() : rec.electrical_power_type(), 
                                                (obj.getBackupElectricalPowerType().ordinal() != ElectricalPowerType.UNKNOWN.ordinal()) ? obj.getBackupElectricalPowerType().ordinal() : rec.backup_electrical_power_type(), 
                                                (obj.getRadioStyle().ordinal() != RadioStyle.UNKNOWN.ordinal()) ? obj.getRadioStyle().ordinal() : rec.radio_style(), 
                                                (obj.getTransmitPower() != 0) ? obj.getTransmitPower() : rec.transmit_power());
        trackedStationRepository.update(updatedRec);

        TrackedStation trackedStationFinal = get(loggedInUser, id);
        changePublisherAccessor.publishTrackedStationUpdate(obj.getCallsign(), "Update", trackedStationFinal);
        return trackedStationFinal;
    }

    public TrackedStation delete(User loggedInUser, String id) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station id not provided");
        }

        Optional<TrackedStationRecord> recOpt = trackedStationRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station not found");
        }
   
        TrackedStation trackedStation = get(loggedInUser, id);
        changePublisherAccessor.publishTrackedStationUpdate(recOpt.get().callsign(), "Delete", trackedStation);
        trackedStationRepository.delete(recOpt.get());
        
        return null;
    }

    public TrackedStation createWeatherStation(User loggedInUser, String callsign, String lat, String lon) {
        return createTrackedStation(loggedInUser, callsign, TrackedStationType.WEATHER, callsign, lat, lon);
    }

    public TrackedStation createStation(User loggedInUser, String callsign) {
        return createTrackedStation(loggedInUser, callsign, TrackedStationType.STATION, callsign, null, null);
    }

    private TrackedStation createTrackedStation(User loggedInUser, String callsign, TrackedStationType type, String name, String lat, String lon) {
        TrackedStation ret = null;

        try {
            List<TrackedStation> stations = getAll(loggedInUser, name, callsign, type.toString());
            if ((stations != null) && (!stations.isEmpty())) {
                ret = stations.get(0);
            }
        } catch (Exception e) {
            logger.error("Exception caught enumerating tracked stations prior to create", e);
        }

        if (ret == null) {
            // not found - create it
            ret = new TrackedStation(null, type, name, null, callsign, 
                                lat, lon, null, null, null, ZonedDateTime.now(), false, TrackedStationStatus.UP, null, ElectricalPowerType.UNKNOWN, ElectricalPowerType.UNKNOWN, RadioStyle.UNKNOWN, 0);
            ret = create(loggedInUser, ret);
        } else {
            ret.setLastHeard(ZonedDateTime.now());
            ret.setLat(lat);
            ret.setLon(lon);
            ret = update(loggedInUser, ret.getId(), ret);
        }

        return ret;
    }

    public RenderedMapItem getMapPoint(User loggedInUser, TrackedStation trackedStation) {
        if (trackedStation != null) {
            return new RenderedMapItem(trackedStation.getLon(), trackedStation.getLat(), trackedStation.getCallsign(), "", trackedStation);
        } 
        return null;
    }

    public void deleteTrackedStations(User systemUser, ZonedDateTime before) {
        try {
            trackedStationRepository.deleteByLast_heard_timeBefore(before);
        } catch (Exception e) {
            logger.error("Exception caught deleting tracked stations", e);
        }
    }

    public void deleteAllData(User loggedInUser) {
        if ((loggedInUser == null) || (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            // no privs
            logger.error("No privileges to allow delete all");
            return;
        }

        trackedStationRepository.deleteAll();
    }
}
