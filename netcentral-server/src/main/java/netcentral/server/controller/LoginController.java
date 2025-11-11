package netcentral.server.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.UserAccessor;
import netcentral.server.object.request.LoginRequest;
import netcentral.server.object.User;

@Controller("/api/v1/login") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class LoginController {

    @Inject
    private UserAccessor userAccessor;

    @Post
    public User login(@Body LoginRequest obj) {
        return userAccessor.login(obj);
    }

}
