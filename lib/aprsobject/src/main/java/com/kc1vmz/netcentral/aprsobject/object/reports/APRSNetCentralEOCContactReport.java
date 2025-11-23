package com.kc1vmz.netcentral.aprsobject.object.reports;

import java.time.ZonedDateTime;

public class APRSNetCentralEOCContactReport extends APRSNetCentralReport {

    private String directorName;
    private String incidentCommanderName;
    private ZonedDateTime lastReportedTime;

    public APRSNetCentralEOCContactReport(){
        super();
    }
    public APRSNetCentralEOCContactReport(String objectName, String directorName, String incidentCommanderName, ZonedDateTime lastReportedTime) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType("EO"); // EOC
        this.setReportType("CO"); // contact
        String data = String.format("%s:%s:%4d%02d%02d",
                                            (directorName.length() > 25) ? directorName.substring(0, 25) : directorName, 
                                            (incidentCommanderName.length() > 25) ? incidentCommanderName.substring(0, 25) : incidentCommanderName, 
                                            lastReportedTime.getYear(), lastReportedTime.getMonthValue(), lastReportedTime.getDayOfMonth());
        this.setReportData(data);
        this.setDirectorName(directorName);
        this.setIncidentCommanderName(incidentCommanderName);

        this.lastReportedTime = lastReportedTime;
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
    }
    public static APRSNetCentralEOCContactReport isValid(String objectName, String message) {
        APRSNetCentralEOCContactReport ret = null;
        if ((message != null) && (message.length() >= 14)) {
            //"EOCO%s:%s:%4d%02d%02d", directorName, incidentCommanderName.length(), lastReportedTime.getYear(), lastReportedTime.getMonthValue(), lastReportedTime.getDayOfMonth());
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase("EO")) {
                if (reportType.equalsIgnoreCase("CO")) {
                    String remainder = message.substring(4);
                    String [] varFields = remainder.split(":");
                    if ((varFields == null) || (varFields.length != 3)) {
                        return ret;
                    }

                    String directorName = varFields[0];
                    String incidentCommanderName = varFields[1];
                    String year = varFields[2].substring(0, 4);
                    String month = varFields[2].substring(4, 6);
                    String day = varFields[2].substring(6);

                    try {
                        ZonedDateTime time = ZonedDateTime.now().withYear(Integer.parseInt(year)).withMonth(Integer.parseInt(month)).withDayOfMonth(Integer.parseInt(day)).withHour(0).withMinute(0);
                        ret = new APRSNetCentralEOCContactReport(objectName, directorName, incidentCommanderName, time);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}

