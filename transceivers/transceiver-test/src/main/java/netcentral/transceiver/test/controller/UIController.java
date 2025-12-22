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

import java.util.Map;

import io.micronaut.views.fields.FormGenerator;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import netcentral.transceiver.test.accessor.UIAccessor;
import netcentral.transceiver.test.config.NetCentralClientConfig;
import netcentral.transceiver.test.config.RegisteredTransceiverConfig;
import netcentral.transceiver.test.object.RegisterRequest;
import netcentral.transceiver.test.object.SendMessageRequest;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS) 
public class UIController {
    @Inject
    UIAccessor uiAccessor;
    @Inject
    NetCentralClientConfig netControlClientConfig;
    @Inject
    RegisteredTransceiverConfig registeredTransceiverConfig;

    private FormGenerator formGenerator;

    public UIController(FormGenerator formGenerator) {
        this.formGenerator = formGenerator;
    }

    @View("home")
    @Get("/")
    public HttpResponse<?> home(HttpRequest<?> request) {
        return HttpResponse.ok(/*CollectionUtils.mapOf("token", token, "user", loggedInUser)*/);
    }

    @SuppressWarnings("unchecked")
    @View("sendmessage")
    @Get("/sendmessage")
    public Map<String, Object> sendMessage(HttpRequest<?> request) {
        return CollectionUtils.mapOf("form", formGenerator.generate("/sendmessage-action", SendMessageRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/sendmessage-action")
    HttpResponse<?> quizMasterCreateAction(HttpRequest<?> request, @Valid @Body SendMessageRequest messageRequest) {
        uiAccessor.sendMessage(messageRequest);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/sendmessage").build());
    } 

    @SuppressWarnings("unchecked")
    @View("register")
    @Get("/register")
    public Map<String, Object> register(HttpRequest<?> request) {
        return CollectionUtils.mapOf("cfgServer", netControlClientConfig.getServer(), 
            "cfgPort", ""+netControlClientConfig.getPort(), 
            "cfgUsername", netControlClientConfig.getUsername(), 
            "cfgPassword", netControlClientConfig.getPassword(), 
            "rtName", registeredTransceiverConfig.getName(),
            "rtDescription", registeredTransceiverConfig.getDescription(),
            "rtType", registeredTransceiverConfig.getType(),
            "rtPort", registeredTransceiverConfig.getPort(),
            "form", formGenerator.generate("/register-action", RegisterRequest.class));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/register-action")
    HttpResponse<?> registerAction(HttpRequest<?> request, @Valid @Body RegisterRequest messageRequest) {
        uiAccessor.register(messageRequest);

        return HttpResponse.seeOther(UriBuilder.of("/").path("/register").build());
    } 
}
