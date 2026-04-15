package netcentral.server.exceptions;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

public class TrackedStationExists extends HttpStatusException {
    public TrackedStationExists(HttpStatus status, String message) {
        super(status, message);
    }
}
