package netcentral.transceiver.kiss.object;

import java.util.List;

public class KISSPacket {

    private String callsignFrom;
    private String callsignTo;
    private String applicationName;
    private List<String> digipeaters;
    private String data;
    private String packet;

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
    public byte[] getHeaderBytes() {
        byte [] ret = new byte[36];

        byte [] callsignFromBytes = callsignFrom.getBytes();
        for (int i = 0; i < 10 && i < callsignFromBytes.length; i++) {
            ret[8+i] = callsignFromBytes[i];
        }
        byte [] callsignToBytes = callsignTo.getBytes();
        for (int i = 0; i < 10  && i < callsignToBytes.length; i++) {
            ret[18+i] = callsignToBytes[i];
        }

        return ret;
    }
    public String getApplicationName() {
        return applicationName;
    }
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    public String getPacket() {
        return packet;
    }
    public void setPacket(String packet) {
        this.packet = packet;
    }
}
