package netcentral.server.accessor;

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
import netcentral.server.enums.UserRole;
import netcentral.server.object.Net;
import netcentral.server.object.NetQuestion;
import netcentral.server.object.Participant;
import netcentral.server.object.User;
import netcentral.server.record.NetQuestionRecord;
import netcentral.server.repository.NetQuestionRepository;

@Singleton
public class NetQuestionAccessor {
    private static final Logger logger = LogManager.getLogger(NetQuestionAccessor.class);

    @Inject
    private NetQuestionRepository netQuestionRepository;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;

    public List<NetQuestion> getAll(User loggedInUser, String completedNetId) {
        List<NetQuestionRecord> recs = netQuestionRepository.findBycompleted_net_id(completedNetId);
        List<NetQuestion> ret = new ArrayList<>();

        if (!recs.isEmpty()) {
            for (NetQuestionRecord rec : recs) {
                ret.add(new NetQuestion(rec.net_question_id(), rec.completed_net_id(), rec.number(), rec.asked_time(), rec.active(), 
                                    rec.reminder_minutes(), rec.question_text(), rec.next_reminder_time()));
            }
        }

        Collections.sort(ret, new Comparator<NetQuestion>() {
            @Override
            public int compare(NetQuestion obj1, NetQuestion obj2) {
                return -1*obj1.getAskedTime().compareTo(obj2.getAskedTime());
            }
        });

        return ret;
    }

    public NetQuestion getByQuestionNumber(User loggedInUser, String completedNetId, int questionNumber) {
        NetQuestion ret = null;
        try {
            List<NetQuestionRecord> recs = netQuestionRepository.findBycompleted_net_id(completedNetId);

            if (!recs.isEmpty()) {
                for (NetQuestionRecord rec : recs) {
                    if (rec.number() == questionNumber) {
                        ret = new NetQuestion(rec.net_question_id(), rec.completed_net_id(), rec.number(), rec.asked_time(), rec.active(), 
                                        rec.reminder_minutes(), rec.question_text(), rec.next_reminder_time());
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }

        return ret;
    }

    public NetQuestion create(User loggedInUser, NetQuestion obj, Net net, List<Participant> netParticipants) {
        if (obj == null) {
            logger.debug("Net Question is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net Question not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        String id = UUID.randomUUID().toString();
        int number = getNextNumber(loggedInUser, obj.getCompletedNetId());
        obj.setNetQuestionId(id);
        obj.setNumber(number);

        if (obj.getAskedTime() == null) {
            obj.setAskedTime(ZonedDateTime.now());
        }
        ZonedDateTime nextReminderTime = obj.getAskedTime().plusMinutes(obj.getReminderMinutes());
        obj.setNextReminderTime(nextReminderTime);
        NetQuestionRecord src = new NetQuestionRecord(id, obj.getCompletedNetId(), number, obj.getAskedTime(), obj.getActive(), obj.getReminderMinutes(), obj.getQuestionText(), nextReminderTime);
        NetQuestionRecord rec = netQuestionRepository.save(src);
        if (rec != null) {
            changePublisherAccessor.publishNetQuestionUpdate(obj.getCompletedNetId(), "Create", obj);

            // tell the participants about the question
            try {
                if (netParticipants != null) {
                    String message = String.format("Net msg %d: %s", obj.getNumber(), obj.getQuestionText());
                    for (Participant netParticipant : netParticipants) {
                        transceiverCommunicationAccessor.sendMessage(loggedInUser, net.getCallsign(), netParticipant.getCallsign(), message);
                    }
                }
            } catch (Exception e) {
            }
            return obj;
        }
        logger.debug("Net Question not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net Question not created");
    }

    private int getNextNumber(User loggedInUser, String completedNetId) {
        int ret = 1;

        try {
            List<NetQuestion> all = getAll(loggedInUser, completedNetId);
            if (all != null) {
                ret = all.size()+1;
            }
        } catch (Exception e) {
        }

        return ret;
    }

    public void deleteAllData(User loggedInUser) {
        if ((loggedInUser == null) || (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            // no privs
            logger.error("No privileges to allow delete all");
            return;
        }

        netQuestionRepository.deleteAll();
    }

    public NetQuestion update(User loggedInUser, String id, NetQuestion obj) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net question id not provided");
        }
        if (obj == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net question not provided");
        }

        Optional<NetQuestionRecord> recOpt = netQuestionRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net question not found");
        }
        NetQuestionRecord rec = recOpt.get();

        // will only update next reminder time and active
        NetQuestionRecord updatedRec = new NetQuestionRecord(rec.net_question_id(), rec.completed_net_id(), rec.number(), rec.asked_time(), 
                                                obj.getActive(), rec.reminder_minutes(),
                                                rec.question_text(), obj.getNextReminderTime());

        netQuestionRepository.update(updatedRec);

        changePublisherAccessor.publishNetQuestionUpdate(rec.net_question_id(), "Update", obj);

        return obj;
    }

}
