package com.kc1vmz.netcentral.aprsobject.object;

import com.kc1vmz.netcentral.aprsobject.interfaces.APRSPacketInterface;

public class APRSAgrelo implements APRSPacketInterface {
    private byte [] data;
    private byte [] header;
    private String callsignFrom;
    private String callsignTo;
    private int bearing;
    private int quality;
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
    public int getBearing() {
        return bearing;
    }
    public void setBearing(int bearing) {
        this.bearing = bearing;
    }
    public int getQuality() {
        return quality;
    }
    public void setQuality(int quality) {
        this.quality = quality;
    }
    public byte getDti() {
        return dti;
    }
    public void setDti(byte dti) {
        this.dti = dti;
    }
}
