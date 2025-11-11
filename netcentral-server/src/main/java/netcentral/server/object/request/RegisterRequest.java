package netcentral.server.object.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record RegisterRequest(@NotBlank String username, @NotBlank String password, @NotBlank String password2, String callsign, String firstName, String lastName) {
}
