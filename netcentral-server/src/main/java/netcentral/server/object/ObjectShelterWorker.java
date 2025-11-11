package netcentral.server.object;

import java.time.ZonedDateTime;

public class ObjectShelterWorker {
    private int shift;
    private int health;
    private int mental;
    private int spiritual;
    private int caseworker;
    private int feeding;
    private int other;

    public ObjectShelterWorker() {
    }
    public ObjectShelterWorker(String callsign, int shift, int health, int mental, int spiritual, int caseworker, int feeding, int other, 
                                                ZonedDateTime lastReportedTime) {
        setShift(shift);
        setHealth(health);
        setMental(mental);
        setSpiritual(spiritual);
        setCaseworker(caseworker);
        setFeeding(feeding);
        setOther(other);
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

