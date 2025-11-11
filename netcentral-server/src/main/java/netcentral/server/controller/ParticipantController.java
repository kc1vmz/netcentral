package netcentral.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.NetAccessor;
import netcentral.server.accessor.NetParticipantAccessor;
import netcentral.server.accessor.ParticipantAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.Net;
import netcentral.server.object.Participant;
import netcentral.server.object.User;

@Controller("/api/v1/participants") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class ParticipantController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private ParticipantAccessor participantAccessor;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;
    @Inject
    private NetAccessor netAccessor;

    @Get 
    public List<Participant> getAll(HttpRequest<?> request, @Nullable @QueryValue String root) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return participantAccessor.getAll(loggedInUser, root);
    }

    @Get("/roots") 
    public Map<String, List<Participant>> getAllRoot(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return participantAccessor.getAllRoot(loggedInUser);
    }

    @Put("/{id}")
    public Participant update(HttpRequest<?> request, @PathVariable String id, @Body Participant obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return participantAccessor.update(loggedInUser, id, obj);
    }

    @Get("/{id}")
    public Participant getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return participantAccessor.get(loggedInUser, id);
    }

    @Get("/{id}/nets")
    public List<Net> getNets(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Participant participant = participantAccessor.get(loggedInUser, id);
        List<Net> nets = netParticipantAccessor.getAllNets(loggedInUser, participant);
        nets = hydrateNets(loggedInUser, nets);

        return nets;
    }

    private List<Net> hydrateNets(User loggedInUser, List<Net> nets) {
        List<Net> ret = new ArrayList<>();
        if (nets != null) {
            for (Net net : nets) {
                Net hydratedNet = null;
                try {
                    hydratedNet = netAccessor.get(loggedInUser, net.getCallsign());
                } catch (Exception e) {
                    hydratedNet = null;
                }
                if (hydratedNet != null) {
                    ret.add(hydratedNet);
                }
            }
        }

        return ret;
    }
}
