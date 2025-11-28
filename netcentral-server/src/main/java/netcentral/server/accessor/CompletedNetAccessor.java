package netcentral.server.accessor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.UserRole;
import netcentral.server.object.CompletedNet;
import netcentral.server.object.Net;
import netcentral.server.object.User;
import netcentral.server.record.CompletedNetRecord;
import netcentral.server.repository.CompletedNetRepository;

@Singleton
public class CompletedNetAccessor {
    private static final Logger logger = LogManager.getLogger(CompletedNetAccessor.class);

    @Inject
    private CompletedNetRepository completedNetRepository;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private UserAccessor userAccessor;

    public List<CompletedNet> getAll(User loggedInUser, String root) {
        List<CompletedNetRecord> recs = completedNetRepository.findAll();
        List<CompletedNet> ret = new ArrayList<>();

        if (!recs.isEmpty()) {
            for (CompletedNetRecord rec : recs) {
                if ((root != null) && (!rec.callsign().startsWith(root))) {
                    // only take those with the optional root
                    continue;
                }
                ret.add(new CompletedNet(rec.callsign(), rec.name(), rec.description(), rec.vfreq(), rec.start_time(), rec.end_time(), rec.completed_net_id(), rec.creator_name(), rec.checkin_message()));
            }
        }

        Collections.sort(ret, new Comparator<CompletedNet>() {
            @Override
            public int compare(CompletedNet obj1, CompletedNet obj2) {
                return -1*obj1.getStartTime().compareTo(obj2.getStartTime());
            }
        });

        return ret;
    }

    public CompletedNet get(User loggedInUser, String id) {
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net id not provided");
        }
        Optional<CompletedNetRecord> recOpt = completedNetRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not found");
        }
        CompletedNetRecord rec = recOpt.get();
        return new CompletedNet(rec.callsign(), rec.name(), rec.description(), rec.vfreq(), rec.start_time(), rec.end_time(), rec.completed_net_id(), rec.creator_name(), rec.checkin_message());
    }


    public CompletedNet create(User loggedInUser, Net obj) {
        if (obj == null) {
            logger.debug("Completed net is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        CompletedNetRecord src = new CompletedNetRecord(obj.getCompletedNetId(), 
                                            obj.getCallsign(), 
                                            (obj.getVoiceFrequency() != null) ? obj.getVoiceFrequency() : "",
                                            obj.getName(), 
                                            (obj.getDescription() != null) ? obj.getDescription() : "",  
                                            obj.getStartTime(), ZonedDateTime.now(),
                                            obj.getCreatorName(), obj.getCheckinMessage());
        CompletedNetRecord rec = completedNetRepository.save(src);
        changePublisherAccessor.publishCompletedNetUpdate(obj.getCompletedNetId(), "Create");
        return get(loggedInUser, rec.completed_net_id());
    }

    public CompletedNet deleteAll(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        if ((!loggedInUser.getRole().equals(UserRole.SYSTEM)) && (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            logger.debug("Insufficient role");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Insufficient role");
        }

        completedNetRepository.deleteAll();
        return null;
    }

}
