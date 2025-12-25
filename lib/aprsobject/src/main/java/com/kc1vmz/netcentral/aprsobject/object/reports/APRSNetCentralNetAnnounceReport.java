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

public class APRSNetCentralNetAnnounceReport extends APRSNetCentralReport {

    private String name;
    private String description;

    public APRSNetCentralNetAnnounceReport(){
        super();
    }
    public APRSNetCentralNetAnnounceReport(String objectName, String name, String description) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_NET_ANNOUNCE);
        String data = String.format("%s:%s", name, description);
        this.setReportData(data);
        this.setName(name);
        this.setDescription(description);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    public static APRSNetCentralNetAnnounceReport isValid(String objectName, String message) {
        APRSNetCentralNetAnnounceReport ret = null;
        if (message != null) {
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_NET)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_NET_ANNOUNCE)) {
                    String remainder = message.substring(4);
                    String [] varFields = remainder.split(":");
                    if ((varFields == null) || (varFields.length != 2)) {
                        return ret;
                    }

                    try {
                        ret = new APRSNetCentralNetAnnounceReport(objectName, varFields[0], varFields[1]);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}

