package com.kc1vmz.netcentral.aprsobject.object.reports;

public class APRSNetControlReport {
    private String reportObjectType;
    private String reportType;
    private String reportData;
    private String objectName;

    public APRSNetControlReport(){
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
        String ret = String.format("%s%s%-9s%s", reportObjectType, reportType, objectName, reportData);
        return ret.getBytes();
    }
}
