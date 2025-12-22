package netcentral.server.object;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

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
