package netcentral.transceiver.aprsis.auth;

import java.util.Optional;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.Cookies;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.transceiver.aprsis.enums.UserRole;
import netcentral.transceiver.aprsis.object.User;

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
            }
        }
        return ret;
    }

    public String getTokenFromSession(HttpRequest<?> request) {
        String ret = null;
        if (request != null) {
            Cookies cookies = request.getCookies();
            if (cookies != null) {
                Optional<Cookie> sessionCookieOpt = cookies.findCookie("SessionID");
                if (sessionCookieOpt.isPresent()) {
                    Cookie cookie = sessionCookieOpt.get();
                    if (cookie != null) {
                        ret = cookie.getValue();
                        // cookie.getMaxAge()
                    }
                }
            }
            if (ret == null) {
                // look to see if we have a header for this
                HttpHeaders headers = request.getHeaders();
                if (headers != null) {
                    ret = headers.get("SessionID");
                }
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
