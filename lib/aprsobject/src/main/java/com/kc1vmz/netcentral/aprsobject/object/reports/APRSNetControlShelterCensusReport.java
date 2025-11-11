package com.kc1vmz.netcentral.aprsobject.object.reports;

import java.time.ZonedDateTime;

public class APRSNetControlShelterCensusReport extends APRSNetControlReport {
    private ZonedDateTime dateReported; 
    private int p03;
    private int p47;
    private int p812;
    private int p1318;
    private int p1965;
    private int p66;

    public APRSNetControlShelterCensusReport(){
        super();
    }
    public APRSNetControlShelterCensusReport(String objectName, int p03, int p47, int p812, int p1318, int p1965, int p66, ZonedDateTime dateReported) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType("SH"); // shelter
        this.setReportType("PC"); // Population census
        String data = String.format("%06d%06d%06d%06d%06d%06d%4d%02d%02d", p03, p47, p812, p1318, p1965, p66, 
                                        dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth());
        this.setReportData(data);
        this.p03 = p03;
        this.p47 = p47;
        this.p812 = p812;
        this.p1318 = p1318;
        this.p1965 = p1965;
        this.p66 = p66;
        this.dateReported = dateReported;
    }
    public int getP03() {
        return p03;
    }
    public void setP03(int p03) {
        this.p03 = p03;
    }
    public int getP47() {
        return p47;
    }
    public void setP47(int p47) {
        this.p47 = p47;
    }
    public int getP812() {
        return p812;
    }
    public void setP812(int p812) {
        this.p812 = p812;
    }
    public int getP1318() {
        return p1318;
    }
    public void setP1318(int p1318) {
        this.p1318 = p1318;
    }
    public int getP1965() {
        return p1965;
    }
    public void setP1965(int p1965) {
        this.p1965 = p1965;
    }
    public int getP66() {
        return p66;
    }
    public void setP66(int p66) {
        this.p66 = p66;
    }
    public ZonedDateTime getDateReported() {
        return dateReported;
    }
    public void setDateReported(ZonedDateTime dateReported) {
        this.dateReported = dateReported;
    }
    public static APRSNetControlShelterCensusReport isValid(String objectName, String message) {
        APRSNetControlShelterCensusReport ret = null;
        if ((message != null) && (message.length() == 48)) {
            //"SHPC%06d%06d%06d%06d%06d%06d%4d%02d%02d", p03, p47, p812, p1318, p1965, p66, dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth());
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase("SH")) {
                if (reportType.equalsIgnoreCase("PC")) {
                    String p03 = message.substring(4, 10);
                    String p47 = message.substring(10, 16);
                    String p812 = message.substring(16, 22);
                    String p1318 = message.substring(22, 28);
                    String p1965 = message.substring(28, 34);
                    String p66 = message.substring(34, 40);
                    String year = message.substring(40, 44);
                    String month = message.substring(44, 46);
                    String day = message.substring(46);

                    try {
                        ZonedDateTime time = ZonedDateTime.now().withYear(Integer.parseInt(year)).withMonth(Integer.parseInt(month)).withDayOfMonth(Integer.parseInt(day)).withHour(0).withMinute(0);
                        ret = new APRSNetControlShelterCensusReport(objectName, Integer.parseInt(p03), Integer.parseInt(p47), Integer.parseInt(p812),
                                             Integer.parseInt(p1318), Integer.parseInt(p1965), Integer.parseInt(p66), time);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}
