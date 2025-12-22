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

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
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
import netcentral.server.accessor.ToolsAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.User;
import netcentral.server.object.request.NTSSendRadiogramRequest;
import netcentral.server.object.request.WinlinkSendMessageRequest;
import netcentral.server.object.response.NTSSendRadiogramResponse;
import netcentral.server.object.response.WinlinkSendMessageResponse;

@Controller("/api/v1/tools") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class ToolsController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private ToolsAccessor toolsAccessor;

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


}
