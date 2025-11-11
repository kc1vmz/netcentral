package netcentral.server.object.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record PasswordEditRequest(@NotBlank String password, @NotBlank String password2) {
}
