package netcentral.server.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.UserAccessor;
import netcentral.server.object.User;

@Controller("/api/v1/logout") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class LogoutController {

    @Inject
    private UserAccessor userAccessor;

    @Post
    public User logout(@Body User obj) {
       return userAccessor.logout(obj);
    }

}
