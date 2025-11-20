package netcentral.server.controller;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;

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
import netcentral.server.accessor.APRSObjectAccessor;
import netcentral.server.accessor.ChangePublisherAccessor;
import netcentral.server.accessor.CompletedNetAccessor;
import netcentral.server.accessor.NetAccessor;
import netcentral.server.accessor.NetMessageAccessor;
import netcentral.server.accessor.NetParticipantAccessor;
import netcentral.server.accessor.ParticipantAccessor;
import netcentral.server.accessor.TransceiverCommunicationAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.RadioStyle;
import netcentral.server.object.Net;
import netcentral.server.object.NetMessage;
import netcentral.server.object.Participant;
import netcentral.server.object.User;

@Controller("/api/v1/nets") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class NetController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private NetAccessor netAccessor;
    @Inject
    private ParticipantAccessor participantAccessor;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;
    @Inject
    private CompletedNetAccessor completedNetAccessor;
    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private NetMessageAccessor netMessageAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;

    @Post
    public Net create(HttpRequest<?> request,  @Body Net obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return netAccessor.create(loggedInUser, obj);
    }

    @Get 
    public List<Net> getAll(HttpRequest<?> request, @Nullable @QueryValue String root) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return netAccessor.getAll(loggedInUser, root);
    }

    @Get("/{id}")
    public Net getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return netAccessor.get(loggedInUser, id);
    }

    @Put("/{id}")
    public Net update(HttpRequest<?> request,  @PathVariable String id, @Body Net obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return netAccessor.update(loggedInUser, id, obj);
    }
    
    @Delete("/{id}")
    public void delete(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.get(loggedInUser, id);
        netAccessor.delete(loggedInUser, net.getCallsign());

        // create a completed net
        completedNetAccessor.create(loggedInUser, net);
    }

    @Get("/{id}/participants") 
    public List<Participant> getParticipants(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.get(loggedInUser, id);

        List<Participant> ret = netParticipantAccessor.getAllParticipants(loggedInUser, net);
        return ret;

    }

    @Post("/{id}/participants/{callsign}")
    public List<Participant> addParticipant(HttpRequest<?> request, @PathVariable String id, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.get(loggedInUser, id);
        Participant participant;
        try {
            participant = participantAccessor.get(loggedInUser, callsign);
        } catch (Exception e) {
            participant = null;
        }
        if (participant == null) {
            participant = new Participant(callsign, "", "", ZonedDateTime.now(), null, null, ElectricalPowerType.UNKNOWN, ElectricalPowerType.UNKNOWN, RadioStyle.UNKNOWN, 0, null, null);
            participant =  participantAccessor.create(loggedInUser, participant);
        }

        netParticipantAccessor.addParticipant(loggedInUser, net, participant);
        List<Participant> ret = netParticipantAccessor.getAllParticipants(loggedInUser, net);
        return ret;
    }

    @Delete("/{id}/participants/{callsign}")
    public List<Participant> deleteParticipant(HttpRequest<?> request, @PathVariable String id, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.get(loggedInUser, id);
        Participant participant = participantAccessor.get(loggedInUser, callsign);

        netParticipantAccessor.removeParticipant(loggedInUser, net, participant);

        try {
            List<Net> nets = netParticipantAccessor.getAllNets(loggedInUser, participant);
            if ((nets == null) || nets.isEmpty()) {
                // remove participant if no longer in any nets
                participantAccessor.delete(loggedInUser, participant.getCallsign());
            }
        } catch (Exception e) {
        }

        List<Participant> ret = netParticipantAccessor.getAllParticipants(loggedInUser, net);
        return ret;
    }

    @Get("/{id}/messages") 
    public List<APRSMessage> getMessages(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.get(loggedInUser, id);
        List<APRSMessage> messages = new ArrayList<>();

        try {
            messages = aprsObjectAccessor.getCompletedNetMessages(loggedInUser, net.getCompletedNetId());
        } catch (Exception e) {
            // ignore
        }
        return messages;
    }

    @Post("/{id}/messages")
    public String sendMessage(HttpRequest<?> request,  @PathVariable String id, @Body String message) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        Net net = netAccessor.get(loggedInUser, id);

        List<Participant> participants = netParticipantAccessor.getAllParticipants(loggedInUser, net);
        ZonedDateTime time = ZonedDateTime.now();
        NetMessage msg = new NetMessage(UUID.randomUUID().toString(), net.getCompletedNetId(), net.getCallsign(), message, time);
        netMessageAccessor.create(loggedInUser, msg);
        aprsObjectAccessor.createAPRSMessageFromNetMessage(loggedInUser, net, msg, "CONSOLE");

        if ((participants != null) && (!participants.isEmpty())) {
            for (Participant participant : participants) {
                transceiverCommunicationAccessor.sendMessage(loggedInUser, net.getCallsign(), participant.getCallsign(), message);
            }
        }
        return "{ \"message\" : \""+message+"\"}";
    }

    @Delete("/all/now")
    public void deleteAll(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        netAccessor.deleteAll(loggedInUser);
        netParticipantAccessor.deleteAll(loggedInUser);
        changePublisherAccessor.publishAllUpdate();
    }
}
