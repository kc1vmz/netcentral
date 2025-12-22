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

public class APRSNetCentralEOCMobilizationReport extends APRSNetCentralReport {

    private Integer status;
    private Integer level;
    private String eocName;
    private ZonedDateTime lastReportedTime;

    public APRSNetCentralEOCMobilizationReport(){
        super();
    }
    public APRSNetCentralEOCMobilizationReport(String objectName, String eocName, int status, int level, ZonedDateTime lastReportedTime) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType("EO"); // EOC
        this.setReportType("MO"); // mobilization
        if ((status < 1) || (status > 3)) {
            status = 0;
        }
        if ((level < 1) || (level > 5)) {
            level = 0;
        }
        String data = String.format("%d%d%4d%02d%02d%s", status, level, lastReportedTime.getYear(), lastReportedTime.getMonthValue(), lastReportedTime.getDayOfMonth(), 
                                        (eocName.length() > 30) ? eocName.substring(0, 30) : eocName);
        this.setReportData(data);
        this.setEocName(eocName);

        this.lastReportedTime = lastReportedTime;
        this.status = status;
        this.level = level;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getLevel() {
        return level;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }
    public ZonedDateTime getLastReportedTime() {
        return lastReportedTime;
    }
    public void setLastReportedTime(ZonedDateTime lastReportedTime) {
        this.lastReportedTime = lastReportedTime;
    }
    public String getEocName() {
        return eocName;
    }
    public void setEocName(String eocName) {
        this.eocName = eocName;
    }
    public static APRSNetCentralEOCMobilizationReport isValid(String objectName, String message) {
        APRSNetCentralEOCMobilizationReport ret = null;
        if ((message != null) && (message.length() >= 14 )) {
            //"EOMO%d%d%4d%02d%02d%s", status, level, lastReportedTime.getYear(), lastReportedTime.getMonthValue(), lastReportedTime.getDayOfMonth(),  eocName);
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase("EO")) {
                if (reportType.equalsIgnoreCase("MO")) {
                    String status = message.substring(4, 5);
                    String level = message.substring(5, 6);

                    String year = message.substring(6, 10);
                    String month = message.substring(10, 12);
                    String day = message.substring(12, 14);

                    String eocName = "";
                    if (message.length() > 14) {
                        eocName = message.substring(14);
                    }

                    try {
                        ZonedDateTime time = ZonedDateTime.now().withYear(Integer.parseInt(year)).withMonth(Integer.parseInt(month)).withDayOfMonth(Integer.parseInt(day)).withHour(0).withMinute(0);
                        ret = new APRSNetCentralEOCMobilizationReport(objectName, eocName, Integer.parseInt(status), Integer.parseInt(level), time);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}

