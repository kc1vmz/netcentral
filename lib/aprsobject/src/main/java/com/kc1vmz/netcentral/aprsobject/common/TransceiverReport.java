package com.kc1vmz.netcentral.aprsobject.common;

import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralReport;

public class TransceiverReport {

    private String transceiverId;
    private APRSNetCentralReport report;
    private String callsignFrom;

    public APRSNetCentralReport getReport() {
        return report;
    }
    public void setReport(APRSNetCentralReport report) {
        this.report = report;
    }
    public String getTransceiverId() {
        return transceiverId;
    }
    public void setTransceiverId(String transceiverId) {
        this.transceiverId = transceiverId;
    }
    public String getCallsignFrom() {
        return callsignFrom;
    }
    public void setCallsignFrom(String callsignFrom) {
        this.callsignFrom = callsignFrom;
    }

}
