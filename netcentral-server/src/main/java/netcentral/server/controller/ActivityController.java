package netcentral.server.controller;

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
