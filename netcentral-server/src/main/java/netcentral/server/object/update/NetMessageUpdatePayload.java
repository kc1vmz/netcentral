package netcentral.server.object.update;

import netcentral.server.object.NetMessage;

public class NetMessageUpdatePayload {
    private String callsign;
    private String action;
    private NetMessage object;

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
    public NetMessage getObject() {
        return object;
    }
    public void setObject(NetMessage object) {
        this.object = object;
    }
}
