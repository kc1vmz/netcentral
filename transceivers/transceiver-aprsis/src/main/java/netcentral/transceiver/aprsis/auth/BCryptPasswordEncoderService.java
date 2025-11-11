package netcentral.transceiver.aprsis.auth;

import io.micronaut.core.annotation.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.constraints.NotBlank;

public class BCryptPasswordEncoderService implements PasswordEncoder {

    private BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

    public String encode(@NotBlank @NonNull CharSequence rawPassword) {
       return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(@NotBlank @NonNull CharSequence rawPassword,
                           @NotBlank @NonNull String encodedPassword) {
        BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();
        return delegate.matches(rawPassword, encodedPassword);
    }
}