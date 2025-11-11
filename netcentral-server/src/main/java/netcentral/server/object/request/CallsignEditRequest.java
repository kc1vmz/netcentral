package netcentral.server.object.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record CallsignEditRequest(@NotBlank String callsign, String name, String country, String state, String license) {
}
