package netcentral.transceiver.agw.object;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Callsign {
    private String callsign;

    public Callsign() {
    }
    public Callsign(String callsign) {
        this.callsign = callsign;
    }
    public Callsign(Callsign callsign) {
        if (callsign != null) {
            this.callsign = callsign.getCallsign();
        }
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
}
