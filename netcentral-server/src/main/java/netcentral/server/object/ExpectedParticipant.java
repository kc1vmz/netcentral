package netcentral.server.object;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ExpectedParticipant {
    private String callsign;

    public ExpectedParticipant() {
    }
    public ExpectedParticipant(String callsign) {
        this.callsign = callsign;
    }
    public ExpectedParticipant(ExpectedParticipant participant) {
        if (participant != null) {
            this.callsign = participant.getCallsign();
        }
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
}
