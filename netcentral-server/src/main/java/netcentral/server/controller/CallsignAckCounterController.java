package netcentral.server.controller;

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
