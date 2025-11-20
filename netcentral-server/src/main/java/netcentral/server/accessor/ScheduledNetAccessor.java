package netcentral.server.accessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ScheduledNetType;
import netcentral.server.enums.UserRole;
import netcentral.server.object.ScheduledNet;
import netcentral.server.object.User;
import netcentral.server.record.ScheduledNetRecord;
import netcentral.server.repository.ScheduledNetRepository;

@Singleton
public class ScheduledNetAccessor {
    private static final Logger logger = LogManager.getLogger(ScheduledNetAccessor.class);

    @Inject
    private ScheduledNetRepository scheduledNetRepository;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private UserAccessor userAccessor;

    public List<ScheduledNet> getAll(User loggedInUser, String root) {
        List<ScheduledNetRecord> recs = scheduledNetRepository.findAll();
        List<ScheduledNet> ret = new ArrayList<>();

        if ((recs != null) && (!recs.isEmpty())) {
            for (ScheduledNetRecord rec : recs) {
                if ((root != null) && (!rec.callsign().startsWith(root))) {
                    // only take those with the optional root
                    continue;
                }
                ret.add(new ScheduledNet(rec.callsign(), rec.name(), rec.description(), ScheduledNetType.values()[rec.type()], rec.vfreq(), rec.lat(), rec.lon(), rec.announce(), rec.creator_name(),
                    rec.day_start(), rec.time_start(), rec.duration(), rec.last_start_time(), rec.next_start_time(), rec.checkin_reminder()));
            }
        }

        return ret;
    }

    public ScheduledNet get(User loggedInUser, String id) {
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Scheduled Net id not provided");
        }
        try {
            Optional<ScheduledNetRecord> recOpt = scheduledNetRepository.findById(id);
            if ((recOpt == null) || !recOpt.isPresent()) {
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Scheduled Net not found");
            }
            ScheduledNetRecord rec = recOpt.get();
            return new ScheduledNet(rec.callsign(), rec.name(), rec.description(), ScheduledNetType.values()[rec.type()], rec.vfreq(), rec.lat(), rec.lon(), rec.announce(), rec.creator_name(),
                    rec.day_start(), rec.time_start(), rec.duration(), rec.last_start_time(), rec.next_start_time(), rec.checkin_reminder());

        } catch (Exception e) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Scheduled Net not found");
        }
    }


    public ScheduledNet create(User loggedInUser, ScheduledNet obj) {
        if (obj == null) {
            logger.debug("Net is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Scheduled Net Net not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        try {
            Optional<ScheduledNetRecord> existingOpt = scheduledNetRepository.findById(obj.getCallsign());
            if ((existingOpt != null) && (existingOpt.get() != null)) {
                logger.debug("Scheduled Net already exists");
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Scheduled Net already exists");
            }
        } catch (Exception e) {
        }

        ScheduledNetRecord src = new ScheduledNetRecord(obj.getCallsign(), 
                                            (obj.getVoiceFrequency() != null) ? obj.getVoiceFrequency() : "",
                                            obj.getName(), 
                                            (obj.getDescription() != null) ? obj.getDescription() : "",  
                                            obj.getLat(), obj.getLon(), obj.isAnnounce(),
                                            obj.getCreatorName(),
                                            obj.getType().ordinal(),
                                            obj.getDayStart(),
                                            obj.getTimeStart(), 
                                            obj.getDuration(), 
                                            obj.getLastStartTime(), obj.getNextStartTime(), obj.isCheckinReminder());
        ScheduledNetRecord rec = scheduledNetRepository.save(src);
        if (rec != null) {
            changePublisherAccessor.publishScheduledNetUpdate(obj.getCallsign(), "Create", obj);
            return obj;
        }

        logger.debug("Scheduled Net not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Scheduled Net not created");
    }

    public ScheduledNet update(User loggedInUser, String id, ScheduledNet obj) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Scheduled Net id not provided");
        }
        if (obj == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Scheduled Net not provided");
        }
        if ((obj.getCallsign() != null) && (!id.equals(obj.getCallsign()))){
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsigns to not match");
        }
        if ((obj.getCallsign() == null) || (obj.getCallsign().isEmpty()) || (obj.getCallsign().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid callsign");
        }
        if ((obj.getName() == null) || (obj.getName().isEmpty()) || (obj.getName().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid name");
        }

        Optional<ScheduledNetRecord> recOpt = scheduledNetRepository.findById(id);
        if ((recOpt == null) || !recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Scheduled Net not found");
        }
        ScheduledNetRecord rec = recOpt.get();

        ScheduledNetRecord updatedRec = new ScheduledNetRecord(rec.callsign(), 
                                            (obj.getVoiceFrequency() != null) ? obj.getVoiceFrequency() : "",
                                            obj.getName(), 
                                            (obj.getDescription() != null) ? obj.getDescription() : "",  
                                            obj.getLat(), obj.getLon(), obj.isAnnounce(),
                                            obj.getCreatorName(),
                                            obj.getType().ordinal(),
                                            obj.getDayStart(),
                                            obj.getTimeStart(), 
                                            obj.getDuration(), 
                                            obj.getLastStartTime(), obj.getNextStartTime(), obj.isCheckinReminder());

        scheduledNetRepository.update(updatedRec);
        obj = get(loggedInUser, id);
        changePublisherAccessor.publishScheduledNetUpdate(obj.getCallsign(), "Update", obj);

        return obj;

    }

    public ScheduledNet delete(User loggedInUser, String id) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Scheduled Net id not provided");
        }

        ScheduledNet net = get(loggedInUser, id);

        Optional<ScheduledNetRecord> recOpt = scheduledNetRepository.findById(id);
        if ((recOpt == null) || !recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Scheduled Net not found");
        }

        ScheduledNetRecord rec = recOpt.get();
        scheduledNetRepository.delete(rec);
        changePublisherAccessor.publishScheduledNetUpdate(rec.callsign(), "Delete", net);

        return null;
    }

    public ScheduledNet deleteAll(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        if ((!loggedInUser.getRole().equals(UserRole.SYSTEM)) && (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            logger.debug("Insufficient role");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Insufficient role");
        }

        scheduledNetRepository.deleteAll();
        return null;
    }
}
