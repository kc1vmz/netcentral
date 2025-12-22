package netcentral.server.auth;

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

import java.util.Optional;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.Cookies;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.UserRole;
import netcentral.server.object.User;

@Singleton
public class SessionAccessor {

    @Inject
    SessionCacheAccessor sessionCacheAccessor;

    public User getUserFromToken(String token) {
        User ret = null;
        if (token != null) {
            String userId = sessionCacheAccessor.find(token);
            if (userId != null) {
                ret = new User();
                ret.setId(userId);
                ret.setAccessToken(token);

                // get callsign in here
            }
        }
        return ret;
    }

    public String getTokenFromSession(HttpRequest<?> request) {
        String ret = null;
        if (request != null) {
            Cookies cookies = request.getCookies();
            Optional<Cookie> sessionCookieOpt = cookies.findCookie("SessionID");
            if (sessionCookieOpt.isPresent()) {
                Cookie cookie = sessionCookieOpt.get();
                if (cookie != null) {
                    ret = cookie.getValue();
                    // cookie.getMaxAge()
                }
            }
            if (ret == null) {
                // look to see if we have a header for this
                HttpHeaders headers = request.getHeaders();
                ret = headers.get("SessionID");
            }
        }
        return ret;
    }

    public User getSystemUser() {
        User systemUser = new User();
        systemUser.setRole(UserRole.SYSTEM);
        return systemUser;
    }
}
