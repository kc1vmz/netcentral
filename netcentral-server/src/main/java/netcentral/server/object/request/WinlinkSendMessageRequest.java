package netcentral.server.object.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record WinlinkSendMessageRequest(@NotBlank String callsign, @NotBlank String password, @NotBlank String recipient, @NotBlank String subject, @NotBlank String message) {
}
