package netcentral.transceiver.agw.object;

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
