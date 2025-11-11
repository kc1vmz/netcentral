package netcentral.server.object.report;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import netcentral.server.enums.ObjectShelterReportingTimeframe;

public class ObjectShelterOperationalFoodReport {
    private String callsign;
    private ObjectShelterReportingTimeframe timeframe;
    private Integer breakfast;
    private Integer lunch;
    private Integer dinner;
    private Integer snack;
    private ZonedDateTime lastReportedTime;
    private String prettyLastReportedTime;

    public ObjectShelterOperationalFoodReport(){
    }
    public ObjectShelterOperationalFoodReport(String callsign, ObjectShelterReportingTimeframe timeframe, Integer breakfast, Integer lunch, Integer dinner, Integer snack, ZonedDateTime lastReportedTime) {
        setCallsign(callsign);
        setTimeframe(timeframe);
        setBreakfast(breakfast);
        setLunch(lunch);
        setDinner(dinner);
        setSnack(snack);
        setLastReportedTime(lastReportedTime);
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public ObjectShelterReportingTimeframe getTimeframe() {
        return timeframe;
    }
    public void setTimeframe(ObjectShelterReportingTimeframe timeframe) {
        this.timeframe = timeframe;
    }
    public Integer getBreakfast() {
        return breakfast;
    }
    public void setBreakfast(Integer breakfast) {
        this.breakfast = breakfast;
    }
    public Integer getLunch() {
        return lunch;
    }
    public void setLunch(Integer lunch) {
        this.lunch = lunch;
    }
    public Integer getDinner() {
        return dinner;
    }
    public void setDinner(Integer dinner) {
        this.dinner = dinner;
    }
    public Integer getSnack() {
        return snack;
    }
    public void setSnack(Integer snack) {
        this.snack = snack;
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

