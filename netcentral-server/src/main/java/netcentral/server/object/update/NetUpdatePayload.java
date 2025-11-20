package netcentral.server.object.update;

import netcentral.server.object.Net;
import netcentral.server.object.ScheduledNet;

public class NetUpdatePayload {
    private String id;
    private String callsign;
    private String action;
    private Net objectNow;
    private ScheduledNet objectScheduled;
    private boolean scheduled;

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
    public Net getObjectNow() {
        return objectNow;
    }
    public void setObjectNow(Net object) {
        this.objectNow = object;
    }
    public ScheduledNet getObjectScheduled() {
        return objectScheduled;
    }
    public void setObjectScheduled(ScheduledNet objectScheduled) {
        this.objectScheduled = objectScheduled;
    }
    public boolean isScheduled() {
        return scheduled;
    }
    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
