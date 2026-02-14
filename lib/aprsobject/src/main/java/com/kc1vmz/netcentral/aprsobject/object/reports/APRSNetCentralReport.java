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

public class APRSNetCentralReport {
    private String reportObjectType;
    private String reportType;
    private String reportData;
    private String objectName;

    public APRSNetCentralReport(){
    }

    public String getReportObjectType() {
        return reportObjectType;
    }
    public void setReportObjectType(String reportObjectType) {
        if ((reportObjectType != null) && (reportObjectType.length() == 2)) {
            this.reportObjectType = reportObjectType;
        }
    }
    public String getReportType() {
        return reportType;
    }
    public void setReportType(String reportType) {
        if ((reportType != null) && (reportType.length() == 2)) {
            this.reportType = reportType;
        }
    }
    public String getReportData() {
        return reportData;
    }
    public void setReportData(String reportData) {
        this.reportData = reportData;
    }
    public String getObjectName() {
        return objectName;
    }
    public void setObjectName(String objectName) {
        if ((objectName != null) && (objectName.length() <= 9)) {
            this.objectName = objectName;
        }
    }
    public byte [] getBytes() {
        String ret = getFullReportData();
        return ret.getBytes();
    }
    public String getFullReportData() {
        return String.format("%s%s%s", reportObjectType, reportType, reportData);
    }
}
