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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.APRSObjectAccessor;
import netcentral.server.accessor.ChangePublisherAccessor;
import netcentral.server.accessor.CompletedNetAccessor;
import netcentral.server.accessor.NetAccessor;
import netcentral.server.accessor.NetExpectedParticipantAccessor;
import netcentral.server.accessor.NetMessageAccessor;
import netcentral.server.accessor.NetParticipantAccessor;
import netcentral.server.accessor.NetQuestionAccessor;
import netcentral.server.accessor.NetQuestionAnswerAccessor;
import netcentral.server.accessor.ParticipantAccessor;
import netcentral.server.accessor.TransceiverCommunicationAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.RadioStyle;
import netcentral.server.object.ExpectedParticipant;
import netcentral.server.object.Net;
import netcentral.server.object.NetMessage;
import netcentral.server.object.NetQuestion;
import netcentral.server.object.NetQuestionAnswer;
import netcentral.server.object.Participant;
import netcentral.server.object.User;
import netcentral.server.object.request.NetCreateRequestExternal;

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
    @Inject
    private NetQuestionAccessor netQuestionAccessor;
    @Inject
    private NetQuestionAnswerAccessor netQuestionAnswerAccessor;
    @Inject
    private NetExpectedParticipantAccessor netExpectedParticipantAccessor;

    @Post
    public Net create(HttpRequest<?> request,  @Body Net obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        if (obj.isRemote()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Cannot create remote net");
        }

        return netAccessor.create(loggedInUser, obj);
    }

    @Post("/requests")
    public Net createFromRequest(HttpRequest<?> request,  @Body NetCreateRequestExternal obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net =  netAccessor.create(loggedInUser, obj.getNet());

        List<ExpectedParticipant> expectedParticipants = obj.getExpectedParticipants();
        for (ExpectedParticipant expectedParticipant : expectedParticipants) {
            netExpectedParticipantAccessor.addExpectedParticipant(loggedInUser, net, expectedParticipant);
        }
        return net;
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

        if (obj.isRemote()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Cannot update remote net");
        }

        return netAccessor.update(loggedInUser, id, obj);
    }
    
    @Delete("/{id}")
    public void delete(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.get(loggedInUser, id);
        // create a completed net
        completedNetAccessor.create(loggedInUser, net);

        netAccessor.delete(loggedInUser, net.getCallsign());
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

    @Get("/{id}/expectedParticipants") 
    public List<ExpectedParticipant> getExpectedParticipants(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.get(loggedInUser, id);

        List<ExpectedParticipant> ret = netExpectedParticipantAccessor.getExpectedParticipants(loggedInUser, net);
        return ret;
    }

    @Post("/{id}/expectedParticipants/{callsign}")
    public List<ExpectedParticipant> addExpectedParticipant(HttpRequest<?> request, @PathVariable String id, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.get(loggedInUser, id);
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
        Net net = netAccessor.get(loggedInUser, id);
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

        Net net = netAccessor.get(loggedInUser, id);
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

        if (net.isRemote()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net is remote");
        }

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
        netQuestionAccessor.deleteAllData(loggedInUser);
        netQuestionAnswerAccessor.deleteAllData(loggedInUser);
        netExpectedParticipantAccessor.deleteAll(loggedInUser);
        changePublisherAccessor.publishAllUpdate();
    }


    @Get("/{id}/questions") 
    public List<NetQuestion> getQuestions(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        Net net = netAccessor.get(loggedInUser, id);
        List<NetQuestion> messages = new ArrayList<>();

        try {
            messages = netQuestionAccessor.getAll(loggedInUser, net.getCompletedNetId());
        } catch (Exception e) {
            // ignore
        }
        return messages;
    }

    @Post("/{callsign}/questions")
    public NetQuestion createQuestion(HttpRequest<?> request,  @PathVariable String callsign, @Body NetQuestion netQuestion) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        Net net = netAccessor.get(loggedInUser, callsign);

        List<Participant> netParticipants = new ArrayList<>();
        try {
            List<Participant> netParticipantsTemp = netParticipantAccessor.getAllParticipants(loggedInUser, net);
            netParticipants = netParticipantsTemp;
        } catch (Exception e) {
        }

        try {
            NetQuestion ret = netQuestionAccessor.create(loggedInUser, netQuestion, net, netParticipants);
            return ret;
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    @Get("/{callsign}/questions/{questionId}/answers") 
    public List<NetQuestionAnswer> getAnswers(HttpRequest<?> request, @PathVariable String callsign, @PathVariable String questionId) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        netAccessor.get(loggedInUser, callsign);
        List<NetQuestionAnswer> messages = new ArrayList<>();

        try {
            messages = netQuestionAnswerAccessor.getAll(loggedInUser, questionId);
        } catch (Exception e) {
            // ignore
        }
        return messages;
    }
   
    @Post("/{callsign}/questions/{questionId}/answers")
    public NetQuestionAnswer createAnswer(HttpRequest<?> request,  @PathVariable String callsign, @PathVariable String questionId, @Body NetQuestionAnswer netQuestionAnswer) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        Net net = netAccessor.get(loggedInUser, callsign);

        try {
            NetQuestionAnswer ret = netQuestionAnswerAccessor.create(loggedInUser, netQuestionAnswer, net);
            return ret;
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

}
