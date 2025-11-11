package netcentral.transceiver.kenwood.controller;

import java.util.Map;

import com.kc1vmz.netcentral.aprsobject.common.TransceiverStatistics;

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
import netcentral.transceiver.kenwood.accessor.StatisticsAccessor;
import netcentral.transceiver.kenwood.accessor.UIAccessor;
import netcentral.transceiver.kenwood.config.NetControlClientConfig;
import netcentral.transceiver.kenwood.config.RegisteredTransceiverConfig;
import netcentral.transceiver.kenwood.object.RegisterRequest;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS) 
public class UIController {
    @Inject
    UIAccessor uiAccessor;
    @Inject
    NetControlClientConfig netControlClientConfig;
    @Inject
    RegisteredTransceiverConfig registeredTransceiverConfig;
    @Inject
    private StatisticsAccessor statisticsAccessor;

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

    @View("statistics")
    @Get("/statistics")
    public HttpResponse<?> statistics(HttpRequest<?> request) {
        TransceiverStatistics stats = statisticsAccessor.get();
        String duration = formatDuration(stats.getStartTimeMinutes());
        return HttpResponse.ok(CollectionUtils.mapOf("stats", stats, "duration", duration));
    }

    private String formatDuration(Long minutesIn) {
        Long seconds = minutesIn * 60;
        Long days = seconds / (3600*24);
        Long hours = seconds / 3600;
        Long minutes = (seconds % 3600) / 60;

        String formattedDays = Long.toString(days);
        String formattedHours = Long.toString(hours);
        String formattedMinutes = Long.toString(minutes);

        return String.format("%s days %s hours %s minutes", formattedDays, formattedHours, formattedMinutes);
    }
}
