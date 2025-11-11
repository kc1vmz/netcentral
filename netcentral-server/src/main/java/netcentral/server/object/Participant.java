package netcentral.server.object;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import io.micronaut.serde.annotation.Serdeable;
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.RadioStyle;

@Serdeable
public class Participant {
    private String callsign;
    private String status;
    private String voiceFrequency;
    private ZonedDateTime startTime;
    private String lat; 
    private String lon; 
    private String prettyStartTime;
    private ElectricalPowerType electricalPowerType;
    private ElectricalPowerType backupElectricalPowerType;
    private RadioStyle radioStyle;
    private int transmitPower;
    private String tacticalCallsign;
    private ZonedDateTime lastHeardTime;
    private String prettyLastHeardTime;

    public Participant() {
    }
    public Participant(String callsign, String status, String voiceFrequency, ZonedDateTime startTime, String lat, String lon,
                                ElectricalPowerType electricalPowerType,  ElectricalPowerType backupElectricalPowerType, RadioStyle radioStyle, int transmitPower, String tacticalCallsign,
                                ZonedDateTime lastHeardTime) {
        this.callsign = callsign;
        this.status = status;
        this.voiceFrequency = voiceFrequency;
        this.setStartTime(startTime);
        this.lat = lat;
        this.lon = lon;
        this.electricalPowerType = electricalPowerType;
        this.backupElectricalPowerType = backupElectricalPowerType;
        this.radioStyle = radioStyle;
        this.transmitPower = transmitPower;
        this.tacticalCallsign = tacticalCallsign;
        this.setLastHeardTime(lastHeardTime);
    }
    public Participant(Participant participant) {
        if (participant != null) {
            this.callsign = participant.getCallsign();
            this.status = participant.getStatus();
            this.voiceFrequency = participant.getVoiceFrequency();
            this.setStartTime(participant.getStartTime());
            this.lon = participant.getLon();
            this.lat = participant.getLat();
            this.electricalPowerType = participant.getElectricalPowerType();
            this.backupElectricalPowerType = participant.getBackupElectricalPowerType();
            this.radioStyle = participant.getRadioStyle();
            this.transmitPower = participant.getTransmitPower();
            this.tacticalCallsign = participant.getTacticalCallsign();
            this.setLastHeardTime(participant.getLastHeardTime());
        }
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getVoiceFrequency() {
        return voiceFrequency;
    }
    public void setVoiceFrequency(String voiceFrequency) {
        this.voiceFrequency = voiceFrequency;
    }
    public ZonedDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
        this.prettyStartTime = PrettyZonedDateTimeFormatter.format(startTime);
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
    public String getPrettyStartTime() {
        return prettyStartTime;
    }
    public void setPrettyStartTime(String prettyStartTime) {
        this.prettyStartTime = prettyStartTime;
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
    public ElectricalPowerType getBackupElectricalPowerType() {
        return backupElectricalPowerType;
    }
    public void setBackupElectricalPowerType(ElectricalPowerType backupElectricalPowerType) {
        this.backupElectricalPowerType = backupElectricalPowerType;
    }
    public ZonedDateTime getLastHeardTime() {
        return lastHeardTime;
    }
    public void setLastHeardTime(ZonedDateTime lastHeardTime) {
        this.lastHeardTime = lastHeardTime;
        this.prettyLastHeardTime = PrettyZonedDateTimeFormatter.format(lastHeardTime);
    }
    public String getPrettyLastHeardTime() {
        return prettyLastHeardTime;
    }
    public void setPrettyLastHeardTime(String prettyLastHeardTime) {
        this.prettyLastHeardTime = prettyLastHeardTime;
    } 
}
