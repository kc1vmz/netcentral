package netcentral.server.controller;

import com.kc1vmz.netcentral.aprsobject.common.NetCentralServerStatistics;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.StatisticsAccessor;
import netcentral.server.accessor.SummaryAccessor;
import netcentral.server.accessor.TrackedStationAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.RenderedMapItem;
import netcentral.server.object.SummaryDashboard;
import netcentral.server.object.SummaryMapPoints;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.User;

@Controller("/api/v1/summaries") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class SummaryController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private SummaryAccessor summaryAccessor;
    @Inject
    private TrackedStationAccessor trackedStationAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;

    @Get("/dashboardCounts")
    public SummaryDashboard getOne(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return summaryAccessor.getDashboardSummary(loggedInUser);
    }

    @Get("/netMapPoints/{callsign}")
    public SummaryMapPoints getNetMapPoints(HttpRequest<?> request, @PathVariable String callsign, @Nullable @QueryValue Boolean includeTrackedStations, 
                                                    @Nullable @QueryValue Boolean includeInfrastructure, @Nullable @QueryValue Boolean includeObjects, @Nullable @QueryValue Boolean includePriorityObjects) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return summaryAccessor.getNetMapPoints(loggedInUser, callsign, includeTrackedStations, includeInfrastructure, includeObjects, includePriorityObjects);
    }

    @Get("/trackedStation/mapPoint/{callsign}")
    public RenderedMapItem getMapPoint(HttpRequest<?> request,  @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStation station = trackedStationAccessor.getByCallsign(loggedInUser, callsign);

        return trackedStationAccessor.getMapPoint(loggedInUser, station);
    }

    @Get("/objectTypeMapPoints/{objectType}")
    public SummaryMapPoints getObjectTypeMapPoints(HttpRequest<?> request,  @PathVariable String objectType) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return summaryAccessor.getObjectTypeMapPoints(loggedInUser, objectType);
    }

    @Get("/dashboardNetCentral")
    public NetCentralServerStatistics getNetCentralStatistics(HttpRequest<?> request) {
        return statisticsAccessor.get();
    }
}
