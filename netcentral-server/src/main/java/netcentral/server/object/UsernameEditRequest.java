package netcentral.server.object;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record UsernameEditRequest(@NotBlank String username, String firstName, String lastName, String callsign) {
}
