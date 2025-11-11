package netcentral.server.object.report;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import netcentral.server.enums.ObjectShelterState;
import netcentral.server.enums.ObjectShelterStatus;

public class ObjectShelterStatusReport {
    private String callsign;
    private String description;
    private ObjectType type;
    private String lat;
    private String lon;
    private boolean alive;
    private ObjectShelterStatus status;
    private ObjectShelterState state;
    private String message;
    private ZonedDateTime lastReportedTime;
    private String prettyLastReportedTime;

    public ObjectShelterStatusReport() {
    }
    public ObjectShelterStatusReport(String callsign, String description, ObjectType type, String lat, String lon, boolean alive, 
                                                ObjectShelterStatus status, ObjectShelterState state, String message, 
                                                ZonedDateTime lastReportedTime) {
        setCallsign(callsign);
        setDescription(description);
        setType(type);
        setLat(lat);
        setLon(lon);
        setAlive(alive);
        setStatus(status);
        setState(state);
        setMessage(message);
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
    public ObjectShelterStatus getStatus() {
        return status;
    }
    public void setStatus(ObjectShelterStatus status) {
        this.status = status;
    }
    public ObjectShelterState getState() {
        return state;
    }
    public void setState(ObjectShelterState state) {
        this.state = state;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
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

