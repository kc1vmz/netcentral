package netcentral.server.controller;

import java.util.List;

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
import netcentral.server.accessor.ChangePublisherAccessor;
import netcentral.server.accessor.NetExpectedParticipantAccessor;
import netcentral.server.accessor.ScheduledNetAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.ExpectedParticipant;
import netcentral.server.object.ScheduledNet;
import netcentral.server.object.User;
import netcentral.server.object.request.ScheduledNetCreateRequestExternal;

@Controller("/api/v1/scheduledNets") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class ScheduledNetController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private ScheduledNetAccessor scheduledNetAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private NetExpectedParticipantAccessor netExpectedParticipantAccessor;


    @Post
    public ScheduledNet create(HttpRequest<?> request,  @Body ScheduledNet obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return scheduledNetAccessor.create(loggedInUser, obj);
    }

    @Post("/requests")
    public ScheduledNet createFromRequest(HttpRequest<?> request,  @Body ScheduledNetCreateRequestExternal obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ScheduledNet scheduledNet = scheduledNetAccessor.create(loggedInUser, obj.getScheduledNet());

        List<ExpectedParticipant> expectedParticipants = obj.getExpectedParticipants();
        for (ExpectedParticipant expectedParticipant : expectedParticipants) {
            netExpectedParticipantAccessor.addExpectedParticipant(loggedInUser, scheduledNet, expectedParticipant);
        }
        return scheduledNet;
    }

    @Get 
    public List<ScheduledNet> getAll(HttpRequest<?> request, @Nullable @QueryValue String root) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return scheduledNetAccessor.getAll(loggedInUser, root);
    }

    @Get("/{id}")
    public ScheduledNet getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return scheduledNetAccessor.get(loggedInUser, id);
    }

    @Put("/{id}")
    public ScheduledNet update(HttpRequest<?> request,  @PathVariable String id, @Body ScheduledNet obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return scheduledNetAccessor.update(loggedInUser, id, obj);
    }

    @Put("/{id}/requests")
    public ScheduledNet updateUsingRequest(HttpRequest<?> request,  @PathVariable String id, @Body ScheduledNetCreateRequestExternal obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return scheduledNetAccessor.update(loggedInUser, id, obj.getScheduledNet());
    }

    @Delete("/{id}")
    public void delete(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ScheduledNet net = scheduledNetAccessor.get(loggedInUser, id);
        scheduledNetAccessor.delete(loggedInUser, net.getCallsign());

        return;
    }

    @Get("/{id}/expectedParticipants") 
    public List<ExpectedParticipant> getExpectedParticipants(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ScheduledNet net = scheduledNetAccessor.get(loggedInUser, id);

        List<ExpectedParticipant> ret = netExpectedParticipantAccessor.getExpectedParticipants(loggedInUser, net);
        return ret;
    }

    @Post("/{id}/expectedParticipants/{callsign}")
    public List<ExpectedParticipant> addExpectedParticipant(HttpRequest<?> request, @PathVariable String id, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ScheduledNet net = scheduledNetAccessor.get(loggedInUser, id);
        ExpectedParticipant participant = null;
        try {
            participant = netExpectedParticipantAccessor.get(loggedInUser, callsign);
        } catch (Exception e) {
            participant = null;
        }

        if (participant == null) {
            participant = new ExpectedParticipant(callsign);
            netExpectedParticipantAccessor.addExpectedParticipant(loggedInUser, net, participant);
        }
        List<ExpectedParticipant> ret = netExpectedParticipantAccessor.getExpectedParticipants(loggedInUser, net);
        return ret;
    }

    @Post("/{id}/expectedParticipants/requests")
    public List<ExpectedParticipant> resetExpectedParticipants(HttpRequest<?> request, @PathVariable String id, @Body String expectedCallsigns) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        expectedCallsigns = expectedCallsigns.substring(1, expectedCallsigns.length()-1);
        ScheduledNet net = scheduledNetAccessor.get(loggedInUser, id);
        try {
            // remove existing
            List<ExpectedParticipant> expectedParticipants = netExpectedParticipantAccessor.getExpectedParticipants(loggedInUser, net);
            if (expectedParticipants != null) {
                for (ExpectedParticipant expectedParticipant : expectedParticipants) {
                    netExpectedParticipantAccessor.removeExpectedParticipant(loggedInUser, net, expectedParticipant);
                }
            }

            // add back new callsigns
            if (expectedCallsigns != null) {
                String [] callsigns = expectedCallsigns.split("[\\s,]+");
                if (callsigns != null) {
                    for (String callsign : callsigns) {
                        if ((callsign != null) && (callsign.length()>0)) {
                            netExpectedParticipantAccessor.addExpectedParticipant(loggedInUser, net, new ExpectedParticipant(callsign));
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        List<ExpectedParticipant> ret = netExpectedParticipantAccessor.getExpectedParticipants(loggedInUser, net);
        return ret;
    }

    @Delete("/{id}/expectedParticipants/{callsign}")
    public List<ExpectedParticipant> deleteExpectedParticipant(HttpRequest<?> request, @PathVariable String id, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        ScheduledNet net = scheduledNetAccessor.get(loggedInUser, id);
        ExpectedParticipant participant = null;
        try {
            participant = netExpectedParticipantAccessor.get(loggedInUser, callsign);
        } catch (Exception e) {
            participant = null;
        }

        if (participant != null) {
            participant = new ExpectedParticipant(callsign);
            netExpectedParticipantAccessor.removeExpectedParticipant(loggedInUser, net, participant);
        }
        List<ExpectedParticipant> ret = netExpectedParticipantAccessor.getExpectedParticipants(loggedInUser, net);
        return ret;
    }

    @Delete("/all/now")
    public void deleteAll(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        scheduledNetAccessor.deleteAll(loggedInUser);
        netExpectedParticipantAccessor.deleteAll(loggedInUser);
        changePublisherAccessor.publishAllUpdate();
    }
}
