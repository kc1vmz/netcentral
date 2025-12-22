package netcentral.server.controller;

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

import java.util.ArrayList;
import java.util.List;

import com.kc1vmz.netcentral.aprsobject.object.APRSWeatherReport;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.APRSObjectAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.User;

@Controller("/api/v1/weatherReports") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class WeatherReportController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private APRSObjectAccessor aprsObjectAccessor;


    @Get("/callsigns/{callsign}/all")
    public List<APRSWeatherReport> getWeatherReportsForCallsign(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<APRSWeatherReport> reports = aprsObjectAccessor.getWeatherReports(loggedInUser, callsign);

        return reports;
    }

    @Get("/callsigns/{callsign}/latest")
    public APRSWeatherReport getLatestWeatherReportsForCallsign(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        APRSWeatherReport report = aprsObjectAccessor.getLatestWeatherReport(loggedInUser, callsign);

        return report;
    }

    @Delete("/callsigns/{callsign}/all")
    public List<APRSWeatherReport> deleteWeatherReportsForCallsign(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        aprsObjectAccessor.deleteWeatherReports(loggedInUser, callsign);

        return new ArrayList<>();
    }
}
