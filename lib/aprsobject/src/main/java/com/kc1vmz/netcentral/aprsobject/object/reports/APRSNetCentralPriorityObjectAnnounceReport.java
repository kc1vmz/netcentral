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

public class APRSNetCentralPriorityObjectAnnounceReport extends APRSNetCentralReport {

    public static final String OBJECT_TYPE_EOC = "EOC";
    public static final String OBJECT_TYPE_SHELTER = "SHELTER";
    public static final String OBJECT_TYPE_MEDICAL = "MEDICAL";

    private String type;
    private String name;
    private String description;

    public APRSNetCentralPriorityObjectAnnounceReport(){
        super();
    }
    public APRSNetCentralPriorityObjectAnnounceReport(String objectName, String type, String name, String description) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_PRIORITY_OBJECT);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_PRIORITY_OBJECT_ANNOUNCE);
        String data = String.format("%s:%s:%s", type, name, description);
        this.setReportData(data);
        this.setType(type);
        this.setName(name);
        this.setDescription(description);
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
    
    public static APRSNetCentralPriorityObjectAnnounceReport isValid(String objectName, String message) {
        APRSNetCentralPriorityObjectAnnounceReport ret = null;
        if (message != null) {
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_PRIORITY_OBJECT)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_PRIORITY_OBJECT_ANNOUNCE)) {
                    String remainder = message.substring(4);
                    String [] varFields = remainder.split(":");
                    if ((varFields == null) || (varFields.length != 3)) {
                        return ret;
                    }

                    try {
                        ret = new APRSNetCentralPriorityObjectAnnounceReport(objectName, varFields[0], varFields[1], varFields[2]);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}

