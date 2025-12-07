package netcentral.server.object.update;

import netcentral.server.object.ExpectedParticipant;

public class NetExpectedParticipantUpdatePayload {
    private String callsign;
    private String action;
    private ExpectedParticipant object;

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public ExpectedParticipant getObject() {
        return object;
    }
    public void setObject(ExpectedParticipant object) {
        this.object = object;
    }
}
