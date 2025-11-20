package netcentral.server.object.update;

import com.kc1vmz.netcentral.aprsobject.object.APRSObject;

public class ObjectUpdatePayload {
    private String callsign;
    private String action;
    private APRSObject object;

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
    public APRSObject getObject() {
        return object;
    }
    public void setObject(APRSObject object) {
        this.object = object;
    }
}
