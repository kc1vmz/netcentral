package com.kc1vmz.netcentral.aprsobject.common;

import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetControlReport;

public class TransceiverReport {

    private String transceiverId;
    private APRSNetControlReport report;
    private String callsignFrom;

    public APRSNetControlReport getReport() {
        return report;
    }
    public void setReport(APRSNetControlReport report) {
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
