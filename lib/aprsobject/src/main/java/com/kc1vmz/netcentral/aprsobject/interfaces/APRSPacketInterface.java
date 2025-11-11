package com.kc1vmz.netcentral.aprsobject.interfaces;

public interface APRSPacketInterface {
    public byte getDti();
    public void setDti(byte dti);
    byte [] getData();
    void setData(byte[] data);
    byte [] getHeader();
    void setHeader(byte[] data);
    String getCallsignFrom();
    void setCallsignFrom(String callsignFrom);
    String getCallsignTo();
    void setCallsignTo(String callsignTo);
}
