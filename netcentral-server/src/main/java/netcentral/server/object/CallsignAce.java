package netcentral.server.object;

import io.micronaut.serde.annotation.Serdeable;
import netcentral.server.enums.CallsignType;

@Serdeable
public class CallsignAce {
    private String id;
    private String callsignTarget;
    private String callsignChecked;
    private CallsignType type;
    private boolean allowed;
    private Integer proximity;

    public CallsignAce() {
    }
    public CallsignAce(String id, String callsignTarget, String callsignChecked, CallsignType type, boolean allowed, Integer proximity) {
        setId(id);
        setCallsignTarget(callsignTarget);
        setCallsignChecked(callsignChecked);
        setType(type);
        setAllowed(allowed);
        setProximity(proximity);
    }
    public CallsignAce(CallsignAce callsignAcl) {
        if (callsignAcl != null) {
            setId(callsignAcl.getId());
            setCallsignTarget(callsignAcl.getCallsignTarget());
            setCallsignChecked(callsignAcl.getCallsignChecked());
            setType(callsignAcl.getType());
            setAllowed(callsignAcl.isAllowed());
            setProximity(callsignAcl.getProximity());
        }
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCallsignTarget() {
        return callsignTarget;
    }
    public void setCallsignTarget(String callsignTarget) {
        this.callsignTarget = callsignTarget;
    }
    public String getCallsignChecked() {
        return callsignChecked;
    }
    public void setCallsignChecked(String callsignChecked) {
        this.callsignChecked = callsignChecked;
    }
    public CallsignType getType() {
        return type;
    }
    public void setType(CallsignType type) {
        this.type = type;
    }
    public boolean isAllowed() {
        return allowed;
    }
    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
    public Integer getProximity() {
        return proximity;
    }
    public void setProximity(Integer proximity) {
        this.proximity = proximity;
    }
}
