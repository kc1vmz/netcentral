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
    private String recipient;
    public final static String RECIPIENT_ALL = "A";
    public final static String RECIPIENT_NET_CONTROL = "C";

    public APRSNetCentralNetMessageReport(){
        super();
    }
    public APRSNetCentralNetMessageReport(String objectName, boolean all, String messageText) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_NET_MESSAGE);
        this.setMessageText(messageText);
        if (all) {
            this.setRecipient(RECIPIENT_ALL);
        } else {
            this.setRecipient(RECIPIENT_NET_CONTROL);
        }
        this.setReportData(this.getRecipient()+messageText);
    }

    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public static APRSNetCentralNetMessageReport isValid(String objectName, String message) {
        APRSNetCentralNetMessageReport ret = null;
        if (message != null) {
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_NET_MESSAGE)) {
                    String remainder = message.substring(5);
                    String recipient = message.substring(4, 5);
                    boolean all = true;
                    if (recipient.equals(RECIPIENT_NET_CONTROL)) {
                        all = false;
                    }
                    try {
                        ret = new APRSNetCentralNetMessageReport(objectName, all, remainder);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}

