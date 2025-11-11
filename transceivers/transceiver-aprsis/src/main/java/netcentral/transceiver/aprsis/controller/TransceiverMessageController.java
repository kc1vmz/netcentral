package netcentral.transceiver.aprsis.controller;

import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessage;
import com.kc1vmz.netcentral.aprsobject.common.TransceiverMessageMany;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.transceiver.aprsis.accessor.RegisteredTransceiverAccessor;
import netcentral.transceiver.aprsis.accessor.TransceiverMessageAccessor;

@Controller("/api/v1/transceiverMessages") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class TransceiverMessageController {
    @Inject
    TransceiverMessageAccessor transceiverMessageAccessor;
    @Inject
    RegisteredTransceiverAccessor registeredTransceiverAccessor;

    @Post
    public void create(HttpRequest<?> request, @Body TransceiverMessage obj) {
        transceiverMessageAccessor.create(obj);
    }
    @Post("/many")
    public void create(HttpRequest<?> request, @Body TransceiverMessageMany obj) {
        transceiverMessageAccessor.create(obj);
    }
}
