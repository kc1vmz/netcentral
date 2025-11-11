package netcentral.server.object;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;

import netcentral.server.enums.ObjectEOCStatus;


public class ObjectEOCMobilization {
    private String eocName;
    private ObjectEOCStatus status;
    private Integer level;

    public ObjectEOCMobilization() {
    }
    public ObjectEOCMobilization(String callsign, String eocName, String description, ObjectType type, String lat, String lon, boolean alive, ObjectEOCStatus status, int level, ZonedDateTime lastReportedTime) {
        setEocName(eocName);
        setStatus(status);
        setLevel(level);
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
    public String getEocName() {
        return eocName;
    }
    public void setEocName(String eocName) {
        this.eocName = eocName;
    }
}

