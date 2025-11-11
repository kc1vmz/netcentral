package netcentral.server.object.report;

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;

import netcentral.server.enums.ObjectEOCStatus;

public class ObjectEOCConsolidatedReport {
    private String callsign;
    private String description;
    private ObjectType type;
    private String lat;
    private String lon;
    private boolean alive;
    private String directorName;
    private String indicentCommanderName;
    private ObjectEOCStatus status;
    private int level;

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
    public ObjectType getType() {
        return type;
    }
    public void setType(ObjectType type) {
        this.type = type;
    }
    public String getDirectorName() {
        return directorName;
    }
    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }
    public String getIndicentCommanderName() {
        return indicentCommanderName;
    }
    public void setIndicentCommanderName(String indicentCommanderName) {
        this.indicentCommanderName = indicentCommanderName;
    }
    public ObjectEOCStatus getStatus() {
        return status;
    }
    public void setStatus(ObjectEOCStatus status) {
        this.status = status;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
}

