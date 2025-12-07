package netcentral.server.object;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class CompletedExpectedParticipant {
    private String callsign;
    private String completedNetParticipantId;
    private String completedNetId;

    public CompletedExpectedParticipant() {
    }
    public CompletedExpectedParticipant(String callsign, String completedNetId, String completedNetParticipantId) {
        this.callsign = callsign;
        this.completedNetId = completedNetId;
        this.completedNetParticipantId = completedNetParticipantId;
    }
    public CompletedExpectedParticipant(CompletedExpectedParticipant participant) {
        if (participant != null) {
            this.callsign = participant.getCallsign();
            this.completedNetId = participant.getCompletedNetId();
            this.completedNetParticipantId = participant.getCompletedNetParticipantId();
        }
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getCompletedNetParticipantId() {
        return completedNetParticipantId;
    }
    public void setCompletedNetParticipantId(String completedNetParticipantId) {
        this.completedNetParticipantId = completedNetParticipantId;
    }
    public String getCompletedNetId() {
        return completedNetId;
    }
    public void setCompletedNetId(String completedNetId) {
        this.completedNetId = completedNetId;
    }
}
