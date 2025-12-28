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

public class APRSNetCentralNetMessageReport extends APRSNetCentralReport {

    private String messageText;

    public APRSNetCentralNetMessageReport(){
        super();
    }
    public APRSNetCentralNetMessageReport(String objectName, String messageText) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_NET_MESSAGE);
        this.setReportData(messageText);
        this.setMessageText(messageText);
    }

    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    
    public static APRSNetCentralNetMessageReport isValid(String objectName, String message) {
        APRSNetCentralNetMessageReport ret = null;
        if (message != null) {
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_NET_MESSAGE)) {
                    String remainder = message.substring(4);
                    try {
                        ret = new APRSNetCentralNetMessageReport(objectName, remainder);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}

