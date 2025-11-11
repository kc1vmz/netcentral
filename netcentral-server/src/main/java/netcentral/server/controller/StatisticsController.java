package netcentral.server.controller;


import com.kc1vmz.netcentral.aprsobject.common.NetCentralServerStatistics;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.StatisticsAccessor;

@Controller("/api/v1/statistics") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class StatisticsController {
    @Inject
    StatisticsAccessor statisticsAccessor;

    @Get
    public NetCentralServerStatistics get(HttpRequest<?> request) {
        return statisticsAccessor.get();
    }
}
