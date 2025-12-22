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

import netcentral.server.enums.ObjectShelterReportingTimeframe;

public class ObjectShelterOperationalMaterielReport {
    private String callsign;
    private ObjectShelterReportingTimeframe timeframe;
    private Integer cots;
    private Integer blankets;
    private Integer comfort;
    private Integer cleanup;
    private Integer signage;
    private Integer other;
    private ZonedDateTime lastReportedTime;
    private String prettyLastReportedTime;


    public ObjectShelterOperationalMaterielReport() {
    }
    public ObjectShelterOperationalMaterielReport(String callsign, ObjectShelterReportingTimeframe timeframe, Integer cots, 
                                                        Integer blankets, Integer comfort, Integer cleanup, Integer signage, Integer other, ZonedDateTime lastReportedTime) {
        setCallsign(callsign);
        setTimeframe(timeframe);
        setCots(cots);
        setBlankets(blankets);
        setComfort(comfort);
        setCleanup(cleanup);
        setSignage(signage);
        setOther(other);
        setLastReportedTime(lastReportedTime);
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public ObjectShelterReportingTimeframe getTimeframe() {
        return timeframe;
    }
    public void setTimeframe(ObjectShelterReportingTimeframe timeframe) {
        this.timeframe = timeframe;
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
    public void setPrettyLastReportedTime(String prettLastReportedTime) {
        this.prettyLastReportedTime = prettLastReportedTime;
    }
    public Integer getCots() {
        return cots;
    }
    public void setCots(Integer cots) {
        this.cots = cots;
    }
    public Integer getBlankets() {
        return blankets;
    }
    public void setBlankets(Integer blankets) {
        this.blankets = blankets;
    }
    public Integer getComfort() {
        return comfort;
    }
    public void setComfort(Integer comfort) {
        this.comfort = comfort;
    }
    public Integer getCleanup() {
        return cleanup;
    }
    public void setCleanup(Integer cleanup) {
        this.cleanup = cleanup;
    }
    public Integer getSignage() {
        return signage;
    }
    public void setSignage(Integer signage) {
        this.signage = signage;
    }
    public Integer getOther() {
        return other;
    }
    public void setOther(Integer other) {
        this.other = other;
    }
}

