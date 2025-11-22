package netcentral.server.object.update;

import netcentral.server.object.CallsignAce;

public class CallsignACEUpdatePayload {
    private String callsign;
    private String action;
    private CallsignAce object;

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
    public CallsignAce getObject() {
        return object;
    }
    public void setObject(CallsignAce object) {
        this.object = object;
    }
}
