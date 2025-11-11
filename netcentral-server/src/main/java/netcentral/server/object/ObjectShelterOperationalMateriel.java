package netcentral.server.object;

import netcentral.server.enums.ObjectShelterReportingTimeframe;

public class ObjectShelterOperationalMateriel {
    private ObjectShelterReportingTimeframe timeframe;
    private Integer cots;
    private Integer blankets;
    private Integer comfort;
    private Integer cleanup;
    private Integer signage;
    private Integer other;


    public ObjectShelterOperationalMateriel() {
    }
    public ObjectShelterOperationalMateriel(ObjectShelterReportingTimeframe timeframe, Integer cots, 
                                                        Integer blankets, Integer comfort, Integer cleanup, Integer signage, Integer other) {
        setTimeframe(timeframe);
        setCots(cots);
        setBlankets(blankets);
        setComfort(comfort);
        setCleanup(cleanup);
        setSignage(signage);
        setOther(other);
    }

    public ObjectShelterReportingTimeframe getTimeframe() {
        return timeframe;
    }
    public void setTimeframe(ObjectShelterReportingTimeframe timeframe) {
        this.timeframe = timeframe;
    }
    public Integer getCots() {
        return cots;
    }
    public void setCots(Integer cots) {
        this.cots = cots;
    }
    public Integer getBlankets() {
        return blankets;
    }
    public void setBlankets(Integer blankets) {
        this.blankets = blankets;
    }
    public Integer getComfort() {
        return comfort;
    }
    public void setComfort(Integer comfort) {
        this.comfort = comfort;
    }
    public Integer getCleanup() {
        return cleanup;
    }
    public void setCleanup(Integer cleanup) {
        this.cleanup = cleanup;
    }
    public Integer getSignage() {
        return signage;
    }
    public void setSignage(Integer signage) {
        this.signage = signage;
    }
    public Integer getOther() {
        return other;
    }
    public void setOther(Integer other) {
        this.other = other;
    }
}

