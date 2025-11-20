package netcentral.server.object.update;

import netcentral.server.object.TrackedStation;

public class TrackedStationUpdatePayload {
    private String callsign;
    private String action;
    private TrackedStation object;

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
    public TrackedStation getObject() {
        return object;
    }
    public void setObject(TrackedStation object) {
        this.object = object;
    }
}
