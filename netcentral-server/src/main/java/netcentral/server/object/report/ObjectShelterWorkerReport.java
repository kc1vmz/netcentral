package netcentral.server.object.report;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

public class ObjectShelterWorkerReport {
    private String callsign;
    private int shift;
    private int health;
    private int mental;
    private int spiritual;
    private int caseworker;
    private int feeding;
    private int other;
    private ZonedDateTime lastReportedTime;
    private String prettyLastReportedTime;

    public ObjectShelterWorkerReport() {
    }
    public ObjectShelterWorkerReport(String callsign, int shift, int health, int mental, int spiritual, int caseworker, int feeding, int other, 
                                                ZonedDateTime lastReportedTime) {
        setCallsign(callsign);
        setShift(shift);
        setHealth(health);
        setMental(mental);
        setSpiritual(spiritual);
        setCaseworker(caseworker);
        setFeeding(feeding);
        setOther(other);
        setLastReportedTime(lastReportedTime);
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
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
    public int getShift() {
        return shift;
    }
    public void setShift(int shift) {
        this.shift = shift;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public int getMental() {
        return mental;
    }
    public void setMental(int mental) {
        this.mental = mental;
    }
    public int getSpiritual() {
        return spiritual;
    }
    public void setSpiritual(int spiritual) {
        this.spiritual = spiritual;
    }
    public int getCaseworker() {
        return caseworker;
    }
    public void setCaseworker(int caseworker) {
        this.caseworker = caseworker;
    }
    public int getFeeding() {
        return feeding;
    }
    public void setFeeding(int feeding) {
        this.feeding = feeding;
    }
    public int getOther() {
        return other;
    }
    public void setOther(int other) {
        this.other = other;
    }
}

