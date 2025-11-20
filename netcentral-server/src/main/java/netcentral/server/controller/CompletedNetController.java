package netcentral.server.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
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
import netcentral.server.accessor.CompletedNetAccessor;
import netcentral.server.accessor.CompletedParticipantAccessor;
import netcentral.server.accessor.NetMessageAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.config.NetConfigServerConfig;
import netcentral.server.object.CompletedNet;
import netcentral.server.object.CompletedParticipant;
import netcentral.server.object.NetMessage;
import netcentral.server.object.User;
import netcentral.server.utils.NetParticipantReport;


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
    private NetParticipantReport netParticipantReport;
    @Inject
    private NetConfigServerConfig netConfigServerConfig;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;


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
            String filename = netParticipantReport.createParticipantReport(net, participants);

            filename = netConfigServerConfig.getTempReportDir()+filename;
            byte[] fileBytes = Files.readAllBytes(Paths.get(filename));
            String newFilename = String.format("NetReport-%s-%s.pdf", net.getCallsign(), net.getPrettyStartTime());
            return HttpResponse.ok(fileBytes)
                    .header("Content-Disposition", "attachment; filename=\""+newFilename+"\"");
        } catch (Exception e) {
            return HttpResponse.serverError();
        }
    }

    @Delete("/all/now")
    public void deleteAll(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        completedNetAccessor.deleteAll(loggedInUser);
        completedParticipantAccessor.deleteAll(loggedInUser);
        changePublisherAccessor.publishAllUpdate();
    }
}
