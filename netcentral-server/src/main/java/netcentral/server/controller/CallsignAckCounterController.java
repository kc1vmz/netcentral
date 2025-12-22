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
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.CallsignAckCounterAccessor;
import netcentral.server.auth.SessionAccessor;

@Controller("/api/v1/callsignAckCounters") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class CallsignAckCounterController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private CallsignAckCounterAccessor callsignAckCounterAccessor;

    @Get("/{callsignFrom}/{callsignTo}")
    public String get(HttpRequest<?> request, @PathVariable String callsignFrom, @PathVariable String callsignTo) {
        return callsignAckCounterAccessor.get(null, callsignFrom, callsignTo);
    }
}
