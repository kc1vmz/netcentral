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

import java.time.ZonedDateTime;
import java.util.List;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import io.micronaut.serde.annotation.Serdeable;
import netcentral.server.enums.TrackedStationType;
import netcentral.server.utils.TrackedStationTypeUtils;

@Serdeable
public class IgnoreStation {
    private String callsign;
    private ZonedDateTime ignoreStartTime;
    private String prettyIgnoreStartTime;
    private String lon;
    private String lat;
    private List<TrackedStationType> types;
    private String prettyTypes;

    public IgnoreStation() {
    }
    public IgnoreStation(String callsign, ZonedDateTime ignoreStartTime, List<TrackedStationType> types, String lat, String lon) {
        this.callsign = callsign;
        this.setIgnoreStartTime(ignoreStartTime);
        setTypes(types);
        this.lat = lat;
        this.lon = lon;
    }
    public IgnoreStation(IgnoreStation ignoreStation) {
        if (ignoreStation != null) {
            this.callsign = ignoreStation.getCallsign();
            this.setIgnoreStartTime(ignoreStation.getIgnoreStartTime());
            setTypes(ignoreStation.getTypes());
            this.lat = ignoreStation.getLat();
            this.lon = ignoreStation.getLon();
        }
    }
    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public ZonedDateTime getIgnoreStartTime() {
        return ignoreStartTime;
    }
    public void setIgnoreStartTime(ZonedDateTime ignoreStartTime) {
        this.ignoreStartTime = ignoreStartTime;
        this.prettyIgnoreStartTime = PrettyZonedDateTimeFormatter.format(ignoreStartTime);
    }
    public String getPrettyIgnoreStartTime() {
        return prettyIgnoreStartTime;
    }
    public void setPrettyIgnoreStartTime(String prettyIgnoreStartTime) {
        this.prettyIgnoreStartTime = prettyIgnoreStartTime;
    }
    public String getLon() {
        return lon;
    }
    public void setLon(String lon) {
        this.lon = lon;
    }
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public List<TrackedStationType> getTypes() {
        return types;
    }
    public void setTypes(List<TrackedStationType> types) {
        this.types = types;
        setPrettyTypes(TrackedStationTypeUtils.getPrettyTypes(this.types));
    }
    public String getPrettyTypes() {
        return prettyTypes;
    }
    public void setPrettyTypes(String prettyTypes) {
        this.prettyTypes = prettyTypes;
    }
}
