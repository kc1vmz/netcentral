package netcentral.transceiver.aprsis.controller;

import java.util.HashMap;
import java.util.Map;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.transceiver.aprsis.accessor.ConfigurationAccessor;
import netcentral.transceiver.aprsis.accessor.RegisteredTransceiverAccessor;

@Controller("/api/v1/configParameters") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class ConfigurationController {
    @Inject
    ConfigurationAccessor configurationAccessor;
    @Inject
    RegisteredTransceiverAccessor registeredTransceiverAccessor;

    @Get
    public Map<String, String> getConfigParameters(HttpRequest<?> request) {
        Map<String, String> values = new HashMap<>();
        values = configurationAccessor.getConfigParameters();
        return values;
    }
    @Put
    public void modifyConfigParameters(HttpRequest<?> request, @Body Map<String, String> values) {
        configurationAccessor.setConfigParameters(values);
    }
}
