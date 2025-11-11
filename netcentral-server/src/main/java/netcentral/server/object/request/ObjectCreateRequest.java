package netcentral.server.object.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record ObjectCreateRequest(int type, @NotBlank String callsign, String description, String lat, String lon) {
}
