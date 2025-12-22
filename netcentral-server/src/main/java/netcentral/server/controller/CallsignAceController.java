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

import java.util.List;

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
import netcentral.server.accessor.CallsignAceAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.CallsignAce;
import netcentral.server.object.User;

@Controller("/api/v1/accessControlLists") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class CallsignAceController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private CallsignAceAccessor callsignAceAccessor;

    @Get("/{callsignTarget}")
    public List<CallsignAce> getACLs(HttpRequest<?> request, @PathVariable String callsignTarget) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return callsignAceAccessor.getAcl(loggedInUser, callsignTarget);
    }

    
    @Delete("/{callsignTarget}/{callsignChecked}")
    public void delete(HttpRequest<?> request, @PathVariable String callsignTarget, @PathVariable String callsignChecked) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        callsignAceAccessor.removeAce(loggedInUser, callsignTarget, callsignChecked);

        return;
    }

    @Post("/{callsignTarget}")
    public CallsignAce createAce(HttpRequest<?> request, @PathVariable String callsignTarget, @Body CallsignAce obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return callsignAceAccessor.addAce(loggedInUser, obj.getCallsignTarget(), obj.getCallsignChecked(), obj.getType(), obj.isAllowed(), obj.getProximity());
    }

    @Put("/{callsignTarget}")
    public CallsignAce update(HttpRequest<?> request, @PathVariable String callsignTarget, @Body CallsignAce obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return callsignAceAccessor.updateAce(loggedInUser, obj.getCallsignTarget(), obj.getCallsignChecked(), obj.getType(), obj.isAllowed(), obj.getProximity());
    }
}
