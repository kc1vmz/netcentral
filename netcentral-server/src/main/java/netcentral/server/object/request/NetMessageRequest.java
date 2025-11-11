package netcentral.server.object.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record NetMessageRequest(@NotBlank String message) {
}
