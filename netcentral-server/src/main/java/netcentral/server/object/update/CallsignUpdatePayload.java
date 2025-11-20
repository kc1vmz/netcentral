package netcentral.server.object.update;

import netcentral.server.object.Callsign;

public class CallsignUpdatePayload {
    private String callsign;
    private String action;
    private Callsign object;

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
    public Callsign getObject() {
        return object;
    }
    public void setObject(Callsign object) {
        this.object = object;
    }
}
