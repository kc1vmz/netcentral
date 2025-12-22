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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.RadioStyle;
import netcentral.server.enums.UserRole;
import netcentral.server.object.Participant;
import netcentral.server.object.User;
import netcentral.server.record.ParticipantRecord;
import netcentral.server.repository.ParticipantRepository;

@Singleton
public class ParticipantAccessor {
    private static final Logger logger = LogManager.getLogger(ParticipantAccessor.class);

    @Inject
    private ParticipantRepository participantRepository;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private UserAccessor userAccessor;


    public List<Participant> getAll(User loggedInUser, String root) {
        logger.debug("getAll() called");
        List<Participant> ret = new ArrayList<>();

        try {
            List<ParticipantRecord> recs = participantRepository.findAll();

            if (!recs.isEmpty()) {
                for (ParticipantRecord rec : recs) {
                    if ((root != null) && (!rec.callsign().startsWith(root))) {
                        // only take those with the optional root
                        continue;
                    }
                    ret.add(new Participant(rec.callsign(), rec.status(), rec.vfreq(), null, null, null, ElectricalPowerType.UNKNOWN, ElectricalPowerType.UNKNOWN, RadioStyle.UNKNOWN, 0, null, null));
                }
            }
        } catch (Exception e) {
            logger.error("Exception caught getting participants", e);
        }

        return ret;
    }

    public Participant get(User loggedInUser, String id) {
        logger.debug(String.format("getId(%s) called", id));
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant id not provided");
        }
        Optional<ParticipantRecord> recOpt = participantRepository.findById(id);
        if (!recOpt.isPresent()) {
            logger.debug("Participant not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not found");
        }
        ParticipantRecord rec = recOpt.get();
        return new Participant(rec.callsign(), rec.status(), rec.vfreq(), null, null, null, ElectricalPowerType.UNKNOWN, ElectricalPowerType.UNKNOWN,RadioStyle.UNKNOWN, 0, null, null);
    }

    public Participant getByCallsign(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("Participant not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not provided");
        }
        try {
            Optional<ParticipantRecord> recOptional = participantRepository.findById(callsign);
            if (recOptional.isEmpty()) {
                logger.debug("Participant not found");
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not found");
            }
            ParticipantRecord rec = recOptional.get();
            return new Participant(rec.callsign(), rec.status(), rec.vfreq(), null, null, null, ElectricalPowerType.UNKNOWN, ElectricalPowerType.UNKNOWN, RadioStyle.UNKNOWN, 0, null, null);
        } catch (Exception e) {
            logger.debug("Participant not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not found");
        }
    }

    public Participant create(User loggedInUser, Participant obj) {
        if (obj == null) {
            logger.debug("Participant is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        try {
            Optional<ParticipantRecord> existingOptional = participantRepository.findById(obj.getCallsign());
            if (existingOptional.isPresent()) {
                logger.debug("Participant already exists");
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant already exists");
            }
        } catch (Exception e) {
        }

        ParticipantRecord src = new ParticipantRecord(obj.getCallsign(), 
                                                        (obj.getStatus() != null) ? obj.getStatus() : "",
                                                        (obj.getVoiceFrequency() != null) ? obj.getVoiceFrequency() : "");
        participantRepository.save(src);
        changePublisherAccessor.publishParticipantUpdate(obj.getCallsign(),"Create", obj);
        return obj;
    }

    public Participant update(User loggedInUser, String id, Participant obj) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant id not provided");
        }
        if (obj == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not provided");
        }
        if ((obj.getCallsign() == null) || (obj.getCallsign().isEmpty()) || (obj.getCallsign().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid callsign");
        }

        Optional<ParticipantRecord> recOpt = participantRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not found");
        }

        ParticipantRecord updatedRec = new ParticipantRecord(obj.getCallsign(), 
                                                        (obj.getStatus() != null) ? obj.getStatus() : "",
                                                        (obj.getVoiceFrequency() != null) ? obj.getVoiceFrequency() : "");
        participantRepository.update(updatedRec);
        changePublisherAccessor.publishParticipantUpdate(obj.getCallsign(),"Update", obj);
        return obj;
    }

    public Participant delete(User loggedInUser, String id) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant id not provided");
        }

        Optional<ParticipantRecord> recOpt = participantRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not found");
        }

        participantRepository.delete(recOpt.get());
        changePublisherAccessor.publishParticipantUpdate(recOpt.get().callsign(),"Delete", null);
        
        return null;
    }

    public Map<String, List<Participant>> getAllRoot(User loggedInUser) {

        Map<String, List<Participant>> rootMap = getCallsignRootMap(loggedInUser); 

        return rootMap;
    }

    private Map<String, List<Participant>> getCallsignRootMap(User loggedInUser) {
        Map<String, List<Participant>> rootMap = new HashMap<>(); 
        List<Participant> allCallsigns = getAll(loggedInUser, null);

        for (Participant callsign: allCallsigns) {
            String callsignRoot = callsign.getCallsign();
            if (callsignRoot.contains("-")) {
                callsignRoot = callsignRoot.substring(0, callsignRoot.indexOf("-"));
            }
            List<Participant> callsigns = rootMap.get(callsignRoot);
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

    public Participant deleteAll(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        if ((!loggedInUser.getRole().equals(UserRole.SYSTEM)) && (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            logger.debug("Insufficient role");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Insufficient role");
        }

        participantRepository.deleteAll();
        return null;
    }
}
