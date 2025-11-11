package netcentral.transceiver.kiss.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class APRSConfiguration {
    @Value("${aprs.config.callsign}")
    private String callsign;
    @Value("${aprs.config.digipeaters}")
    private String digipeaters;
    @Value("${aprs.config.applicationName}")
    private String applicationName;

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getDigipeaters() {
        return digipeaters;
    }
    public void setDigipeaters(String digipeaters) {
        this.digipeaters = digipeaters;
    }
    public String getApplicationName() {
        return applicationName;
    }
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
