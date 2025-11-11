package netcentral.transceiver.kiss.object;

import java.util.List;

public class AgwResponse2 {
    private String callsignFrom;
    private String callsignTo;
    private List<String> digipeaters;
    private String data;

    public AgwResponse2(String callsignFrom, String callsignTo, List<String> digipeaters, String data) {
        this.callsignFrom = callsignFrom;
        this.callsignTo = callsignTo;
        this.digipeaters = digipeaters;
        this.data = data;
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

    public List<String> getDigipeaters() {
        return digipeaters;
    }

    public void setDigipeaters(List<String> digipeaters) {
        this.digipeaters = digipeaters;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte [] getPacketBytes() {
        byte [] ret = new byte[36+data.length()];

        //8
        for (int c = 0; c < callsignFrom.length() && c < 9; c++) {
            ret[c+8] = (byte) callsignFrom.charAt(c);
        }
        //18
        for (int c = 0; c < callsignTo.length() && c < 9; c++) {
            ret[c+18] = (byte) callsignTo.charAt(c);
        }
        //36
        for (int c = 36; c < 36 + data.length(); c++) {
            ret[c] = (byte) data.charAt(c-36);
        }
        return ret;
    }
    
}
