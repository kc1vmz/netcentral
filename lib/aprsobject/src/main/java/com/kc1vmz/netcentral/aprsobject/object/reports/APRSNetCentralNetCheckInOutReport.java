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

public class APRSNetCentralNetCheckInOutReport extends APRSNetCentralReport {

    private String callsign;
    private boolean checkIn;

    public APRSNetCentralNetCheckInOutReport(){
        super();
    }
    public APRSNetCentralNetCheckInOutReport(String objectName, String callsign, boolean checkIn) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET);
        this.setReportType((checkIn) ? APRSNetCentralReportConstants.REPORT_TYPE_NET_CHECKIN : APRSNetCentralReportConstants.REPORT_TYPE_NET_CHECKOUT);
        this.setReportData(callsign);
        this.setCheckIn(checkIn);
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public boolean isCheckIn() {
        return checkIn;
    }
    public void setCheckIn(boolean checkIn) {
        this.checkIn = checkIn;
    }

    public static APRSNetCentralNetCheckInOutReport isValid(String objectName, String message) {
        APRSNetCentralNetCheckInOutReport ret = null;
        if (message != null) {
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_NET_CHECKIN)) {
                    String remainder = message.substring(4);
                    try {
                        ret = new APRSNetCentralNetCheckInOutReport(objectName, remainder, true);
                    } catch (Exception e) {
                        ret = null;
                    }
                } else if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_NET_CHECKOUT)) {
                    String remainder = message.substring(4);
                    try {
                        ret = new APRSNetCentralNetCheckInOutReport(objectName, remainder, false);
                    } catch (Exception e) {
                        ret = null;
                    }
                } 
            }
        }
        return ret;
    }
}

