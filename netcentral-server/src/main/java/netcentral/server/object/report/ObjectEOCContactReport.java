package netcentral.server.object.report;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;


public class ObjectEOCContactReport {
    private String callsign;
    private String description;
    private ObjectType type;
    private String lat;
    private String lon;
    private boolean alive;
    private String directorName;
    private String incidentCommanderName;
    private ZonedDateTime lastReportedTime;
    private String prettyLastReportedTime;

    public ObjectEOCContactReport() {
        type = ObjectType.UNKNOWN;
    }
    public ObjectEOCContactReport(String callsign, String description, ObjectType type, String lat, String lon, boolean alive, String directorName, String incidentCommanderName,  ZonedDateTime lastReportedTime) {
        setCallsign(callsign);
        setDescription(description);
        setType(type);
        setLat(lat);
        setLon(lon);
        setAlive(alive);
        setDirectorName(directorName);
        setIncidentCommanderName(incidentCommanderName);
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
    public String getDirectorName() {
        return directorName;
    }
    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }
    public String getIncidentCommanderName() {
        return incidentCommanderName;
    }
    public void setIncidentCommanderName(String incidentCommanderName) {
        this.incidentCommanderName = incidentCommanderName;
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
}
