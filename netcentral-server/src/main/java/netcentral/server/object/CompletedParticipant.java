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

@Serdeable
public class CompletedParticipant {
    private String callsign;
    private String completedNetParticipantId;
    private String completedNetId;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private String prettyStartTime;
    private String prettyEndTime;
    private ElectricalPowerType electricalPowerType;
    private RadioStyle radioStyle;
    private int transmitPower;
    private String tacticalCallsign;

    public CompletedParticipant() {
    }
    public CompletedParticipant(String callsign, String completedNetId, String completedNetParticipantId, ZonedDateTime startTime, ZonedDateTime endTime,
                                ElectricalPowerType electricalPowerType, RadioStyle radioStyle, int transmitPower, String tacticalCallsign) {
        this.callsign = callsign;
        this.completedNetId = completedNetId;
        this.completedNetParticipantId = completedNetParticipantId;
        setStartTime(startTime);
        setEndTime(endTime);
        this.electricalPowerType = electricalPowerType;
        this.radioStyle = radioStyle;
        this.transmitPower = transmitPower;
        this.tacticalCallsign = tacticalCallsign;
    }
    public CompletedParticipant(CompletedParticipant participant) {
        if (participant != null) {
            this.callsign = participant.getCallsign();
            this.completedNetId = participant.getCompletedNetId();
            this.completedNetParticipantId = participant.getCompletedNetParticipantId();
            setStartTime(participant.getStartTime());
            setEndTime(participant.getEndTime());
            this.electricalPowerType = participant.getElectricalPowerType();
            this.radioStyle = participant.getRadioStyle();
            this.transmitPower = participant.getTransmitPower();
            this.tacticalCallsign = participant.getTacticalCallsign();
        }
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public ZonedDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
        this.prettyStartTime = PrettyZonedDateTimeFormatter.format(startTime);
    }
    public ZonedDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
        this.prettyEndTime = PrettyZonedDateTimeFormatter.format(endTime);
    }
    public String getCompletedNetParticipantId() {
        return completedNetParticipantId;
    }
    public void setCompletedNetParticipantId(String completedNetParticipantId) {
        this.completedNetParticipantId = completedNetParticipantId;
    }
    public String getCompletedNetId() {
        return completedNetId;
    }
    public void setCompletedNetId(String completedNetId) {
        this.completedNetId = completedNetId;
    }
    public String getPrettyStartTime() {
        return prettyStartTime;
    }
    public void setPrettyStartTime(String prettyStartTime) {
        this.prettyStartTime = prettyStartTime;
    }
    public String getPrettyEndTime() {
        return prettyEndTime;
    }
    public void setPrettyEndTime(String prettyEndTime) {
        this.prettyEndTime = prettyEndTime;
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
    public String getTacticalCallsign() {
        return tacticalCallsign;
    }
    public void setTacticalCallsign(String tacticalCallsign) {
        this.tacticalCallsign = tacticalCallsign;
    }
}
