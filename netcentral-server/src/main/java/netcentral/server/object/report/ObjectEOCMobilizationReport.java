package netcentral.server.object.report;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import netcentral.server.enums.ObjectEOCStatus;


public class ObjectEOCMobilizationReport {
    private String callsign;
    private String eocName;
    private String description;
    private ObjectType type;
    private String lat;
    private String lon;
    private boolean alive;
    private ObjectEOCStatus status;
    private Integer level;
    private ZonedDateTime lastReportedTime;
    private String prettyLastReportedTime;

    public ObjectEOCMobilizationReport() {
        type = ObjectType.UNKNOWN;
    }
    public ObjectEOCMobilizationReport(String callsign, String eocName, String description, ObjectType type, String lat, String lon, boolean alive, ObjectEOCStatus status, int level, ZonedDateTime lastReportedTime) {
        setCallsign(callsign);
        setEocName(eocName);
        setDescription(description);
        setType(type);
        setLat(lat);
        setLon(lon);
        setAlive(alive);
        setStatus(status);
        setLevel(level);
        setLastReportedTime(lastReportedTime);
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
    public ObjectType getType() {
        return type;
    }
    public void setType(ObjectType type) {
        this.type = type;
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
    public boolean isAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public ObjectEOCStatus getStatus() {
        return status;
    }
    public void setStatus(ObjectEOCStatus status) {
        this.status = status;
    }
    public Integer getLevel() {
        return level;
    }
    public void setLevel(Integer level) {
        this.level = level;
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
    public String getEocName() {
        return eocName;
    }
    public void setEocName(String eocName) {
        this.eocName = eocName;
    }
}

