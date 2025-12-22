package com.kc1vmz.netcentral.aprsobject.object;

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

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.interfaces.APRSPacketInterface;
import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

public class APRSStatus implements APRSPacketInterface {
    private String id;
    private String time;
    private ZonedDateTime ldtime;
    private String status;
    private byte [] data;
    private byte [] header;
    private String callsignFrom;
    private String callsignTo;
    private byte dti;
    private String prettyLdtime;

    public APRSStatus(){
    }
    public APRSStatus(String id, String callsignFrom, String time, ZonedDateTime ldtime, String status) {
        this.id = id;
        this.callsignFrom = callsignFrom;
        this.time = time;
        this.ldtime = ldtime;
        this.status = status;
        this.prettyLdtime = PrettyZonedDateTimeFormatter.format(ldtime);
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public ZonedDateTime getLdtime() {
        return ldtime;
    }
    public void setLdtime(ZonedDateTime ldtime) {
        this.ldtime = ldtime;
        this.prettyLdtime = PrettyZonedDateTimeFormatter.format(ldtime);
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
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
    public byte getDti() {
        return dti;
    }
    public void setDti(byte dti) {
        this.dti = dti;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPrettyLdtime() {
        return prettyLdtime;
    }
    public void setPrettyLdtime(String prettyLdtime) {
        this.prettyLdtime = prettyLdtime;
    }
}
