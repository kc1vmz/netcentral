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

public class APRSNetCentralShelterWorkerReport extends APRSNetCentralReport {
    private int shift;
    private int health;
    private int mental;
    private int spiritual;
    private int caseworker;
    private int feeding;
    private int other;
    private ZonedDateTime dateReported;

    public APRSNetCentralShelterWorkerReport(){
        super();
    }
    public APRSNetCentralShelterWorkerReport(String objectName, int shift, int health, int mental, int spiritual, int caseworker, int feeding, int other, ZonedDateTime dateReported) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_WORKER_CENSUS);
        if ((shift < 1) || (shift > 3)) {
            shift = 0;
        }
        String data = String.format("%d%06d%06d%06d%06d%06d%06d%04d%02d%02d", shift, health, mental, spiritual, caseworker, feeding, other, dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth());
        this.setReportData(data);
        this.dateReported = dateReported;
        this.shift = shift;
        this.health = health;
        this.mental = mental;
        this.spiritual = spiritual;
        this.caseworker = caseworker;
        this.feeding = feeding;
        this.other = other;
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
    public ZonedDateTime getDateReported() {
        return dateReported;
    }
    public void setDateReported(ZonedDateTime dateReported) {
        this.dateReported = dateReported;
    }

    public static APRSNetCentralShelterWorkerReport isValid(String objectName, String message) {
        APRSNetCentralShelterWorkerReport ret = null;
        if ((message != null) && (message.length() == 49)) {
            //"SHWC%d%06d%06d%06d%06d%06d%06d%04d%02d%02d", shift, health, mental, spiritual, caseworker, feeding, other, dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth());
            
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_WORKER_CENSUS)) {
                    String shift = message.substring(4, 5);
                    String health = message.substring(5, 11);
                    String mental = message.substring(11, 17);
                    String spiritual = message.substring(17, 23);
                    String caseworker = message.substring(23, 29);
                    String feeding = message.substring(29, 35);
                    String other = message.substring(35, 41);
                    String year = message.substring(41, 45);
                    String month = message.substring(45, 47);
                    String day = message.substring(47);

                    try {
                        ZonedDateTime time = ZonedDateTime.now().withYear(Integer.parseInt(year)).withMonth(Integer.parseInt(month)).withDayOfMonth(Integer.parseInt(day)).withHour(0).withMinute(0);
                        ret = new APRSNetCentralShelterWorkerReport(objectName, Integer.parseInt(shift), Integer.parseInt(health), Integer.parseInt(mental), Integer.parseInt(spiritual),
                                                                        Integer.parseInt(caseworker), Integer.parseInt(feeding), Integer.parseInt(other), time);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }

}
