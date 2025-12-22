package netcentral.server.object.update;

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
