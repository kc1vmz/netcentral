package netcentral.server.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;

import com.kc1vmz.netcentral.aprsobject.object.APRSRaw;
import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

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

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.APRSObjectAccessor;
import netcentral.server.accessor.NetCentralServerConfigAccessor;
import netcentral.server.accessor.ToolsAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.User;
import netcentral.server.object.request.NTSSendRadiogramRequest;
import netcentral.server.object.request.WinlinkSendMessageRequest;
import netcentral.server.object.response.NTSSendRadiogramResponse;
import netcentral.server.object.response.WinlinkSendMessageResponse;
import netcentral.server.utils.RawPacketReport;

@Controller("/api/v1/tools") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class ToolsController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private ToolsAccessor toolsAccessor;
    @Inject
    private NetCentralServerConfigAccessor netCentralServerConfigAccessor;
    @Inject
    private RawPacketReport rawPacketReport;
    @Inject 
    private APRSObjectAccessor aprsObjectAccessor;

    @Post("/winlink/requests")
    public WinlinkSendMessageResponse initiateWinlinkMessage(HttpRequest<?> request, @Body WinlinkSendMessageRequest action) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        WinlinkSendMessageResponse response = new WinlinkSendMessageResponse();
        response = toolsAccessor.initiateWinlinkMessage(loggedInUser, action);
        if (!response.isSuccess()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, response.getStatus());
        }
        return response;
    }
    @Get("/winlink/requests/{callsign}")
    public WinlinkSendMessageResponse getWinlinkMessageStatus(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        WinlinkSendMessageResponse response = new WinlinkSendMessageResponse();
        response = toolsAccessor.getStatusWinlinkMessage(loggedInUser, callsign);
        if (!response.isSuccess()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, response.getStatus());
        }
        return response;
    }
    @Delete("/winlink/requests/{callsign}")
    public void terminateWinlinkMessage(HttpRequest<?> request, @PathVariable String callsign) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        WinlinkSendMessageResponse response = new WinlinkSendMessageResponse();
        response = toolsAccessor.deleteStatusWinlinkMessage(loggedInUser, callsign);
        if (!response.isSuccess()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, response.getStatus());
        }
    }

    @Post("/ntsradiogram/requests")
    public NTSSendRadiogramResponse initiateNTSRadiogram(HttpRequest<?> request, @Body NTSSendRadiogramRequest action) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        NTSSendRadiogramResponse response = new NTSSendRadiogramResponse();
        response = toolsAccessor.initiateNTSRadiogram(loggedInUser, action);
        if (!response.isSuccess()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, response.getStatus());
        }
        return response;
    }

    @Get(uri = "/rawPackets", produces = MediaType.TEXT_PLAIN)
    public HttpResponse<byte[]> downloadCompletedNetReport(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        try {
            List<APRSRaw> rawPackets = aprsObjectAccessor.getRawPackets(loggedInUser);
            String filename = rawPacketReport.createReport(rawPackets);

            filename = netCentralServerConfigAccessor.getTempReportDir()+filename;
            byte[] fileBytes = Files.readAllBytes(Paths.get(filename));
            String newFilename = String.format("RawPacketReport-%s.txt", PrettyZonedDateTimeFormatter.format(ZonedDateTime.now()));
            return HttpResponse.ok(fileBytes)
                    .header("Content-Disposition", "attachment; filename=\""+newFilename+"\"");
        } catch (Exception e) {
            return HttpResponse.serverError();
        }
    }


}
