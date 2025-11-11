package com.kc1vmz.netcentral.aprsobject.object;

import java.util.Map;

import com.kc1vmz.netcentral.aprsobject.interfaces.APRSPacketInterface;

public class APRSStationCapabilities implements APRSPacketInterface {
    private byte [] data;
    private byte [] header;
    private String callsignFrom;
    private String callsignTo;
    private Map<String, String> values;
    private byte dti;

    public byte [] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public byte [] getHeader() {
        return header;
    }
    public void setHeader(byte[] header) {
        this.header = header;
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
    public Map<String, String> getValues() {
        return values;
    }
    public void setValues(Map<String, String> values) {
        this.values = values;
    }
    public byte getDti() {
        return dti;
    }
    public void setDti(byte dti) {
        this.dti = dti;
    }
}
