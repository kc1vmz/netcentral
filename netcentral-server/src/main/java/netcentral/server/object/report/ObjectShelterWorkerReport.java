package netcentral.server.object.report;

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

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

public class ObjectShelterWorkerReport {
    private String callsign;
    private int shift;
    private int health;
    private int mental;
    private int spiritual;
    private int caseworker;
    private int feeding;
    private int other;
    private ZonedDateTime lastReportedTime;
    private String prettyLastReportedTime;

    public ObjectShelterWorkerReport() {
    }
    public ObjectShelterWorkerReport(String callsign, int shift, int health, int mental, int spiritual, int caseworker, int feeding, int other, 
                                                ZonedDateTime lastReportedTime) {
        setCallsign(callsign);
        setShift(shift);
        setHealth(health);
        setMental(mental);
        setSpiritual(spiritual);
        setCaseworker(caseworker);
        setFeeding(feeding);
        setOther(other);
        setLastReportedTime(lastReportedTime);
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public ZonedDateTime getLastReportedTime() {
        return lastReportedTime;
    }
    public void setLastReportedTime(ZonedDateTime lastReportedTime) {
        this.lastReportedTime = lastReportedTime;
        this.prettyLastReportedTime = PrettyZonedDateTimeFormatter.format(lastReportedTime);
    }
    public String getPrettyLastReportedTime() {
        return prettyLastReportedTime;
    }
    public void setPrettyLastReportedTime(String prettyLastReportedTime) {
        this.prettyLastReportedTime = prettyLastReportedTime;
    }
    public int getShift() {
        return shift;
    }
    public void setShift(int shift) {
        this.shift = shift;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public int getMental() {
        return mental;
    }
    public void setMental(int mental) {
        this.mental = mental;
    }
    public int getSpiritual() {
        return spiritual;
    }
    public void setSpiritual(int spiritual) {
        this.spiritual = spiritual;
    }
    public int getCaseworker() {
        return caseworker;
    }
    public void setCaseworker(int caseworker) {
        this.caseworker = caseworker;
    }
    public int getFeeding() {
        return feeding;
    }
    public void setFeeding(int feeding) {
        this.feeding = feeding;
    }
    public int getOther() {
        return other;
    }
    public void setOther(int other) {
        this.other = other;
    }
}

