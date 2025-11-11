package com.kc1vmz.netcentral.aprsobject.common;

import java.util.List;

public class TransceiverMessageMany {

    private String transceiverId;
    private String callsignFrom;
    private String callsignTo;
    private List<String> messages;

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
    public List<String> getMessages() {
        return messages;
    }
    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
