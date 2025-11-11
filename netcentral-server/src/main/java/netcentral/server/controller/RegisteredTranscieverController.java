package netcentral.server.controller;

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
