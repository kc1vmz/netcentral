package netcentral.server.object.update;

import netcentral.server.object.NetQuestion;

public class NetQuestionUpdatePayload {
    private String callsign;
    private String action;
    private NetQuestion object;

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
    public NetQuestion getObject() {
        return object;
    }
    public void setObject(NetQuestion object) {
        this.object = object;
    }
}
