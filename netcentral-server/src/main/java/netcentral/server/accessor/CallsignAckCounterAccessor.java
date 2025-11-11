package netcentral.server.accessor;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.User;
import netcentral.server.record.CallsignAckCounterRecord;
import netcentral.server.repository.CallsignAckCounterRepository;

@Singleton
public class CallsignAckCounterAccessor {
    private static final Logger logger = LogManager.getLogger(CallsignAckCounterAccessor.class);
    private final Long MIN = 1L;
    private final Long MAX = 60416815L;

    @Inject
    private CallsignAckCounterRepository callsignAckCounterRepository;

    public String get(User loggedInUser, String callsignFrom, String callsignTo) {
        long counter = 0;
        if ((callsignFrom == null) || (callsignFrom == null)) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Required callsign not provided");
        }
        String id=callsignFrom+callsignTo;
        Optional<CallsignAckCounterRecord> recOpt = callsignAckCounterRepository.findById(id);
        boolean create = true;
        if ((recOpt == null) || !recOpt.isPresent()) {
            counter = 1;
            create = true;
        } else {
            counter = recOpt.get().counter()+1;
            create = false;
        }
        if (counter >= MAX) {
            counter = MIN;
        }

        CallsignAckCounterRecord src = new CallsignAckCounterRecord(id, callsignFrom, callsignTo, counter);
        if (create) {
            callsignAckCounterRepository.save(src);
        } else {
            callsignAckCounterRepository.update(src);
        }
        return toBase36(src.counter());
    }

    private String toBase36(Long value) {
        try {
            return Long.toString(value, 36).toUpperCase();
        } catch (NumberFormatException | NullPointerException ex) {
        }
        return null;
    }
}
