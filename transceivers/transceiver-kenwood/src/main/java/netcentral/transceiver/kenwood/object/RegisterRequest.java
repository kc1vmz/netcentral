package netcentral.transceiver.kenwood.object;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record RegisterRequest(@NotBlank String fqdName, @NotBlank String port, @NotBlank String username, @NotBlank String password) {
}
