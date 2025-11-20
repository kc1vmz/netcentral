package netcentral.server.accessor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import netcentral.server.object.NetMessage;
import netcentral.server.object.User;
import netcentral.server.record.NetMessageRecord;
import netcentral.server.repository.NetMessageRepository;

@Singleton
public class NetMessageAccessor {
    private static final Logger logger = LogManager.getLogger(NetMessageAccessor.class);

    @Inject
    private NetAccessor netAccessor;
    @Inject
    private NetMessageRepository netMessageRepository;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private UserAccessor userAccessor;

    public List<NetMessage> getAll(User loggedInUser, String callsignNet, String completedNetId) {
        List<NetMessage> ret = new ArrayList<>();

        if (completedNetId == null) {
            // get the active net
            Net net = netAccessor.get(loggedInUser, callsignNet);
            if (net == null) {
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net callsign not found");
            }
            completedNetId = net.getCompletedNetId();
        }

        List<NetMessageRecord> recs = netMessageRepository.findBycompleted_net_id(completedNetId);
        if ((recs != null) && (!recs.isEmpty())) {
            for (NetMessageRecord rec : recs) {
                ret.add(new NetMessage(rec.net_message_id(), rec.completed_net_id(), rec.callsign_from(), rec.message(), rec.received_time(), rec.recipient()));
            }
        }

        return ret;
    }

    public NetMessage get(User loggedInUser, String id) {
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NetMessage id not provided");
        }
        Optional<NetMessageRecord> recOpt = netMessageRepository.findById(id);
        if (!recOpt.isPresent()) {
            logger.debug("NetMessage not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NetMessage not found");
        }
        NetMessageRecord rec = recOpt.get();
        return new NetMessage(rec.net_message_id(), rec.completed_net_id(), rec.callsign_from(), rec.message(), rec.received_time(), rec.recipient());
    }


    public NetMessage create(User loggedInUser, NetMessage obj) {
        if (obj == null) {
            logger.debug("NetMessage is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NetMessage not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        String messageId = UUID.randomUUID().toString();
        obj.setId(messageId);
        NetMessageRecord src = new NetMessageRecord(messageId, obj.getCompletedNetId(), obj.getCallsignFrom(), obj.getMessage(), ZonedDateTime.now(), obj.getRecipient());
        netMessageRepository.save(src);
        changePublisherAccessor.publishNetMessageUpdate(obj.getCompletedNetId(), "Create", obj);
        return obj;
    }

    public Net deleteAll(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        if ((!loggedInUser.getRole().equals(UserRole.SYSTEM)) && (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            logger.debug("Insufficient role");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Insufficient role");
        }

        netMessageRepository.deleteAll();
        return null;
    }
}
