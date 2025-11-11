package netcentral.transceiver.aprsis.object;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record SendMessageRequest(@NotBlank String callsign, @NotBlank String to_callsign, @NotBlank String message) {
}
