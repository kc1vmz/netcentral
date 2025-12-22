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
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.APRSObject;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.CallsignType;
import netcentral.server.enums.UserRole;
import netcentral.server.object.CallsignAce;
import netcentral.server.object.Net;
import netcentral.server.object.Participant;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.User;
import netcentral.server.record.CallsignAceRecord;
import netcentral.server.repository.CallsignAceRepository;
import netcentral.server.utils.ConvertLonLat;

@Singleton
public class CallsignAceAccessor {
    private static final Logger logger = LogManager.getLogger(CallsignAceAccessor.class);

    @Inject
    private CallsignAceRepository callsignAceRepository;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;
    @Inject
    private NetAccessor netAccessor;
    @Inject
    private TrackedStationAccessor trackedStationAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;


    public boolean isWriteable(User loggedInUser, APRSObject priorityObject, String callsignChecked) {
        boolean ret = false;
        String callsignTarget = priorityObject.getCallsignFrom();

        List<CallsignAce> acl = getAcl(loggedInUser, callsignTarget);
        if ((acl == null) || (acl.isEmpty())) {
            return true;  // nothing restricting access, assume open to all
        }

        String root = getCallsignFromStationId(callsignChecked);

        for (CallsignAce ace: acl) {
            if (ace.getType().equals(CallsignType.STATION) && (ace.getCallsignChecked().startsWith(root))) {
                ret = ace.isAllowed();
                break;
            } else if (ace.getType().equals(CallsignType.WITHIN_PROXIMITY)) {
                if (ace.getProximity() != null) {
                    if (isInGeofence(loggedInUser, priorityObject, callsignChecked, ace.getProximity())) {  
                        ret = ace.isAllowed();
                    }
                    break;
                }
            } else if (ace.getType().equals(CallsignType.NET)) {
                List<Net> nets = null;
                if (root.equals("*")) {
                    nets = netAccessor.getAll(loggedInUser, null);
                } else {
                    Participant participant = new Participant();
                    participant.setCallsign(callsignChecked);

                    nets = netParticipantAccessor.getAllNets(loggedInUser, participant);
                }

                if ((nets != null) && (!nets.isEmpty())) {
                    for (Net net : nets) {
                        // if callsignChecked is in the list, then its good
                        if (net != null) {
                            List<Participant> netParticipants = netParticipantAccessor.getAllParticipants(loggedInUser, net);
                            if (netParticipants != null) {
                                for (Participant netParticipant : netParticipants) {
                                    if (netParticipant.getCallsign().startsWith(root)) {
                                        ret = ace.isAllowed();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return ret;
    }

    /*
     * https://www.google.com/search?q=Haversine+formula&oq=given+longitude+latitude+determine+in+geofence&gs_lcrp=EgZjaHJvbWUyBggAEEUYOTIHCAEQIRigATIHCAIQIRigATIHCAMQIRigATIHCAQQIRifBTIHCAUQIRifBTIHCAYQIRifBTIHCAcQIRifBTIHCAgQIRifBTIHCAkQIRifBdIBCTE3NDYzajBqN6gCALACAA&sourceid=chrome&ie=UTF-8&mstk=AUtExfD2oqvZYpc8AiI7ABGWBxEXP2FTYF1EkheHqIrRUr5rat1BAVWsqIRo7ShVxjsXhTioFV4UasV9QsqXcuLHWdcUcXPGllRB8w5nz6xAo2wIsxUAa5mTOUB1vDqhfD65K-T490Rz3zYDd6g0FwnOLZxhW5m9rbJs7mHXfV5GiaR_o-Q&csui=3&ved=2ahUKEwjK6-awwcyQAxW_FFkFHYSeGh8QgK4QegQIAhAC
     * Haversine formula
     *     import math

    # Coordinates in decimal degrees (e.g. 2.89078, 12.79797)
    lon1, lat1 = coord1
    lon2, lat2 = coord2

    R = 6371000  # radius of Earth in meters
    phi_1 = math.radians(lat1)
    phi_2 = math.radians(lat2)

    delta_phi = math.radians(lat2 - lat1)
    delta_lambda = math.radians(lon2 - lon1)

    a = math.sin(delta_phi / 2.0) ** 2 + math.cos(phi_1) * math.cos(phi_2) * math.sin(delta_lambda / 2.0) ** 2
    
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

    meters = R * c  # output distance in meters
    km = meters / 1000.0  # output distance in kilometers

    meters = round(meters, 3)
    km = round(km, 3)

     */
    private boolean isInGeofence(User loggedInUser, APRSObject priorityObject, String callsignChecked, int proximityInFeet) {
        boolean ret = false;
        try {
            TrackedStation checked = trackedStationAccessor.getByCallsign(loggedInUser, callsignChecked);

            // get location of target
            // get location of checked
            // determine distance from each in radians
            long R = 6371000L;
            double meterToFeet = 3.28084;
            double lonTarget = ConvertLonLat.convertLongitude(priorityObject.getLon());
            double latTarget = ConvertLonLat.convertLatitude(priorityObject.getLat());
            double lonChecked = ConvertLonLat.convertLongitude(checked.getLon());
            double latChecked = ConvertLonLat.convertLatitude(checked.getLat());
            double phiTarget, phiChecked;
            double deltaLon, deltaLat;

            phiTarget = Math.toRadians(latTarget);
            phiChecked = Math.toRadians(latChecked);
            deltaLat = Math.toRadians(latTarget - latChecked);
            deltaLon = Math.toRadians(lonTarget - lonChecked);
            double a = Math.sin(Math.pow(deltaLat/2, 2) + Math.cos(phiTarget) * Math.cos(phiChecked) * Math.pow(Math.sin(deltaLon / 2), 2));
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double feet = R * c * meterToFeet;

            if (feet <= proximityInFeet) {
                ret = true;
            }
        } catch (Exception e) {
        }

        return ret;
    }

    private String getCallsignFromStationId(String callsign) {
        if (callsign != null) {
            if (callsign.contains("-")) {
                int index = callsign.indexOf("-");
                return callsign.substring(0, index);
            }
        }
        return callsign;
    }

    public List<CallsignAce> getAcl(User loggedInUser, String callsignTarget) {
        logger.debug("getAll() called");
        List<CallsignAceRecord> recs = callsignAceRepository.findBycallsign_target(callsignTarget);
        List<CallsignAce> ret = new ArrayList<>();

        if ((recs != null) && (!recs.isEmpty())) {
            for (CallsignAceRecord rec : recs) {
                ret.add(new CallsignAce(rec.callsign_ace_id(), rec.callsign_target(), rec.callsign_checked(), 
                                            CallsignType.values()[rec.callsign_checked_type()], rec.allowed(), rec.proximity()));
            }
        }

        return ret;
    }

    public CallsignAce addAce(User loggedInUser, String callsignTarget, String callsignChecked, CallsignType type, boolean allowed, Integer proximity) {
        CallsignAce ace = new CallsignAce(UUID.randomUUID().toString(), callsignTarget, callsignChecked, type, allowed, proximity);

        CallsignAceRecord aceRecord = new CallsignAceRecord(ace.getId(), ace.getCallsignTarget(), ace.getCallsignChecked(), ace.getType().ordinal(), ace.isAllowed(), ace.getProximity());

        CallsignAceRecord existing = null;
        try {
            existing = callsignAceRepository.find(callsignTarget, callsignChecked);
        } catch (Exception e) {
            existing = null;
        }

        try {
            if (existing != null) {
                callsignAceRepository.update(aceRecord);
                changePublisherAccessor.publishCallsignACEUpdate(callsignTarget, "Update", ace);
            } else {
                callsignAceRepository.save(aceRecord);
                changePublisherAccessor.publishCallsignACEUpdate(callsignTarget, "Create", ace);
            }

        } catch (Exception e) {
            logger.error("Could not update ACE for callsign " + callsignTarget, e);
            ace = null;
        }
  
        return ace;
    }

    public CallsignAce updateAce(User loggedInUser, String callsignTarget, String callsignChecked, CallsignType type, boolean allowed, Integer proximity) {
        CallsignAce ace = new CallsignAce(UUID.randomUUID().toString(), callsignTarget, callsignChecked, type, allowed, proximity);

        CallsignAceRecord aceRecord = new CallsignAceRecord(ace.getId(), ace.getCallsignTarget(), ace.getCallsignChecked(), ace.getType().ordinal(), ace.isAllowed(), ace.getProximity());

        CallsignAceRecord existing = null;
        try {
            existing = callsignAceRepository.find(callsignTarget, callsignChecked);
        } catch (Exception e) {
            existing = null;
        }

        try {
            if (existing != null) {
                callsignAceRepository.update(aceRecord);
                changePublisherAccessor.publishCallsignACEUpdate(callsignTarget, "Update", ace);
            } else {
              ace = null;
              // update only
            }
        } catch (Exception e) {
            logger.error("Could not update ACE for callsign " + callsignTarget, e);
            ace = null;
        }
  
        return ace;
    }

    public boolean removeAce(User loggedInUser, String callsignTarget, String callsignChecked) {
        CallsignAceRecord existing = null;
        boolean ret = false;
        try {
            existing = callsignAceRepository.find(callsignTarget, callsignChecked);
        } catch (Exception e) {
            existing = null;
        }

        try {
            if (existing != null) {
                CallsignAce ace = new CallsignAce(existing.callsign_ace_id(), existing.callsign_target(), existing.callsign_checked(), CallsignType.values()[existing.callsign_checked_type()], existing.allowed(), existing.proximity());
                callsignAceRepository.delete(existing);
                changePublisherAccessor.publishCallsignACEUpdate(callsignTarget, "Delete", ace);
                ret = true;
            } 
        } catch (Exception e) {
            logger.error("Could not delete ACE for callsign " + callsignTarget, e);
        }
  
        return ret;
    }

    public void deleteAllData(User loggedInUser) {
        if ((loggedInUser == null) || (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            // no privs
            logger.error("No privileges to allow delete all");
            return;
        }

        callsignAceRepository.deleteAll();
    }
}
