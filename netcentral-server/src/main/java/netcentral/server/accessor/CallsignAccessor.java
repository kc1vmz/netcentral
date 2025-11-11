package netcentral.server.accessor;

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
import netcentral.server.enums.UserRole;
import netcentral.server.object.Callsign;
import netcentral.server.object.User;
import netcentral.server.record.CallsignRecord;
import netcentral.server.repository.CallsignRepository;

@Singleton
public class CallsignAccessor {
    private static final Logger logger = LogManager.getLogger(CallsignAccessor.class);

    @Inject
    private CallsignRepository callsignRepository;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;


    public List<Callsign> getAll(User loggedInUser, String root) {
        logger.debug("getAll() called");
        List<CallsignRecord> recs = callsignRepository.findAll();
        List<Callsign> ret = new ArrayList<>();

        if (!recs.isEmpty()) {
            for (CallsignRecord rec : recs) {
                if ((root != null) && (!rec.callsign().startsWith(root))) {
                    // only take those with the optional root
                    continue;
                }
                ret.add(new Callsign(rec.callsign(), rec.name(), rec.country(), rec.state(), rec.license()));
            }
        }

        return ret;
    }

    public Callsign getByCallsign(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("Callsign not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }
        if (callsign.contains("-")) {
            // get a root 
            callsign = callsign.substring(0, callsign.indexOf("-"));
        }
        try {
            Optional<CallsignRecord> recOptional = callsignRepository.findById(callsign);
            if (recOptional.isEmpty()) {
                logger.debug("Callsign not found");
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not found");
            }
            CallsignRecord rec = recOptional.get();
            return new Callsign(rec.callsign(), rec.name(), rec.country(), rec.state(), rec.license());
        } catch (Exception e) {
            logger.debug("Callsign not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not found");
        }
    }

    public Callsign create(User loggedInUser, Callsign obj) {
        if (obj == null) {
            logger.debug("Callsign is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        try {
            Optional<CallsignRecord> existingOptional = callsignRepository.findById(obj.getCallsign());
            if (existingOptional.isPresent()) {
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign already exists");
            }
        } catch (Exception e) {
        }

        CallsignRecord src = new CallsignRecord(obj.getCallsign(), obj.getName(), obj.getCountry(), obj.getState(), obj.getLicense());
        CallsignRecord rec = callsignRepository.save(src);
        if (rec != null) {
            return obj;
        }
        logger.debug("Callsign not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not created");
    }

    public Callsign update(User loggedInUser, String id, Callsign obj) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign id not provided");
        }
        if (obj == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not provided");
        }
        if ((obj.getCallsign() == null) || (obj.getCallsign().isEmpty()) || (obj.getCallsign().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid callsign");
        }

        Optional<CallsignRecord> recOpt = callsignRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not found");
        }
        CallsignRecord updatedRec = new CallsignRecord(obj.getCallsign(), obj.getName(), obj.getCountry(), obj.getState(), obj.getLicense());
        callsignRepository.update(updatedRec);

        return obj;
    }

    public Callsign delete(User loggedInUser, String id) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign id not provided");
        }

        Optional<CallsignRecord> recOpt = callsignRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsign not found");
        }

        callsignRepository.delete(recOpt.get());
        
        return null;
    }

    public Map<String, List<Callsign>> getAllRoot(User loggedInUser) {

        Map<String, List<Callsign>> rootMap = getCallsignRootMap(loggedInUser); 

        return rootMap;
    }

    private Map<String, List<Callsign>> getCallsignRootMap(User loggedInUser) {
        Map<String, List<Callsign>> rootMap = new HashMap<>(); 
        List<Callsign> allCallsigns = getAll(loggedInUser, null);

        for (Callsign callsign: allCallsigns) {
            String callsignRoot = callsign.getCallsign();
            if (callsignRoot.contains("-")) {
                callsignRoot = callsignRoot.substring(0, callsignRoot.indexOf("-"));
            }
            List<Callsign> callsigns = rootMap.get(callsignRoot);
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

    public void identify(User loggedInUser, String callsign) {
        transceiverCommunicationAccessor.sendMessage(loggedInUser, (loggedInUser.getCallsign() == null) ? null : loggedInUser.getCallsign().getCallsign(), "WHO-15", callsign);
    }

    public void deleteAllData(User loggedInUser) {
        if ((loggedInUser == null) || (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            // no privs
            logger.error("No privileges to allow delete all");
            return;
        }

        callsignRepository.deleteAll();
    }
}
