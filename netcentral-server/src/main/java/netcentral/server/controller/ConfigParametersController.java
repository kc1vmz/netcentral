package netcentral.server.controller;

/*
    Net Central
    Copyright (c) , 2026 John Rokicki KC1VMZ

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
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.ConfigParametersAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.object.User;

@Controller("/api/v1/configParameters") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class ConfigParametersController {
    @Inject
    private SessionAccessor sessionAccessor;
    @Inject
    private ConfigParametersAccessor configParametersAccessor;

    @Put("/{param}")
    public String setConfigurationParameter(HttpRequest<?> request,  @PathVariable String param, @QueryValue String value) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return configParametersAccessor.setValue(loggedInUser, param, value);
    }

    @Get("/{param}")
    public String getConfigurationParameter(HttpRequest<?> request, @PathVariable String param) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return configParametersAccessor.getValue(loggedInUser, param);
    }
}
