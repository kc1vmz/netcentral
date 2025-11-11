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
import netcentral.server.accessor.TrackedStationAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.User;

@Controller("/api/v1/trackedStations") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class TrackedStationController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private TrackedStationAccessor trackedStationAccessor;

    @Post
    public TrackedStation create(HttpRequest<?> request,  @Body TrackedStation obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return trackedStationAccessor.create(loggedInUser, obj);
    }

    @Get 
    public List<TrackedStation> getAll(HttpRequest<?> request, @Nullable @QueryValue String name, @Nullable @QueryValue String callsign, @Nullable @QueryValue String type) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return trackedStationAccessor.getAll(loggedInUser, name, callsign, type);
    }

    @Get("/{id}")
    public TrackedStation getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return trackedStationAccessor.get(loggedInUser, id);
    }

    @Put("/{id}")
    public TrackedStation update(HttpRequest<?> request,  @PathVariable String id, @Body TrackedStation obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        TrackedStation trackedStation = trackedStationAccessor.get(loggedInUser, id);

        return trackedStationAccessor.update(loggedInUser, trackedStation.getId(), obj);
    }
    
    @Delete("/{id}")
    public void delete(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation trackedStation = trackedStationAccessor.get(loggedInUser, id);
        trackedStationAccessor.delete(loggedInUser, trackedStation.getId());

        return;
    }
}
