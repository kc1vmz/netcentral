package com.kc1vmz.netcentral.aprsobject.object.reports;

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;

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

public class APRSNetCentralObjectAnnounceReport extends APRSNetCentralReport {

    public static final String OBJECT_TYPE_EOC = "EO";
    public static final String OBJECT_TYPE_SHELTER = "SH";
    public static final String OBJECT_TYPE_MEDICAL = "MC";
    public static final String OBJECT_TYPE_GENERAL = "GN";
    public static final String UNKNOWN = "UK";

    private String type;
    private String name;
    private String description;
    private ObjectType objectType;

    public APRSNetCentralObjectAnnounceReport(){
        super();
    }
    public APRSNetCentralObjectAnnounceReport(String objectName, String type, String name, String description, ObjectType objectType) {
        super();
        this.setObjectName(objectName);
        if (objectType.equals(ObjectType.EOC)) {
            this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_EOC);
            this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_PRIORITY_OBJECT_ANNOUNCE);
        } else if (objectType.equals(ObjectType.SHELTER)) {
            this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER);
            this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_PRIORITY_OBJECT_ANNOUNCE);
        } else if (objectType.equals(ObjectType.MEDICAL)) {
            this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_MEDICAL);
            this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_PRIORITY_OBJECT_ANNOUNCE);
        } else if (objectType.equals(ObjectType.RESOURCE)) {
            this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_GENERAL_RESOURCE_OBJECT);
            this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_GENERAL_RESOURCE_OBJECT_ANNOUNCE);
        } else {
            this.setReportObjectType(UNKNOWN);
            this.setReportType(UNKNOWN);
        }
        String data = String.format("%s:%s:%s", type, name, description);
        this.setReportData(data);
        this.setType(type);
        this.setObjectType(objectType);
        this.setName(name);
        this.setDescription(description);
    }

    public ObjectType getObjectType() {
        return objectType;
    }
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
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
    
    public static APRSNetCentralObjectAnnounceReport isValid(String objectName, String message) {
        APRSNetCentralObjectAnnounceReport ret = null;
        if (message != null) {
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);
            boolean isValid = false;
            ObjectType type = ObjectType.UNKNOWN;

            if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_PRIORITY_OBJECT_ANNOUNCE)) {
                if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_EOC)) {
                    isValid = true;
                    type = ObjectType.EOC;
                } else if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER)) {
                    isValid = true;
                    type = ObjectType.SHELTER;
                } else if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_MEDICAL)) {
                    isValid = true;
                    type = ObjectType.MEDICAL;
                }
            } else if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_GENERAL_RESOURCE_OBJECT)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_GENERAL_RESOURCE_OBJECT_ANNOUNCE)) {
                    isValid = true;
                    type = ObjectType.RESOURCE;
                }
            }

            if (isValid) {
                String remainder = message.substring(4);
                String [] varFields = remainder.split(":");
                if ((varFields == null) || (varFields.length != 3)) {
                    return ret;
                }

                try {
                    ret = new APRSNetCentralObjectAnnounceReport(objectName, varFields[0], varFields[1], varFields[2], type);
                } catch (Exception e) {
                    ret = null;
                }
            }
        }
        return ret;
    }
}

