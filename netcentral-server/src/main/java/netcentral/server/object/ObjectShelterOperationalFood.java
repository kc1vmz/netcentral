package netcentral.server.object;

import netcentral.server.enums.ObjectShelterReportingTimeframe;

public class ObjectShelterOperationalFood {
    private ObjectShelterReportingTimeframe timeframe;
    private Integer breakfast;
    private Integer lunch;
    private Integer dinner;
    private Integer snack;

    public ObjectShelterOperationalFood(){
    }
    public ObjectShelterOperationalFood(ObjectShelterReportingTimeframe timeframe, Integer breakfast, Integer lunch, Integer dinner, Integer snack) {
        setTimeframe(timeframe);
        setBreakfast(breakfast);
        setLunch(lunch);
        setDinner(dinner);
        setSnack(snack);
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
}

