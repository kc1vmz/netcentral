package com.kc1vmz.netcentral.aprsobject.object.reports;

import java.time.ZonedDateTime;

public class APRSNetControlShelterOperationalFoodReport extends APRSNetControlReport {
    private ZonedDateTime date;
    private ZonedDateTime dateReported;
    private int timePeriod;
    private int breakfast;
    private int lunch;
    private int dinner; 
    private int snack;


    public APRSNetControlShelterOperationalFoodReport(){
        super();
    }
    public APRSNetControlShelterOperationalFoodReport(String objectName, int timePeriod, int breakfast, int lunch, int dinner, int snack, ZonedDateTime date, ZonedDateTime dateReported) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType("SH"); // shelter
        this.setReportType("OF"); // operational - food
        if ((timePeriod < 1) || (timePeriod > 3)) {
            timePeriod = 0;
        }
        String data = String.format("%d%06d%06d%06d%06d%4d%02d%02d", timePeriod, breakfast, lunch, dinner, snack, 
                                        dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth());
        this.setReportData(data);

        this.date = date;
        this.dateReported = dateReported;
        this.timePeriod = timePeriod;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner; 
        this.snack = snack;
    }
    public ZonedDateTime getDate() {
        return date;
    }
    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
    public ZonedDateTime getDateReported() {
        return dateReported;
    }
    public void setDateReported(ZonedDateTime dateReported) {
        this.dateReported = dateReported;
    }
    public int getTimePeriod() {
        return timePeriod;
    }
    public void setTimePeriod(int timePeriod) {
        this.timePeriod = timePeriod;
    }
    public int getBreakfast() {
        return breakfast;
    }
    public void setBreakfast(int breakfast) {
        this.breakfast = breakfast;
    }
    public int getLunch() {
        return lunch;
    }
    public void setLunch(int lunch) {
        this.lunch = lunch;
    }
    public int getDinner() {
        return dinner;
    }
    public void setDinner(int dinner) {
        this.dinner = dinner;
    }
    public int getSnack() {
        return snack;
    }
    public void setSnack(int snack) {
        this.snack = snack;
    }
    public static APRSNetControlShelterOperationalFoodReport isValid(String objectName, String message) {
        APRSNetControlShelterOperationalFoodReport ret = null;
        if ((message != null) && (message.length() == 37)) {
            //"SHOF%d%06d%06d%06d%06d%4d%02d%02d", timePeriod, breakfast, lunch, dinner, snack, dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth()
            
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase("SH")) {
                if (reportType.equalsIgnoreCase("OF")) {
                    String timeframe = message.substring(4, 5);
                    String breakfast = message.substring(5, 11);
                    String lunch = message.substring(11, 17);
                    String dinner = message.substring(17, 23);
                    String snack = message.substring(23, 29);
                    String year = message.substring(29, 33);
                    String month = message.substring(33, 35);
                    String day = message.substring(35);

                    try {
                        ZonedDateTime time = ZonedDateTime.now().withYear(Integer.parseInt(year)).withMonth(Integer.parseInt(month)).withDayOfMonth(Integer.parseInt(day)).withHour(0).withMinute(0);
                        ret = new APRSNetControlShelterOperationalFoodReport(objectName, Integer.parseInt(timeframe), Integer.parseInt(breakfast), Integer.parseInt(lunch),
                                                                        Integer.parseInt(dinner), Integer.parseInt(snack), time, ZonedDateTime.now());
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }

}
