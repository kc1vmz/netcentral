package netcentral.transceiver.agw.auth;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

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