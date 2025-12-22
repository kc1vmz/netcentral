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
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.ActivityAccessor;
import netcentral.server.accessor.UserAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.Activity;
import netcentral.server.object.User;

@Controller("/api/v1/activities") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class ActivityController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private ActivityAccessor activityAccessor;
    @Inject
    private UserAccessor userAccessor;

    @Post
    public Activity create(HttpRequest<?> request,  @Body String text) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Activity activity = activityAccessor.create(loggedInUser, text);
        if (activity.getUser() != null) {
            try {
                User user = userAccessor.get(loggedInUser, activity.getUser().getId());
                if (user != null) {
                    activity.setUser(user);
                }
            } catch (Exception e) {
            }
        }
        return activity;
    }

    @Get 
    public List<Activity> getAll(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        List<Activity> activities =  activityAccessor.getAll(loggedInUser);
        if (activities != null) {
            for (Activity activity: activities) {
                if (activity.getUser() != null) {
                    try {
                        User user = userAccessor.get(loggedInUser, activity.getUser().getId());
                        if (user != null) {
                            activity.setUser(user);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }

        return activities;
    }

    @Get("/{id}")
    public Activity getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Activity activity = activityAccessor.get(loggedInUser, id);
        if (activity.getUser() != null) {
            try {
                User user = userAccessor.get(loggedInUser, activity.getUser().getId());
                if (user != null) {
                    activity.setUser(user);
                }
            } catch (Exception e) {
            }
        }
        return activity;
    }

    @Delete("/{id}")
    public void delete(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Activity activity = activityAccessor.get(loggedInUser, id);
        activityAccessor.delete(loggedInUser, activity.getId());

        return;
    }
}
