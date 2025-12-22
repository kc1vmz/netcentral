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
import netcentral.server.accessor.UserAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.User;
import netcentral.server.object.request.RegisterRequest;

@Controller("/api/v1/users") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class UserController {
    @Inject
    private UserAccessor userAccessor;
    @Inject
    SessionAccessor sessionAccessor;

    @Get 
    public List<User> getAll(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return userAccessor.getAll(loggedInUser);
    }

    @Get("/{id}")
    public User getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return userAccessor.get(loggedInUser, id);
    }

    @Post
    public User create(HttpRequest<?> request, @Body User obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return userAccessor.create(loggedInUser, obj);
    }

    @Put("/{id}")
    public User update(HttpRequest<?> request, @PathVariable String id, @Body User obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return userAccessor.update(loggedInUser, id, obj);
    }

    @Delete("/{id}")
    public void delete(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        userAccessor.delete(loggedInUser, id);
    }

    @Post("/register")
    public User register(HttpRequest<?> request, @Body RegisterRequest req) {
        return userAccessor.register(req);
    }

}
