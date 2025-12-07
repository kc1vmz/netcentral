package netcentral.server.accessor;

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
import netcentral.server.enums.UserRole;
import netcentral.server.object.CompletedExpectedParticipant;
import netcentral.server.object.Net;
import netcentral.server.object.User;
import netcentral.server.record.CompletedExpectedParticipantRecord;
import netcentral.server.repository.CompletedExpectedParticipantRepository;

@Singleton
public class CompletedExpectedParticipantAccessor {
    private static final Logger logger = LogManager.getLogger(CompletedExpectedParticipantAccessor.class);

    @Inject
    private CompletedExpectedParticipantRepository completedExpectedParticipantRepository;
    @Inject
    private UserAccessor userAccessor;


    public List<CompletedExpectedParticipant> getAll(User loggedInUser, String root) {
        List<CompletedExpectedParticipantRecord> recs = completedExpectedParticipantRepository.findAll();
        List<CompletedExpectedParticipant> ret = new ArrayList<>();

        if (!recs.isEmpty()) {
            for (CompletedExpectedParticipantRecord rec : recs) {
                if ((root != null) && (!rec.callsign().startsWith(root))) {
                    // only take those with the optional root
                    continue;
                }
                ret.add(new CompletedExpectedParticipant(rec.callsign(), rec.completed_net_id(), rec.completed_expected_participant_id()));
            }
        }

        return ret;
    }

    public List<CompletedExpectedParticipant> getAllByCompletedNetId(User loggedInUser, String completedNetId) {
        List<CompletedExpectedParticipantRecord> recs = completedExpectedParticipantRepository.findBycompleted_net_id(completedNetId);
        List<CompletedExpectedParticipant> ret = new ArrayList<>();

        if ((recs != null) && (!recs.isEmpty())) {
            for (CompletedExpectedParticipantRecord rec : recs) {
                ret.add(new CompletedExpectedParticipant(rec.callsign(), rec.completed_net_id(), rec.completed_expected_participant_id()));
            }
        }

        return ret;
    }

    public CompletedExpectedParticipant get(User loggedInUser, String id) {
        logger.debug(String.format("getId(%s) called", id));
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant id not provided");
        }
        Optional<CompletedExpectedParticipantRecord> recOpt = completedExpectedParticipantRepository.findById(id);
        if (!recOpt.isPresent()) {
            logger.debug("Participant not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not found");
        }
        CompletedExpectedParticipantRecord rec = recOpt.get();
        return new CompletedExpectedParticipant(rec.callsign(), rec.completed_net_id(), rec.completed_expected_participant_id());
    }

    public List<CompletedExpectedParticipant> getByCallsign(User loggedInUser, String callsign) {
        List<CompletedExpectedParticipant> ret = new ArrayList<>();
        if (callsign == null) {
            logger.debug("Callsign not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not provided");
        }
        try {
            List<CompletedExpectedParticipantRecord> recList = completedExpectedParticipantRepository.findBycallsign(callsign);
            if ((recList == null) || (recList.isEmpty())) {
                logger.debug("Callsign not found");
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not found");
            }
            for (CompletedExpectedParticipantRecord rec : recList) {
                ret.add(new CompletedExpectedParticipant(rec.callsign(), rec.completed_net_id(), rec.completed_expected_participant_id()));
            }
            return ret;
        } catch (Exception e) {
            logger.debug("Callsign not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not found");
        }
    }

    public CompletedExpectedParticipant create(User loggedInUser, Net net, CompletedExpectedParticipant obj) {
        if (obj == null) {
            logger.debug("Participant is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        String id = UUID.randomUUID().toString();

        CompletedExpectedParticipantRecord src = new CompletedExpectedParticipantRecord(id, net.getCompletedNetId(), obj.getCallsign());
        CompletedExpectedParticipantRecord rec = completedExpectedParticipantRepository.save(src);
        if (rec != null) {
            return get(loggedInUser, id);
        }
        logger.debug("Completed expected participant not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Completed expected participant not created");
    }


    public Map<String, List<CompletedExpectedParticipant>> getAllRoot(User loggedInUser) {

        Map<String, List<CompletedExpectedParticipant>> rootMap = getCallsignRootMap(loggedInUser); 

        return rootMap;
    }

    private Map<String, List<CompletedExpectedParticipant>> getCallsignRootMap(User loggedInUser) {
        Map<String, List<CompletedExpectedParticipant>> rootMap = new HashMap<>(); 
        List<CompletedExpectedParticipant> allCallsigns = getAll(loggedInUser, null);

        for (CompletedExpectedParticipant callsign: allCallsigns) {
            String callsignRoot = callsign.getCallsign();
            if (callsignRoot.contains("-")) {
                callsignRoot = callsignRoot.substring(0, callsignRoot.indexOf("-"));
            }
            List<CompletedExpectedParticipant> callsigns = rootMap.get(callsignRoot);
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

    public CompletedExpectedParticipant deleteAll(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        if ((!loggedInUser.getRole().equals(UserRole.SYSTEM)) && (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            logger.debug("Insufficient role");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Insufficient role");
        }

        completedExpectedParticipantRepository.deleteAll();
        return null;
    }
}
