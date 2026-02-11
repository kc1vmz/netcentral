package netcentral.transceiver.test.controller;

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

import com.kc1vmz.netcentral.aprsobject.common.TransceiverObject;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.transceiver.test.accessor.RegisteredTransceiverAccessor;
import netcentral.transceiver.test.accessor.TransceiverObjectAccessor;

@Controller("/api/v1/transceiverObjects") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class TransceiverObjectController {
    @Inject
    TransceiverObjectAccessor transceiverObjectAccessor;
    @Inject
    RegisteredTransceiverAccessor registeredTransceiverAccessor;

    @Post
    public void create(HttpRequest<?> request, @Body TransceiverObject obj) {
        transceiverObjectAccessor.createObject(obj);
    }
}
