package netcentral.server.object.update;

import netcentral.server.object.Participant;

public class NetParticipantUpdatePayload {
    private String callsign;
    private String action;
    private Participant object;

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
    public Participant getObject() {
        return object;
    }
    public void setObject(Participant object) {
        this.object = object;
    }
}
