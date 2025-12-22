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
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.RadioStyle;
import netcentral.server.enums.TrackedStationStatus;
import netcentral.server.enums.TrackedStationType;

@Serdeable
public class TrackedStation {
    private String id;
    private String callsign;
    private String lat;
    private String lon;
    private String name;
    private String description;
    private String frequencyTx;
    private String frequencyRx;
    private String tone;
    private ZonedDateTime lastHeard;
    private boolean trackingActive;
    private TrackedStationStatus status;
    private String ipAddress;
    private TrackedStationType type;
    private String prettyLastHeard;
    private ElectricalPowerType electricalPowerType;
    private ElectricalPowerType backupElectricalPowerType;
    private RadioStyle radioStyle;
    private int transmitPower;

    public TrackedStation() {
        electricalPowerType = ElectricalPowerType.UNKNOWN;
        backupElectricalPowerType = ElectricalPowerType.UNKNOWN;
        radioStyle = RadioStyle.UNKNOWN;
        status = TrackedStationStatus.UNKNOWN;
        type = TrackedStationType.UNKNOWN;
    }

    public TrackedStation(String id, TrackedStationType type, String name, String description, String callsign, 
                                String lat, String lon,  String frequencyTx, 
                                String frequencyRx, String tone, ZonedDateTime lastHeard, 
                                boolean trackingActive, TrackedStationStatus status, String ipAddress,
                                ElectricalPowerType electricalPowerType, ElectricalPowerType backupElectricalPowerType, RadioStyle radioStyle, int transmitPower) {
        this.id = id;
        this.callsign = callsign;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.description = description;
        this.frequencyTx = frequencyTx;
        this.frequencyRx = frequencyRx;
        this.tone = tone;
        setLastHeard(lastHeard);
        this.trackingActive = trackingActive;
        this.status = status;
        this.ipAddress = ipAddress;
        this.type = type;
        this.electricalPowerType = electricalPowerType;
        this.backupElectricalPowerType = backupElectricalPowerType;
        this.radioStyle = radioStyle;
        this.transmitPower = transmitPower;

        if (electricalPowerType == null) electricalPowerType = ElectricalPowerType.UNKNOWN;
        if (backupElectricalPowerType == null) backupElectricalPowerType = ElectricalPowerType.UNKNOWN;
        if (radioStyle == null) radioStyle = RadioStyle.UNKNOWN;
        if (status == null) status = TrackedStationStatus.UNKNOWN;
        if (type == null) type = TrackedStationType.UNKNOWN;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLon() {
        return lon;
    }
    public void setLon(String lon) {
        this.lon = lon;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFrequencyTx() {
        return frequencyTx;
    }
    public void setFrequencyTx(String frequencyTx) {
        this.frequencyTx = frequencyTx;
    }
    public String getFrequencyRx() {
        return frequencyRx;
    }
    public void setFrequencyRx(String frequencyRx) {
        this.frequencyRx = frequencyRx;
    }
    public String getTone() {
        return tone;
    }
    public void setTone(String tone) {
        this.tone = tone;
    }
    public ZonedDateTime getLastHeard() {
        return lastHeard;
    }
    public void setLastHeard(ZonedDateTime lastHeard) {
        this.lastHeard = lastHeard;
        this.prettyLastHeard = PrettyZonedDateTimeFormatter.format(lastHeard);
    }
    public boolean isTrackingActive() {
        return trackingActive;
    }
    public void setTrackingActive(boolean trackingActive) {
        this.trackingActive = trackingActive;
    }
    public TrackedStationStatus getStatus() {
        return status;
    }
    public void setStatus(TrackedStationStatus status) {
        this.status = status;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public TrackedStationType getType() {
        return type;
    }
    public void setType(TrackedStationType type) {
        this.type = type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getPrettyLastHeard() {
        return prettyLastHeard;
    }
    public void setPrettyLastHeard(String prettyLastHeard) {
        this.prettyLastHeard = prettyLastHeard;
    }

    public ElectricalPowerType getElectricalPowerType() {
        return electricalPowerType;
    }

    public void setElectricalPowerType(ElectricalPowerType electricalPowerType) {
        this.electricalPowerType = electricalPowerType;
    }

    public RadioStyle getRadioStyle() {
        return radioStyle;
    }

    public void setRadioStyle(RadioStyle radioStyle) {
        this.radioStyle = radioStyle;
    }

    public int getTransmitPower() {
        return transmitPower;
    }

    public void setTransmitPower(int transmitPower) {
        this.transmitPower = transmitPower;
    }

    public ElectricalPowerType getBackupElectricalPowerType() {
        return backupElectricalPowerType;
    }

    public void setBackupElectricalPowerType(ElectricalPowerType backupElectricalPowerType) {
        this.backupElectricalPowerType = backupElectricalPowerType;
    }
}
