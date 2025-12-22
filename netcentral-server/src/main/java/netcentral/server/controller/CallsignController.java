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
import java.util.Map;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.CallsignAccessor;
import netcentral.server.accessor.NetAccessor;
import netcentral.server.accessor.TransceiverCommunicationAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.Callsign;
import netcentral.server.object.Net;
import netcentral.server.object.User;

@Controller("/api/v1/callsigns") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class CallsignController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private CallsignAccessor callsignAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;
    @Inject
    private NetAccessor netAccessor;

    @Get 
    public List<Callsign> getAll(HttpRequest<?> request, @Nullable @QueryValue String root) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return callsignAccessor.getAll(loggedInUser, root);
    }

    @Get("/roots") 
    public Map<String, List<Callsign>> getAllRoot(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return callsignAccessor.getAllRoot(loggedInUser);
    }

    @Get("/{id}")
    public Callsign getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return callsignAccessor.getByCallsign(loggedInUser, id);
    }

    
    @Delete("/{id}")
    public void delete(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Callsign callsign =  callsignAccessor.getByCallsign(loggedInUser, id);
        callsignAccessor.delete(loggedInUser, callsign.getCallsign());

        return;
    }

    @Post("/{id}/identifyRequests")
    public Callsign identify(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Callsign callsign = callsignAccessor.getByCallsign(loggedInUser, id);
        callsignAccessor.identify(loggedInUser, id);

        return callsign;
    }

    @Put("/{id}")
    public Callsign update(HttpRequest<?> request,  @PathVariable String id, @Body Callsign obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return callsignAccessor.update(loggedInUser, id, obj);
    }

    @Post("/{callsign}/messages/{netCallsign}")
    public String sendMessage(HttpRequest<?> request, @PathVariable String callsign, @PathVariable String netCallsign, @Body String message) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.get(loggedInUser, netCallsign);

        transceiverCommunicationAccessor.sendMessage(loggedInUser, net.getCallsign(), callsign, message);

        return "{ \"message\" : \""+message+"\"}";
    }


}
