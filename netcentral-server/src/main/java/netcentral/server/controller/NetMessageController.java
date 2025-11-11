package netcentral.server.controller;

import java.util.List;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.NetMessageAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.NetMessage;
import netcentral.server.object.User;

@Controller("/api/v1/netMessages") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class NetMessageController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private NetMessageAccessor netMessageAccessor;


    @Post
    public NetMessage create(HttpRequest<?> request,  @Body NetMessage obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return netMessageAccessor.create(loggedInUser, obj);
    }

    @Get 
    public List<NetMessage> getAll(HttpRequest<?> request, @Nullable @QueryValue String callsignNet, @Nullable @QueryValue String completedNetId) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return netMessageAccessor.getAll(loggedInUser, callsignNet, completedNetId);
    }

    @Get("/{id}")
    public NetMessage getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return netMessageAccessor.get(loggedInUser, id);
    }
}
