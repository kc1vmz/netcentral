package com.kc1vmz.netcentral.aprsobject.object.reports;

import java.time.ZonedDateTime;

public class APRSNetControlShelterOperationalMaterielReport extends APRSNetControlReport {
    private int timePeriod;
    private int cots;
    private int blankets; 
    private int comfort; 
    private int cleanup; 
    private int signage;
    private int other;
    private ZonedDateTime date;
    private ZonedDateTime dateReported;


    public APRSNetControlShelterOperationalMaterielReport(){
        super();
    }
    public APRSNetControlShelterOperationalMaterielReport(String objectName, int timePeriod, int cots, int blankets, int comfort, int cleanup, int signage, int other, ZonedDateTime date, ZonedDateTime dateReported) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType("SH"); // shelter
        this.setReportType("OM"); // operational - food
        if ((timePeriod < 1) || (timePeriod > 3)) {
            timePeriod = 0;
        }
        String data = String.format("%d%06d%06d%06d%06d%06d%06d%04d%02d%02d", timePeriod, cots, blankets, comfort, cleanup, signage, other, 
                                        dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth());
        this.setReportData(data);

        this.timePeriod = timePeriod;
        this.cots = cots;
        this.blankets = blankets; 
        this.comfort = comfort; 
        this.cleanup = cleanup; 
        this.signage = signage;
        this.other = other;
        this.date = date;
        this.dateReported = dateReported;
    }
    public int getTimePeriod() {
        return timePeriod;
    }
    public void setTimePeriod(int timePeriod) {
        this.timePeriod = timePeriod;
    }
    public int getCots() {
        return cots;
    }
    public void setCots(int cots) {
        this.cots = cots;
    }
    public int getBlankets() {
        return blankets;
    }
    public void setBlankets(int blankets) {
        this.blankets = blankets;
    }
    public int getComfort() {
        return comfort;
    }
    public void setComfort(int comfort) {
        this.comfort = comfort;
    }
    public int getCleanup() {
        return cleanup;
    }
    public void setCleanup(int cleanup) {
        this.cleanup = cleanup;
    }
    public int getSignage() {
        return signage;
    }
    public void setSignage(int signage) {
        this.signage = signage;
    }
    public int getOther() {
        return other;
    }
    public void setOther(int other) {
        this.other = other;
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
    public static APRSNetControlShelterOperationalMaterielReport isValid(String objectName, String message) {
        APRSNetControlShelterOperationalMaterielReport ret = null;
        if ((message != null) && (message.length() == 49)) {
            //"SHOM%d%06d%06d%06d%06d%06d%06d%04d%02d%02d",timePeriod, cots, blankets, comfort, cleanup, signage, other, dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth());
            
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase("SH")) {
                if (reportType.equalsIgnoreCase("OM")) {
                    String timeframe = message.substring(4, 5);
                    String cots = message.substring(5, 11);
                    String blankets = message.substring(11, 17);
                    String comfort = message.substring(17, 23);
                    String cleanup = message.substring(23, 29);
                    String signage = message.substring(29, 35);
                    String other = message.substring(35, 41);
                    String year = message.substring(41, 45);
                    String month = message.substring(45, 47);
                    String day = message.substring(47);

                    try {
                        ZonedDateTime time = ZonedDateTime.now().withYear(Integer.parseInt(year)).withMonth(Integer.parseInt(month)).withDayOfMonth(Integer.parseInt(day)).withHour(0).withMinute(0);
                        ret = new APRSNetControlShelterOperationalMaterielReport(objectName, Integer.parseInt(timeframe), Integer.parseInt(cots), Integer.parseInt(blankets),
                                                                        Integer.parseInt(comfort), Integer.parseInt(cleanup), Integer.parseInt(signage), Integer.parseInt(other), time, ZonedDateTime.now());
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}
