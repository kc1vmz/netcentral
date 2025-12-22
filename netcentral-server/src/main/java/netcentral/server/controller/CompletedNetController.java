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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.ChangePublisherAccessor;
import netcentral.server.accessor.CompletedExpectedParticipantAccessor;
import netcentral.server.accessor.CompletedNetAccessor;
import netcentral.server.accessor.CompletedParticipantAccessor;
import netcentral.server.accessor.NetMessageAccessor;
import netcentral.server.accessor.NetQuestionAccessor;
import netcentral.server.accessor.NetQuestionAnswerAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.config.NetConfigServerConfig;
import netcentral.server.object.CompletedExpectedParticipant;
import netcentral.server.object.CompletedNet;
import netcentral.server.object.CompletedParticipant;
import netcentral.server.object.NetMessage;
import netcentral.server.object.NetQuestion;
import netcentral.server.object.NetQuestionAnswer;
import netcentral.server.object.User;
import netcentral.server.utils.NetMessageReport;
import netcentral.server.utils.NetParticipantReport;
import netcentral.server.utils.NetQuestionReport;
import netcentral.server.utils.NetQuestionReportItem;


@Controller("/api/v1/completedNets") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class CompletedNetController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private CompletedNetAccessor completedNetAccessor;
    @Inject
    private CompletedParticipantAccessor completedParticipantAccessor;
    @Inject
    private NetMessageAccessor netMessageAccessor;
    @Inject
    private NetMessageReport netMessageReport;
    @Inject
    private NetQuestionReport netQuestionReport;
    @Inject
    private NetParticipantReport netParticipantReport;
    @Inject
    private NetConfigServerConfig netConfigServerConfig;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private NetQuestionAccessor netQuestionAccessor;
    @Inject
    private NetQuestionAnswerAccessor netQuestionAnswerAccessor;
    @Inject
    private CompletedExpectedParticipantAccessor completedExpectedParticipantAccessor;


    @Get 
    public List<CompletedNet> getAll(HttpRequest<?> request, @Nullable @QueryValue String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return completedNetAccessor.getAll(loggedInUser, callsign);
    }

    @Get("/{id}")
    public CompletedNet getOne(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return completedNetAccessor.get(loggedInUser, id);
    }

    @Get("/{id}/participants") 
    public List<CompletedParticipant> getParticipants(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        CompletedNet net = completedNetAccessor.get(loggedInUser, id);

        List<CompletedParticipant> ret = completedParticipantAccessor.getAllByCompletedNetId(loggedInUser, net.getCompletedNetId());
        return ret;
    }

    @Get("/{id}/expectedParticipants") 
    public List<CompletedExpectedParticipant> getExpectedParticipants(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        CompletedNet net = completedNetAccessor.get(loggedInUser, id);

        List<CompletedExpectedParticipant> ret = completedExpectedParticipantAccessor.getAllByCompletedNetId(loggedInUser, net.getCompletedNetId());
        return ret;
    }

    @Get("/{id}/messages") 
    public List<NetMessage> getMessages(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        CompletedNet net = completedNetAccessor.get(loggedInUser, id);

        List<NetMessage> ret = netMessageAccessor.getAll(loggedInUser, net.getCallsign(), net.getCompletedNetId());
        return ret;
    }

    @Get(uri = "/{id}/partipationReports", produces = MediaType.APPLICATION_PDF)
    public HttpResponse<byte[]> downloadCompletedNetReport(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        CompletedNet net = completedNetAccessor.get(loggedInUser, id);

        try {
            List<CompletedParticipant> participants = completedParticipantAccessor.getAllByCompletedNetId(loggedInUser, net.getCompletedNetId());

            if (participants != null) {
                Collections.sort(participants, new Comparator<CompletedParticipant>() {
                    @Override
                    public int compare(CompletedParticipant p1, CompletedParticipant p2) {
                        return p1.getCallsign().compareTo(p2.getCallsign());
                    }
                });
            }

            List<CompletedExpectedParticipant> expectedParticipants = completedExpectedParticipantAccessor.getAllByCompletedNetId(loggedInUser, net.getCompletedNetId());

            if (expectedParticipants != null) {
                Collections.sort(expectedParticipants, new Comparator<CompletedExpectedParticipant>() {
                    @Override
                    public int compare(CompletedExpectedParticipant p1, CompletedExpectedParticipant p2) {
                        return p1.getCallsign().compareTo(p2.getCallsign());
                    }
                });
            }


            String filename = netParticipantReport.createReport(net, participants, expectedParticipants);

            filename = netConfigServerConfig.getTempReportDir()+filename;
            byte[] fileBytes = Files.readAllBytes(Paths.get(filename));
            String newFilename = String.format("NetReport-%s-%s.pdf", net.getCallsign(), net.getPrettyStartTime());
            return HttpResponse.ok(fileBytes)
                    .header("Content-Disposition", "attachment; filename=\""+newFilename+"\"");
        } catch (Exception e) {
            return HttpResponse.serverError();
        }
    }

    @Get(uri = "/{id}/messageReports", produces = MediaType.APPLICATION_PDF)
    public HttpResponse<byte[]> downloadCompletedNetMessageReport(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        CompletedNet net = completedNetAccessor.get(loggedInUser, id);

        try {
            List<NetMessage> messages = netMessageAccessor.getAll(loggedInUser, null, net.getCompletedNetId());

            if (messages != null) {
                Collections.sort(messages, new Comparator<NetMessage>() {
                    @Override
                    public int compare(NetMessage message1, NetMessage message2) {
                        return message1.getReceivedTime().compareTo(message2.getReceivedTime());
                    }
                });
            }

            String filename = netMessageReport.createReport(net, messages);

            filename = netConfigServerConfig.getTempReportDir()+filename;
            byte[] fileBytes = Files.readAllBytes(Paths.get(filename));
            String newFilename = String.format("NetMessagesReport-%s-%s.pdf", net.getCallsign(), net.getPrettyStartTime());
            return HttpResponse.ok(fileBytes)
                    .header("Content-Disposition", "attachment; filename=\""+newFilename+"\"");
        } catch (Exception e) {
            return HttpResponse.serverError();
        }
    }

    @Get(uri = "/{id}/questionAnswerReports", produces = MediaType.APPLICATION_PDF)
    public HttpResponse<byte[]> downloadCompletedNetQuestionAnswerReport(HttpRequest<?> request, @PathVariable String id) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        CompletedNet net = completedNetAccessor.get(loggedInUser, id);

        try {
            List<NetQuestionReportItem> lines = getQuestionAnswerReportLines(loggedInUser, net.getCompletedNetId());
            String filename = netQuestionReport.createReport(net, lines);

            filename = netConfigServerConfig.getTempReportDir()+filename;
            byte[] fileBytes = Files.readAllBytes(Paths.get(filename));
            String newFilename = String.format("NetQuestionsReport-%s-%s.pdf", net.getCallsign(), net.getPrettyStartTime());
            return HttpResponse.ok(fileBytes)
                    .header("Content-Disposition", "attachment; filename=\""+newFilename+"\"");
        } catch (Exception e) {
            return HttpResponse.serverError();
        }
    }

    private List<NetQuestionReportItem> getQuestionAnswerReportLines(User loggedInUser, String completedNetId) {
        List<NetQuestionReportItem> ret = new ArrayList<>();

        // for each question, add question and all answers
        List<NetQuestion> questions = new ArrayList<>();
        try {
            List<NetQuestion> questionsTemp = netQuestionAccessor.getAll(loggedInUser, completedNetId);
            questions = questionsTemp;
        } catch (Exception e) {
        }

        // Sort by dueDate in ascending order
        if (questions != null) {
            Collections.sort(questions, new Comparator<NetQuestion>() {
                @Override
                public int compare(NetQuestion task1, NetQuestion task2) {
                    return task1.getAskedTime().compareTo(task2.getAskedTime());
                }
            });

            for (NetQuestion question : questions) {
                ret.add(new NetQuestionReportItem(question.getAskedTime(), question.getQuestionText()));
                List <NetQuestionAnswer> answers = new ArrayList<>();
                try {
                    List <NetQuestionAnswer> answersTemp = netQuestionAnswerAccessor.getAll(loggedInUser, question.getNetQuestionId());
                    answers = answersTemp;
                } catch (Exception e) {
                }

                if (answers != null) {
                    Collections.sort(answers, new Comparator<NetQuestionAnswer>() {
                        @Override
                        public int compare(NetQuestionAnswer answer1, NetQuestionAnswer answer2) {
                            return answer1.getAnsweredTime().compareTo(answer2.getAnsweredTime());
                        }
                    });
                    for (NetQuestionAnswer answer : answers) {
                        ret.add(new NetQuestionReportItem(answer.getAnsweredTime(), answer.getAnswerText(), answer.getCallsign()));
                    }
                } else {
                    ret.add(new NetQuestionReportItem("No answers provided"));
                }
            }
        }
        return ret;
    }

    @Delete("/all/now")
    public void deleteAll(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        completedNetAccessor.deleteAll(loggedInUser);
        completedParticipantAccessor.deleteAll(loggedInUser);
        completedExpectedParticipantAccessor.deleteAll(loggedInUser);
        changePublisherAccessor.publishAllUpdate();
    }

    @Get("/{completedNetId}/questions") 
    public List<NetQuestion> getQuestions(HttpRequest<?> request, @PathVariable String completedNetId) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        CompletedNet completedNet = completedNetAccessor.get(loggedInUser, completedNetId);
        List<NetQuestion> messages = new ArrayList<>();

        try {
            messages = netQuestionAccessor.getAll(loggedInUser, completedNet.getCompletedNetId());
        } catch (Exception e) {
            // ignore
        }
        return messages;
    }

    @Get("/{completedNetId}/questions/{questionId}/answers") 
    public List<NetQuestionAnswer> getAnswers(HttpRequest<?> request, @PathVariable String completedNetId, @PathVariable String questionId) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        completedNetAccessor.get(loggedInUser, completedNetId);
        List<NetQuestionAnswer> messages = new ArrayList<>();

        try {
            messages = netQuestionAnswerAccessor.getAll(loggedInUser, questionId);
        } catch (Exception e) {
            // ignore
        }
        return messages;
    }
}
