package netcentral.transceiver.agw.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class APRSConfiguration {
    @Value("${aprs.config.callsign}")
    private String callsign;

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
}
