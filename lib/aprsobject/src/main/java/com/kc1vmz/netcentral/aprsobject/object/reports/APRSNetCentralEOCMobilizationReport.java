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

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

public class APRSNetCentralEOCMobilizationReport extends APRSNetCentralReport {
    public final static int STATUS_TYPE_UNKNOWN = 0;
    public final static int STATUS_TYPE_NORMAL = 1;
    public final static int STATUS_TYPE_DRILL = 2;
    public final static int STATUS_TYPE_PARTIAL = 3;
    public final static int STATUS_TYPE_FULL = 4;

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
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_EOC);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_MOBILIZATION);
        if ((status < STATUS_TYPE_NORMAL) || (status > STATUS_TYPE_FULL)) {
            status = STATUS_TYPE_UNKNOWN;
        }
        if ((level < 1) || (level > 5)) {
            level = 0;
        }
        String data = String.format("%d%d%s%s", status, level,  PrettyZonedDateTimeFormatter.formatAPRSReport(lastReportedTime), 
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
        if ((message != null) && (message.length() >= 20 )) {
            //"EOMO%d%d%14s%s", status, level, time, eocName);
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_EOC)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_MOBILIZATION)) {
                    String status = message.substring(4, 5);
                    String level = message.substring(5, 6);
                    String timeStr = message.substring(6, 20);
                    String eocName = "";
                    if (message.length() > 20) {
                        eocName = message.substring(20);
                    }

                    try {
                        ZonedDateTime time = PrettyZonedDateTimeFormatter.fromAPRSReport(timeStr);
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

