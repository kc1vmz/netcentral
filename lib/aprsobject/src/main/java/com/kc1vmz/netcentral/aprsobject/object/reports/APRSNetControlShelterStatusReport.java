package com.kc1vmz.netcentral.aprsobject.object.reports;

import java.time.ZonedDateTime;

public class APRSNetControlShelterStatusReport extends APRSNetControlReport {
    private ZonedDateTime dateReported; 
    private int status;
    private int state;
    private String message;

    public APRSNetControlShelterStatusReport(){
        super();
    }
    public APRSNetControlShelterStatusReport(String objectName, int status, int state, String message, ZonedDateTime dateReported) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType("SH"); // shelter
        this.setReportType("ST"); // Population census
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
    public static APRSNetControlShelterStatusReport isValid(String objectName, String message) {
        APRSNetControlShelterStatusReport ret = null;
        if ((message != null) && (message.length() >= 6)) {
            //"SHST%d%d%s", state, status, message
            
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase("SH")) {
                if (reportType.equalsIgnoreCase("ST")) {
                    String state = message.substring(4, 5);
                    String status = message.substring(5, 6);
                    String messageText = "";
                    if (message.length() > 6) {
                        messageText = message.substring(6);
                    }

                    try {
                        ZonedDateTime time = ZonedDateTime.now();
                        ret = new APRSNetControlShelterStatusReport(objectName, Integer.parseInt(status),  Integer.parseInt(state), messageText, time);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}
