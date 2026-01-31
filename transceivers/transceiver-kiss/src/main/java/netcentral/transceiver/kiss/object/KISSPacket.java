package netcentral.transceiver.kiss.object;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

import java.util.List;

public class KISSPacket {

    private String callsignFrom;
    private String callsignTo;
    private String applicationName;
    private List<String> digipeaters;
    private String data;
    private String packet;
    private boolean valid;

    public KISSPacket() {
        valid = false;
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
    public boolean isValid() {
        return valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
