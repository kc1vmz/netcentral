package com.kc1vmz.netcentral.aprsobject.object.reports;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

import java.time.ZonedDateTime;

public class APRSNetCentralShelterOperationalMaterielReport extends APRSNetCentralReport {
    public final static int REPORT_INFOTYPE_UNKNOWN = 0;
    public final static int REPORT_INFOTYPE_ONHAND = 1;
    public final static int REPORT_INFOTYPE_REQUIRE = 2;
    public final static int REPORT_INFOTYPE_USED = 3;

    private int infoType;
    private int cots;
    private int blankets; 
    private int comfort; 
    private int cleanup; 
    private int signage;
    private int other;
    private ZonedDateTime date;
    private ZonedDateTime dateReported;


    public APRSNetCentralShelterOperationalMaterielReport(){
        super();
    }
    public APRSNetCentralShelterOperationalMaterielReport(String objectName, int infoType, int cots, int blankets, int comfort, int cleanup, int signage, int other, ZonedDateTime date, ZonedDateTime dateReported) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_OPERATIONAL_MATERIEL);
        if ((infoType != REPORT_INFOTYPE_ONHAND) && (infoType != REPORT_INFOTYPE_REQUIRE) && (infoType != REPORT_INFOTYPE_USED)) {
            infoType = REPORT_INFOTYPE_UNKNOWN;
        }
        String data = String.format("%d%06d%06d%06d%06d%06d%06d%04d%02d%02d", infoType, cots, blankets, comfort, cleanup, signage, other, 
                                        dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth());
        this.setReportData(data);

        this.infoType = infoType;
        this.cots = cots;
        this.blankets = blankets; 
        this.comfort = comfort; 
        this.cleanup = cleanup; 
        this.signage = signage;
        this.other = other;
        this.date = date;
        this.dateReported = dateReported;
    }
    public int getInfoType() {
        return infoType;
    }
    public void setInfoType(int infoType) {
        this.infoType = infoType;
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
    public static APRSNetCentralShelterOperationalMaterielReport isValid(String objectName, String message) {
        APRSNetCentralShelterOperationalMaterielReport ret = null;
        if ((message != null) && (message.length() == 49)) {
            //"SHOM%d%06d%06d%06d%06d%06d%06d%04d%02d%02d",timePeriod, cots, blankets, comfort, cleanup, signage, other, dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth());
            
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_OPERATIONAL_MATERIEL)) {
                    String infoType = message.substring(4, 5);
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
                        ret = new APRSNetCentralShelterOperationalMaterielReport(objectName, Integer.parseInt(infoType), Integer.parseInt(cots), Integer.parseInt(blankets),
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
