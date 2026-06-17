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

import java.util.List;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.TrackedStationTypeRuleAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.TrackedStationTypeRule;
import netcentral.server.object.User;
import netcentral.server.object.request.TrackedStationRuleRequest;

@Controller("/api/v1/trackedStationTypeRules") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class TrackedStationTypeRulesController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private TrackedStationTypeRuleAccessor trackedStationTypeRuleAccessor;

    @Post
    public TrackedStationTypeRule create(HttpRequest<?> request,  @Body TrackedStationTypeRule obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStationTypeRule rule = trackedStationTypeRuleAccessor.create(loggedInUser, obj);
        return rule;
    }

    @Post("/requests")
    public TrackedStationTypeRule createUsingRequest(HttpRequest<?> request,  @Body TrackedStationRuleRequest obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStationTypeRule rule = trackedStationTypeRuleAccessor.createByRequest(loggedInUser, obj);
        return rule;
    }

    @Get 
    public List<TrackedStationTypeRule> getAll(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<TrackedStationTypeRule> rules =  trackedStationTypeRuleAccessor.getAll(loggedInUser);
        return rules;
    }

    @Get("/{id}")
    public TrackedStationTypeRule getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStationTypeRule rule = trackedStationTypeRuleAccessor.get(loggedInUser, id);
        return rule;
    }

    @Delete("/{id}")
    public void delete(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        TrackedStationTypeRule rule = trackedStationTypeRuleAccessor.get(loggedInUser, id);
        trackedStationTypeRuleAccessor.delete(loggedInUser, rule.getId());

        return;
    }

    @Put("/{id}/requests")
    public TrackedStationTypeRule updateByRequest(HttpRequest<?> request,  @PathVariable String id, @Body TrackedStationRuleRequest obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return trackedStationTypeRuleAccessor.updateByRequest(loggedInUser, id, obj);
    }
}
