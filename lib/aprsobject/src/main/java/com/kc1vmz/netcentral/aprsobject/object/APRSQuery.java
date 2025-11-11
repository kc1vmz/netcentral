package com.kc1vmz.netcentral.aprsobject.object;

import com.kc1vmz.netcentral.aprsobject.interfaces.APRSPacketInterface;

public class APRSQuery implements APRSPacketInterface {
    private byte [] data;
    private byte [] header;
    private String callsignFrom;
    private String callsignTo;
    private String queryType;
    private String lat;
    private String lon;
    private int radius;
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
    };
    public String getQueryType() {
        return queryType;
    }
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLon() {
        return lon;
    }
    public void setLon(String lon) {
        this.lon = lon;
    }
    public int getRadius() {
        return radius;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }
    public byte getDti() {
        return dti;
    }
    public void setDti(byte dti) {
        this.dti = dti;
    }
}
