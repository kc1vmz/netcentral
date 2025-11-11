package com.kc1vmz.netcentral.aprsobject.common;

public class TransceiverMessage {

    private String transceiverId;
    private String callsignFrom;
    private String callsignTo;
    private String message;
    private boolean bulletin;
    private boolean ackRequested;

    public TransceiverMessage() {
        ackRequested = true;
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
    public String getCallsignTo() {
        return callsignTo;
    }
    public void setCallsignTo(String callsignTo) {
        this.callsignTo = callsignTo;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public boolean isBulletin() {
        return bulletin;
    }
    public void setBulletin(boolean bulletin) {
        this.bulletin = bulletin;
    }
    public boolean isAckRequested() {
        return ackRequested;
    }
    public void setAckRequested(boolean ackRequested) {
        this.ackRequested = ackRequested;
    }
}
