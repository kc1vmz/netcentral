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

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Anomaly {
    private String id;
    private String callsign;
    private String packetText;
    private String description;
    private ZonedDateTime time;
    private String prettyTime;

    public Anomaly() {
    }
    public Anomaly(String id, String callsign, String packetText, String description, ZonedDateTime time) {
        this.id = id;
        this.callsign = callsign;
        this.packetText = packetText;
        this.description = description;
        setTime(time);
    }
    public Anomaly(Anomaly anomaly) {
        if (anomaly != null) {
            this.id = anomaly.getId();
            this.callsign = anomaly.getCallsign();
            this.description = anomaly.getDescription();
            this.packetText = anomaly.getPacketText();
            setTime(anomaly.getTime());
        }
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPacketText() {
        return packetText;
    }
    public void setPacketText(String packetText) {
        this.packetText = packetText;
    }
    public ZonedDateTime getTime() {
        return time;
    }
    public void setTime(ZonedDateTime time) {
        this.time = time;
        this.prettyTime = PrettyZonedDateTimeFormatter.format(time);
    }
    public String getPrettyTime() {
        return prettyTime;
    }
    public void setPrettyTime(String prettyTime) {
        this.prettyTime = prettyTime;
    }
}
