package com.kc1vmz.netcentral.parser.exception;

public class ParserException extends AgwException {
    private String callsign;
    private String packetText;
    private String description;

    public ParserException(String description) {
        super(description);
    }
    public ParserException(String callsign, String packetText, String description) {
        super(description);
        this.callsign = callsign;
        this.packetText = packetText;
    }
    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getPacketText() {
        return packetText;
    }
    public void setPacketText(String packetText) {
        this.packetText = packetText;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
