package netcentral.server.controller;

import java.util.List;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.ChangePublisherAccessor;
import netcentral.server.accessor.ScheduledNetAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.ScheduledNet;
import netcentral.server.object.User;
import netcentral.server.object.request.ScheduledNetCreateRequestExternal;

@Controller("/api/v1/scheduledNets") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class ScheduledNetController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private ScheduledNetAccessor scheduledNetAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;


    @Post
    public ScheduledNet create(HttpRequest<?> request,  @Body ScheduledNet obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return scheduledNetAccessor.create(loggedInUser, obj);
    }

    @Post("/requests")
    public ScheduledNet createFromRequest(HttpRequest<?> request,  @Body ScheduledNetCreateRequestExternal obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return scheduledNetAccessor.create(loggedInUser, obj.getScheduledNet());
    }

    @Get 
    public List<ScheduledNet> getAll(HttpRequest<?> request, @Nullable @QueryValue String root) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return scheduledNetAccessor.getAll(loggedInUser, root);
    }

    @Get("/{id}")
    public ScheduledNet getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return scheduledNetAccessor.get(loggedInUser, id);
    }

    @Put("/{id}")
    public ScheduledNet update(HttpRequest<?> request,  @PathVariable String id, @Body ScheduledNet obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return scheduledNetAccessor.update(loggedInUser, id, obj);
    }

    @Put("/{id}/requests")
    public ScheduledNet updateUsingRequest(HttpRequest<?> request,  @PathVariable String id, @Body ScheduledNetCreateRequestExternal obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return scheduledNetAccessor.update(loggedInUser, id, obj.getScheduledNet());
    }

    @Delete("/{id}")
    public void delete(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ScheduledNet net = scheduledNetAccessor.get(loggedInUser, id);
        scheduledNetAccessor.delete(loggedInUser, net.getCallsign());

        return;
    }

    @Delete("/all/now")
    public void deleteAll(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        scheduledNetAccessor.deleteAll(loggedInUser);
        changePublisherAccessor.publishAllUpdate();
    }
}
