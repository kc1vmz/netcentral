package netcentral.server.accessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.common.RegisteredTransceiver;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.User;
import netcentral.server.record.RegisteredTransceiverRecord;
import netcentral.server.repository.RegisteredTransceiverRepository;

@Singleton
public class RegisteredTransceiverAccessor {
    private static final Logger logger = LogManager.getLogger(RegisteredTransceiverAccessor.class);

    @Inject
    private RegisteredTransceiverRepository registeredTransceiverRepository;
    @Inject
    private ActivityAccessor activityAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;

    public List<RegisteredTransceiver> getAll(User loggedInUser) {
        List<RegisteredTransceiverRecord> recs = registeredTransceiverRepository.findAll();
        List<RegisteredTransceiver> ret = new ArrayList<>();

        if ((recs != null) && (!recs.isEmpty())) {
            for (RegisteredTransceiverRecord rec : recs) {
                ret.add(new RegisteredTransceiver(rec.registered_transceiver_id(), rec.name(), rec.description(), rec.fqd_name(), rec.type(), rec.port(), rec.enabled_receive(), rec.enabled_transmit()));
            }
        }

        return ret;
    }

    public RegisteredTransceiver get(User loggedInUser, String id) {
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver id not provided");
        }
        Optional<RegisteredTransceiverRecord> recOpt = registeredTransceiverRepository.findById(id);
        if ((recOpt == null) || !recOpt.isPresent()) {
            logger.debug("RegisteredTransceiver not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver not found");
        }
        RegisteredTransceiverRecord rec = recOpt.get();
        return new RegisteredTransceiver(rec.registered_transceiver_id(), rec.name(), rec.description(), rec.fqd_name(), rec.type(), rec.port(), rec.enabled_receive(), rec.enabled_transmit());
    }

    public RegisteredTransceiver getByName(User loggedInUser, String name) {
        if (name == null) {
            logger.debug("null name");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver name not provided");
        }
        List<RegisteredTransceiverRecord> records = registeredTransceiverRepository.findByname(name);
        if ((records == null) || (records.isEmpty())) {
            logger.debug("RegisteredTransceiver not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver not found");
        }
        RegisteredTransceiverRecord rec = records.get(0);  // only ever one
        return new RegisteredTransceiver(rec.registered_transceiver_id(), rec.name(), rec.description(), rec.fqd_name(), rec.type(), rec.port(), rec.enabled_receive(), rec.enabled_transmit());
    }

    public RegisteredTransceiver create(User loggedInUser, RegisteredTransceiver obj) {
        if (obj == null) {
            logger.debug("RegisteredTransceiver is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        try {
            Optional<RegisteredTransceiverRecord> existingOpt = registeredTransceiverRepository.findById(obj.getId());
            if ((existingOpt != null) && (existingOpt.get() != null)) {
                logger.debug("RegisteredTransceiver already exists");
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver already exists");
            }
        } catch (Exception e) {
        }

        String id = UUID.randomUUID().toString();
        RegisteredTransceiverRecord src = new RegisteredTransceiverRecord(id, obj.getName(), 
                                            (obj.getDescription() != null) ? obj.getDescription() : "",  
                                            obj.getFqdName(), obj.getType(), obj.getPort(), false, false);  // do not auto-enable
        RegisteredTransceiverRecord rec = registeredTransceiverRepository.save(src);
        if (rec != null) {
            obj.setId(id);
            try {
                activityAccessor.create(loggedInUser, String.format("Transceiver %s registered",  obj.getName()));
            } catch (Exception e) {
            }

            changePublisherAccessor.publishTransceiverUpdate(id, "Create");

            return obj;
        }

        logger.debug("RegisteredTransceiver not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver not created");
    }

    public RegisteredTransceiver update(User loggedInUser, String id, RegisteredTransceiver obj) {
        if (obj == null) {
            logger.debug("RegisteredTransceiver is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver not provided");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver id not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (!id.equals(obj.getId())) {
            logger.debug("RegisteredTransceiver id does not match");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "RegisteredTransceiver id does not match");
        }

        try {
            Optional<RegisteredTransceiverRecord> existingOpt = registeredTransceiverRepository.findById(obj.getId());
            if ((existingOpt == null) || (!existingOpt.isPresent())) {
                logger.debug("RegisteredTransceiver does not exist");
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver does not exist");
            }
        } catch (Exception e) {
        }

        RegisteredTransceiverRecord src = new RegisteredTransceiverRecord(obj.getId(), obj.getName(), 
                                            (obj.getDescription() != null) ? obj.getDescription() : "",  
                                            obj.getFqdName(), obj.getType(), obj.getPort(), obj.isEnabledReceive(), obj.isEnabledTransmit());
        registeredTransceiverRepository.update(src);
        try {
            activityAccessor.create(loggedInUser, String.format("Transceiver %s updated",  obj.getName()));
        } catch (Exception e) {
        }
        changePublisherAccessor.publishTransceiverUpdate(obj.getId(), "Update");
        return get(loggedInUser, id);
    }

    public RegisteredTransceiver delete(User loggedInUser, String id) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver id not provided");
        }

        Optional<RegisteredTransceiverRecord> recOpt = registeredTransceiverRepository.findById(id);
        if ((recOpt == null) || !recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "RegisteredTransceiver not found");
        }
        try {
            activityAccessor.create(loggedInUser, String.format("Transceiver %s deleted",  recOpt.get().name()));
        } catch (Exception e) {
        }

        registeredTransceiverRepository.delete(recOpt.get());
        changePublisherAccessor.publishTransceiverUpdate(id, "Delete");
        
        return null;
    }
}
