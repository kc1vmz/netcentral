package com.kc1vmz.netcentral.aprsobject.object.reports;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

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

public class APRSNetCentralNetStartReport extends APRSNetCentralReport {

    private ZonedDateTime startTime;

    public APRSNetCentralNetStartReport(){
        super();
    }
    public APRSNetCentralNetStartReport(String objectName, ZonedDateTime startTime) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_NET_START);
        String data = PrettyZonedDateTimeFormatter.formatISO8601(startTime);
        this.setReportData(data);
        this.setStartTime(startTime);
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public static APRSNetCentralNetStartReport isValid(String objectName, String message) {
        APRSNetCentralNetStartReport ret = null;
        if (message != null) {
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_NET_START)) {
                    String remainder = message.substring(4);
                    ZonedDateTime startTime = ZonedDateTime.parse(remainder);
                    try {
                        ret = new APRSNetCentralNetStartReport(objectName, startTime);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}

