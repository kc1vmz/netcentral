package netcentral.server.controller;

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;

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
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.server.accessor.APRSObjectAccessor;
import netcentral.server.accessor.NetCentralServerConfigAccessor;
import netcentral.server.accessor.TransceiverCommunicationAccessor;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.enums.ObjectSymbolTableConstants;
import netcentral.server.object.NetCentralServerConfiguration;
import netcentral.server.object.User;
import netcentral.server.object.request.ObjectCreateRequest;

@Controller("/api/v1/configurations") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class NetCentralServerConfigurationController {
    @Inject
    SessionAccessor sessionAccessor;
    @Inject
    private NetCentralServerConfigAccessor netCentralServerConfigAccessor;
    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;

    @Get
    public NetCentralServerConfiguration getOne(HttpRequest<?> request) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);

        return netCentralServerConfigAccessor.getDefault(loggedInUser);
    }

    @Put
    public NetCentralServerConfiguration update(HttpRequest<?> request, @Body NetCentralServerConfiguration obj) {
        String token = sessionAccessor.getTokenFromSession(request);
        User loggedInUser = sessionAccessor.getUserFromToken(token);
        NetCentralServerConfiguration original = netCentralServerConfigAccessor.getDefault(loggedInUser);
        NetCentralServerConfiguration ret = netCentralServerConfigAccessor.updateDefault(loggedInUser, obj);

        if (original.isNetMgrEnabled() != ret.isNetMgrEnabled()) {
            configureAPRSNetManager(loggedInUser, original, ret);
        }

        return ret;
    }

    private void configureAPRSNetManager(User loggedInUser, NetCentralServerConfiguration previous, NetCentralServerConfiguration next) {
        if ((previous == null) || (next == null)) {
            return;
        }
        if ((previous.isNetMgrEnabled() == next.isNetMgrEnabled())) {
            return;
        }

        String description = "Net Central Net Manager";
        ObjectCreateRequest objectCreateRequest = new ObjectCreateRequest(ObjectType.NETMGR.ordinal(), next.getNetMgrCallsign(), description, 
                                                                            next.getNetMgrLat(), next.getNetMgrLon(),
                                                                            ObjectSymbolTableConstants.DEFAULT_NETMGR_SYMBOL_TABLE_ID,
                                                                            ObjectSymbolTableConstants.DEFAULT_NETMGR_SYMBOL_TABLE_CODE);

        boolean alive = false;
        if (next.isNetMgrEnabled()) {
            // enable the manager - add the object
            aprsObjectAccessor.upObject(loggedInUser, objectCreateRequest);
            alive = true;
        } else {
            alive = false;
            // disable the manager - kill the object
            aprsObjectAccessor.downObject(loggedInUser, objectCreateRequest);
        }

        transceiverCommunicationAccessor.sendObject(loggedInUser,next.getNetMgrCallsign(), next.getNetMgrCallsign(), description, alive, 
                                                next.getNetMgrLat(), next.getNetMgrLon(),
                                                ObjectSymbolTableConstants.DEFAULT_NETMGR_SYMBOL_TABLE_ID,
                                                ObjectSymbolTableConstants.DEFAULT_NETMGR_SYMBOL_TABLE_CODE);

    }

}
