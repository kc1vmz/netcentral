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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import netcentral.server.enums.UserRole;
import netcentral.server.object.CompletedParticipant;
import netcentral.server.object.Net;
import netcentral.server.object.Participant;
import netcentral.server.object.User;
import netcentral.server.record.CompletedParticipantRecord;
import netcentral.server.repository.CompletedParticipantRepository;

@Singleton
public class CompletedParticipantAccessor {
    private static final Logger logger = LogManager.getLogger(CompletedParticipantAccessor.class);

    @Inject
    private CompletedParticipantRepository completedParticipantRepository;
    @Inject
    private UserAccessor userAccessor;


    public List<CompletedParticipant> getAll(User loggedInUser, String root) {
        List<CompletedParticipantRecord> recs = completedParticipantRepository.findAll();
        List<CompletedParticipant> ret = new ArrayList<>();

        if (!recs.isEmpty()) {
            for (CompletedParticipantRecord rec : recs) {
                if ((root != null) && (!rec.callsign().startsWith(root))) {
                    // only take those with the optional root
                    continue;
                }
                ret.add(new CompletedParticipant(rec.callsign(), rec.completed_net_id(), rec.completed_participant_id(), rec.start_time(), rec.end_time(), 
                                            ElectricalPowerType.values()[rec.electrical_power_type()], 
                                            RadioStyle.values()[rec.radio_style()], 
                                            rec.transmit_power(), rec.tactical_callsign()));
            }
        }

        return ret;
    }

    public List<CompletedParticipant> getAllByCompletedNetId(User loggedInUser, String completedNetId) {
        List<CompletedParticipantRecord> recs = completedParticipantRepository.findBycompleted_net_id(completedNetId);
        List<CompletedParticipant> ret = new ArrayList<>();

        if ((recs != null) && (!recs.isEmpty())) {
            for (CompletedParticipantRecord rec : recs) {
                ret.add(new CompletedParticipant(rec.callsign(), rec.completed_net_id(), rec.completed_participant_id(), rec.start_time(), rec.end_time(),
                                            ElectricalPowerType.values()[rec.electrical_power_type()], 
                                            RadioStyle.values()[rec.radio_style()], 
                                            rec.transmit_power(), rec.tactical_callsign()));
            }
        }

        return ret;
    }

    public CompletedParticipant get(User loggedInUser, String id) {
        logger.debug(String.format("getId(%s) called", id));
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant id not provided");
        }
        Optional<CompletedParticipantRecord> recOpt = completedParticipantRepository.findById(id);
        if (!recOpt.isPresent()) {
            logger.debug("Participant not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not found");
        }
        CompletedParticipantRecord rec = recOpt.get();
        return new CompletedParticipant(rec.callsign(), rec.completed_net_id(), rec.completed_participant_id(), rec.start_time(), rec.end_time(), 
                                            ElectricalPowerType.values()[rec.electrical_power_type()], 
                                            RadioStyle.values()[rec.radio_style()], 
                                            rec.transmit_power(), rec.tactical_callsign());
    }

    public List<CompletedParticipant> getByCallsign(User loggedInUser, String callsign) {
        List<CompletedParticipant> ret = new ArrayList<>();
        if (callsign == null) {
            logger.debug("Callsign not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not provided");
        }
        try {
            List<CompletedParticipantRecord> recList = completedParticipantRepository.findBycallsign(callsign);
            if ((recList == null) || (recList.isEmpty())) {
                logger.debug("Callsign not found");
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not found");
            }
            for (CompletedParticipantRecord rec : recList) {
                ret.add(new CompletedParticipant(rec.callsign(), rec.completed_net_id(), rec.completed_participant_id(), rec.start_time(), rec.end_time(),
                                            ElectricalPowerType.values()[rec.electrical_power_type()], 
                                            RadioStyle.values()[rec.radio_style()], 
                                            rec.transmit_power(), rec.tactical_callsign()));
            }
            return ret;
        } catch (Exception e) {
            logger.debug("Callsign not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not found");
        }
    }

    public CompletedParticipant create(User loggedInUser, Net net, Participant obj) {
        if (obj == null) {
            logger.debug("Participant is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        String id = UUID.randomUUID().toString();

        CompletedParticipantRecord src = new CompletedParticipantRecord(id, net.getCompletedNetId(), obj.getCallsign(),obj.getStartTime(), ZonedDateTime.now(),
                                            obj.getElectricalPowerType().ordinal(), 
                                            obj.getRadioStyle().ordinal(), 
                                            obj.getTransmitPower(), obj.getTacticalCallsign());
        CompletedParticipantRecord rec = completedParticipantRepository.save(src);
        if (rec != null) {
            return get(loggedInUser, id);
        }
        logger.debug("Completed participant not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Completed participant not created");
    }


    public Map<String, List<CompletedParticipant>> getAllRoot(User loggedInUser) {

        Map<String, List<CompletedParticipant>> rootMap = getCallsignRootMap(loggedInUser); 

        return rootMap;
    }

    private Map<String, List<CompletedParticipant>> getCallsignRootMap(User loggedInUser) {
        Map<String, List<CompletedParticipant>> rootMap = new HashMap<>(); 
        List<CompletedParticipant> allCallsigns = getAll(loggedInUser, null);

        for (CompletedParticipant callsign: allCallsigns) {
            String callsignRoot = callsign.getCallsign();
            if (callsignRoot.contains("-")) {
                callsignRoot = callsignRoot.substring(0, callsignRoot.indexOf("-"));
            }
            List<CompletedParticipant> callsigns = rootMap.get(callsignRoot);
            if (callsigns != null) {
                callsigns.add(callsign);
            } else {
                callsigns = new ArrayList<>();
                callsigns.add(callsign);
                rootMap.put(callsignRoot, callsigns);
            }
        }
        return rootMap;
    }

    public CompletedParticipant deleteAll(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        if ((!loggedInUser.getRole().equals(UserRole.SYSTEM)) && (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            logger.debug("Insufficient role");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Insufficient role");
        }

        completedParticipantRepository.deleteAll();
        return null;
    }
}
