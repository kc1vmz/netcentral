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

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.object.APRSObjectResource;
import com.kc1vmz.netcentral.aprsobject.object.APRSStatus;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.APRSObjectAccessor;
import netcentral.server.accessor.ObjectCleanupAccessor;
import netcentral.server.accessor.TransceiverCommunicationAccessor;
import netcentral.server.accessor.UserAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.User;
import netcentral.server.object.request.ObjectCreateRequest;
import netcentral.server.object.request.ObjectCreateRequestExternal;
import netcentral.server.object.response.ObjectCreateResponseExternal;
import netcentral.server.utils.APRSCreateObjectQueue;

@Controller("/api/v1/APRSObjects") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class APRSObjectController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;
    @Inject
    private APRSCreateObjectQueue aprsCreateObjectQueue;
    @Inject
    private ObjectCleanupAccessor objectCleanupAccessor;
    @Inject
    private UserAccessor userAccessor;

    @Post
    public HttpResponse<APRSObjectResource> create(HttpRequest<?> request,  @Body APRSObjectResource obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        String id = UUID.randomUUID().toString();

        if (obj.getHeardTime() == null) {
            obj.setHeardTime(ZonedDateTime.now());
        }
        obj.setId(id);
        URI location = UriBuilder.of("/api/v1/APRSObjects/"+id).build();
        queueCreateObject(loggedInUser, obj);

        return HttpResponse.created(obj).headers(headers -> headers.location(location));
    }

    @Get 
    public List<APRSObject> getAll(HttpRequest<?> request, @QueryValue("priority") Optional<Boolean> priority) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return aprsObjectAccessor.getObjects(loggedInUser, priority.orElse(false));
    }

    @Delete("/{id}")
    public void delete(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        aprsObjectAccessor.delete(loggedInUser, id);

        return;
    }

    @Post("/requests")
    public ObjectCreateResponseExternal objectRequest(HttpRequest<?> request,  @Body ObjectCreateRequestExternal messageRequest) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        String message = constructObjectMessageText(messageRequest);
        // given an object, create it on APRS and in our database
        if ((messageRequest.getLat() != null) && (messageRequest.getLon() != null)) {
            transceiverCommunicationAccessor.sendObject(loggedInUser, messageRequest.getCallsign(), messageRequest.getCallsign(), 
                                       message, messageRequest.isUp(), 
                                       messageRequest.getLat(), 
                                       messageRequest.getLon());
        }

        ObjectCreateRequest obj = new ObjectCreateRequest(Integer.parseInt(messageRequest.getType()), messageRequest.getCallsign(), messageRequest.getDescription(), messageRequest.getLat(), messageRequest.getLon());
        if (messageRequest.isUp()) {
            aprsObjectAccessor.upObject(loggedInUser, obj);
            return new ObjectCreateResponseExternal("Alive", messageRequest.getType());
        } else {
            aprsObjectAccessor.downObject(loggedInUser, obj);
            return new ObjectCreateResponseExternal("Dead", messageRequest.getType());
        }
    }

    private String constructObjectMessageText(ObjectCreateRequestExternal messageRequest) {
        String ret;
        if ((messageRequest.getVoiceFrequency() != null) && 
            ((messageRequest.getVoiceFrequency().length() == 10) || (messageRequest.getVoiceFrequency().length() == 20))) {
            // must be in 10 or 20 char format
            ret = String.format("%s %s", messageRequest.getVoiceFrequency(),messageRequest.getDescription());
        } else {
           ret = messageRequest.getDescription();
        }
        return ret;
    }

    private void queueCreateObject(User loggedInUser, APRSObjectResource obj) {
        aprsCreateObjectQueue.addAPRSObject(obj);
    }

    @Get("/statusMessages/{callsign}")
    public List<APRSStatus> getAllStatus(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return aprsObjectAccessor.getStatus(loggedInUser, callsign);
    }

    @Delete("/all/now")
    public void deleteAllNow(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());

        objectCleanupAccessor.cleanupAllAPRSData(loggedInUser);

        return;
    }

    @Delete("/all/byConfig")
    public void deleteAllByConfig(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());

        objectCleanupAccessor.cleanupObjectsByTime(loggedInUser);

        return;
    }
}
