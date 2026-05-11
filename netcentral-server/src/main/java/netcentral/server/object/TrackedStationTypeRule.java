package netcentral.server.object;

import netcentral.server.enums.TrackedStationType;
import netcentral.server.enums.TrackedStationTypeRuleTarget;
import netcentral.server.enums.TrackedStationTypeRuleType;

/*
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

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

public class TrackedStationTypeRule {
    private String id;
    private TrackedStationTypeRuleType ruleType;
    private String value;
    private TrackedStationType trackedStationType;
    private TrackedStationTypeRuleTarget ruleTarget;

    public TrackedStationTypeRule(String id, TrackedStationTypeRuleTarget ruleTarget, TrackedStationTypeRuleType ruleType, String value, TrackedStationType trackedStationType) {
        this.id = id;
        this.ruleTarget = ruleTarget;
        this.ruleType = ruleType;
        this.value = value;
        this.trackedStationType = trackedStationType;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public TrackedStationTypeRuleType getRuleType() {
        return ruleType;
    }
    public void setRuleType(TrackedStationTypeRuleType ruleType) {
        this.ruleType = ruleType;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public TrackedStationType getTrackedStationType() {
        return trackedStationType;
    }
    public void setTrackedStationType(TrackedStationType trackedStationType) {
        this.trackedStationType = trackedStationType;
    }
    public TrackedStationTypeRuleTarget getRuleTarget() {
        return ruleTarget;
    }
    public void setRuleTarget(TrackedStationTypeRuleTarget ruleTarget) {
        this.ruleTarget = ruleTarget;
    }
}
