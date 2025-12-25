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

public class APRSNetCentralShelterStatusReport extends APRSNetCentralReport {
    private ZonedDateTime dateReported; 
    private int status;
    private int state;
    private String message;

    public APRSNetCentralShelterStatusReport(){
        super();
    }
    public APRSNetCentralShelterStatusReport(String objectName, int status, int state, String message, ZonedDateTime dateReported) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_STATUS);
        if ((status < 1) || (status > 2)) {
            status = 0;
        }
        if ((state < 1) || (state > 2)) {
            state = 0;
        }
        if ((message != null) && (message.length() > 40)) {
            // max 40 characters
            message = message.substring(0, 40);
        }
        String data = String.format("%d%d%s", state, status, message);
        this.setReportData(data);
        this.dateReported = dateReported;
        this.message = message;
        setState(state);
        setStatus(status);
    }
    public ZonedDateTime getDateReported() {
        return dateReported;
    }
    public void setDateReported(ZonedDateTime dateReported) {
        this.dateReported = dateReported;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public static APRSNetCentralShelterStatusReport isValid(String objectName, String message) {
        APRSNetCentralShelterStatusReport ret = null;
        if ((message != null) && (message.length() >= 6)) {
            //"SHST%d%d%s", state, status, message
            
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_STATUS)) {
                    String state = message.substring(4, 5);
                    String status = message.substring(5, 6);
                    String messageText = "";
                    if (message.length() > 6) {
                        messageText = message.substring(6);
                    }

                    try {
                        ZonedDateTime time = ZonedDateTime.now();
                        ret = new APRSNetCentralShelterStatusReport(objectName, Integer.parseInt(status),  Integer.parseInt(state), messageText, time);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}
