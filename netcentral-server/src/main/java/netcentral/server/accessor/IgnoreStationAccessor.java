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
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.UserRole;
import netcentral.server.object.IgnoreStation;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.User;
import netcentral.server.record.IgnoreStationRecord;
import netcentral.server.repository.IgnoreStationRepository;
import netcentral.server.utils.TrackedStationTypeUtils;

@Singleton
public class IgnoreStationAccessor {
    private static final Logger logger = LogManager.getLogger(IgnoreStationAccessor.class);

    @Inject
    private IgnoreStationRepository ignoreStationRepository;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private TrackedStationAccessor trackedStationAccessor;

    private List<IgnoreStation> cache = null;

    private synchronized void updateCache(List<IgnoreStation> list) {
        cache = list;
    }
    private synchronized List<IgnoreStation> getCache() {
        return cache;
    }

    public List<IgnoreStation> getAll(User loggedInUser, String root) {
        List<IgnoreStation> ret = getAll(loggedInUser, root, false);
        if (ret == null) {
            ret = new ArrayList<>();
        }
        return ret;
    }

    private List<IgnoreStation> forceGetAll(User loggedInUser, String root) {
        // used to force a cache reset
        return getAll(loggedInUser, root, true);
    }

    private List<IgnoreStation> getAll(User loggedInUser, String root, boolean force) {
        List<IgnoreStation> ret = getCache();
        force = true; // force this for now

        if ((force) || (ret == null)) {
            ret = new ArrayList<>();
            try {
                List<IgnoreStationRecord> recs = ignoreStationRepository.findAll();

                if (!recs.isEmpty()) {
                    for (IgnoreStationRecord rec : recs) {
                        if ((root != null) && (!rec.callsign().startsWith(root))) {
                            // only take those with the optional root
                            continue;
                        }
                        ret.add(new IgnoreStation(rec.callsign(), rec.ignore_start_time(), TrackedStationTypeUtils.convertTrackedStationTypeStringToList(rec.types()), rec.lat(), rec.lon()));
                    }
                }
                updateCache(ret);
            } catch (Exception e) {
                updateCache(null);
            }
        }
        return ret;
    }

    public IgnoreStation create(User loggedInUser, IgnoreStation obj) {
        if (obj == null) {
            logger.debug("IgnoreStation is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station to ignore not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        try {
            Optional<IgnoreStationRecord> existingOptional = ignoreStationRepository.findById(obj.getCallsign());
            if (existingOptional.isPresent()) {
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Station already in ignore list");
            }
        } catch (Exception e) {
        }

        try {
            TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, obj.getCallsign());
            if (trackedStation != null) {
                obj.setLat(trackedStation.getLat());
                obj.setLon(trackedStation.getLon());
            }
        } catch (Exception e) {
        }

        IgnoreStationRecord src = new IgnoreStationRecord(obj.getCallsign(), obj.getIgnoreStartTime(), 
                                                            TrackedStationTypeUtils.convertTrackedStationTypesToString(obj.getTypes()),
                                                            obj.getLat(), obj.getLon());
        IgnoreStationRecord rec = ignoreStationRepository.save(src);
        if (rec != null) {
            forceGetAll(loggedInUser, null); // update cache; not performant, but also limited scale
            changePublisherAccessor.publishIgnoredUpdate(obj.getCallsign(), ChangePublisherAccessor.CREATE, obj);
            return obj;
        }
        logger.debug("Error persisting ignored station");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Error persisting ignored station");
    }

    public IgnoreStation delete(User loggedInUser, String callsign) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (callsign == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Ignored station callsign not provided");
        }

        Optional<IgnoreStationRecord> recOpt = ignoreStationRepository.findById(callsign);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Ignored station not found");
        }

        IgnoreStationRecord rec = recOpt.get();

        IgnoreStation ignoreStation = new IgnoreStation();
        ignoreStation.setCallsign(callsign);
        ignoreStation.setTypes(TrackedStationTypeUtils.convertTrackedStationTypeStringToList(rec.types()));
        changePublisherAccessor.publishIgnoredUpdate(ignoreStation.getCallsign(), ChangePublisherAccessor.DELETE, ignoreStation);
        ignoreStationRepository.delete(rec);
        forceGetAll(loggedInUser, null); // update cache; not performant, but also limited scale

        return null;
    }

    public void deleteAllData(User loggedInUser) {
        if ((loggedInUser == null) || (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            // no privs
            logger.error("No privileges to allow delete all");
            return;
        }

        ignoreStationRepository.deleteAll();
        forceGetAll(loggedInUser, null); // update cache
    }

    public IgnoreStation getIgnoredStation(User loggedInUser, String callsign) {
        IgnoreStation ret = null;

        if (callsign == null) {
            return ret;
        }

        List<IgnoreStation> ignoreList = getAll(loggedInUser, null);
        if (ignoreList != null) {
            for (IgnoreStation station : ignoreList) {
                if (station.getCallsign().equals(callsign)) {
                    ret = station;
                    break;
                }
            }
        }
        return ret;
    }

    public boolean isIgnored(User loggedInUser, String callsign) {
        boolean ret = false;

        if (callsign == null) {
            return ret;
        }

        IgnoreStation station = getIgnoredStation(loggedInUser, callsign);
        if (station != null) {
            ret = true;
        }

        return ret;
    }

}
