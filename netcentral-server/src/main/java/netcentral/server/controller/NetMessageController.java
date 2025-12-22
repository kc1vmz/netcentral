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
import netcentral.server.accessor.ChangePublisherAccessor;
import netcentral.server.accessor.NetMessageAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.NetMessage;
import netcentral.server.object.User;

@Controller("/api/v1/netMessages") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class NetMessageController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private NetMessageAccessor netMessageAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;

    @Post
    public NetMessage create(HttpRequest<?> request,  @Body NetMessage obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return netMessageAccessor.create(loggedInUser, obj);
    }

    @Get 
    public List<NetMessage> getAll(HttpRequest<?> request, @Nullable @QueryValue String callsignNet, @Nullable @QueryValue String completedNetId) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return netMessageAccessor.getAll(loggedInUser, callsignNet, completedNetId);
    }

    @Get("/{id}")
    public NetMessage getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return netMessageAccessor.get(loggedInUser, id);
    }

    @Delete("/all/now")
    public void deleteAll(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        netMessageAccessor.deleteAll(loggedInUser);
        changePublisherAccessor.publishAllUpdate();
    }
}
