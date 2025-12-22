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

import com.kc1vmz.netcentral.aprsobject.common.RegisteredTransceiver;

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
import netcentral.server.accessor.RegisteredTransceiverAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.User;

@Controller("/api/v1/registeredTransceivers") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class RegisteredTranscieverController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private RegisteredTransceiverAccessor registeredTransceiverAccessor;

    @Post
    public RegisteredTransceiver create(HttpRequest<?> request,  @Body RegisteredTransceiver obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        try {
            RegisteredTransceiver registeredTransceiver = registeredTransceiverAccessor.getByName(loggedInUser, obj.getName());
            return registeredTransceiver;
        } catch (Exception e) {
            // does not exist - create
        }

        return registeredTransceiverAccessor.create(loggedInUser, obj);
    }

    @Get 
    public List<RegisteredTransceiver> getAll(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return registeredTransceiverAccessor.getAll(loggedInUser);
    }

    @Get("/{id}")
    public RegisteredTransceiver getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return registeredTransceiverAccessor.get(loggedInUser, id);
    }

    @Delete("/{id}")
    public void delete(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        RegisteredTransceiver registeredTransceiver = registeredTransceiverAccessor.get(loggedInUser, id);
        registeredTransceiverAccessor.delete(loggedInUser, registeredTransceiver.getId());

        return;
    }

    @Put("/{id}")
    public RegisteredTransceiver update(HttpRequest<?> request, @PathVariable String id,  @Body RegisteredTransceiver obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        RegisteredTransceiver registeredTransceiver = registeredTransceiverAccessor.get(loggedInUser, id);
        return registeredTransceiverAccessor.update(loggedInUser, registeredTransceiver.getId(), obj);
    }
}
