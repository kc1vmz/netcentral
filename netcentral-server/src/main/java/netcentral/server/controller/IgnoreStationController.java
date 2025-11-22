package netcentral.server.controller;

import java.time.ZonedDateTime;
import java.util.List;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.IgnoreStationAccessor;

import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.IgnoreStation;
import netcentral.server.object.User;

@Controller("/api/v1/ignoreStations") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class IgnoreStationController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private IgnoreStationAccessor ignoreStationAccessor;

    @Get 
    public List<IgnoreStation> getAll(HttpRequest<?> request, @Nullable @QueryValue String root) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return ignoreStationAccessor.getAll(loggedInUser, root);
    }

    @Delete("/{callsign}")
    public void delete(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ignoreStationAccessor.delete(loggedInUser, callsign);

        return;
    }

    @Post
    public IgnoreStation create(HttpRequest<?> request, @Body IgnoreStation ignoreStation) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ignoreStation.setIgnoreStartTime(ZonedDateTime.now());
        return ignoreStationAccessor.create(loggedInUser, ignoreStation);
    }
}
