package netcentral.transceiver.kenwood.controller;

import com.kc1vmz.netcentral.aprsobject.common.TransceiverReport;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import netcentral.transceiver.kenwood.accessor.TransceiverReportAccessor;

@Controller("/api/v1/transceiverReports") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class TransceiverReportController {
    @Inject
    TransceiverReportAccessor transceiverReportAccessor;

    @Post
    public void create(HttpRequest<?> request, @Body TransceiverReport obj) {
        transceiverReportAccessor.create(obj);
    }
}
