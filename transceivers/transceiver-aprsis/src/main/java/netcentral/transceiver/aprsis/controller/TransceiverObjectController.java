package netcentral.transceiver.aprsis.controller;

import com.kc1vmz.netcentral.aprsobject.common.TransceiverObject;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.transceiver.aprsis.accessor.RegisteredTransceiverAccessor;
import netcentral.transceiver.aprsis.accessor.TransceiverObjectAccessor;

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
