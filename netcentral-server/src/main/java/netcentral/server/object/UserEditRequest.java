package netcentral.server.object;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record UserEditRequest(@NotBlank String callsign, @NotBlank String emailAddress, String firstName, String lastName) {
}
