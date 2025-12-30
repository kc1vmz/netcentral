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

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.interfaces.APRSPacketInterface;
import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

public class APRSObject implements APRSPacketInterface {
    private String id;
    private byte [] data;
    private byte [] header;
    private String callsignFrom;
    private String callsignTo;
    private boolean alive;
    private String lat;
    private String lon;
    private String time;
    private ZonedDateTime ldtime;
    private String comment;
    private byte dti;
    private String prettyLdtime;
    private ObjectType type;
    private boolean remote;

    public APRSObject(){
    }
    public APRSObject(String id, String callsignFrom, String callsignTo, boolean alive, String lat, String lon, String time, ZonedDateTime ldtime, String comment, ObjectType type, boolean remote) {
        this.id = id;
        this.callsignFrom = callsignFrom;
        this.callsignTo = callsignTo;
        this.alive = alive;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.ldtime = ldtime;
        this.comment = comment;
        this.prettyLdtime = PrettyZonedDateTimeFormatter.format(ldtime);
        this.type = type;
        this.remote = remote;
    }
    @Override
    public byte [] getData() {
        return data;
    }
    @Override
    public void setData(byte[] data) {
        this.data = data;
    }
    @Override
    public byte [] getHeader() {
        return header;
    }
    @Override
    public void setHeader(byte[] header) {
        this.header = header;
    }
    @Override
    public String getCallsignFrom() {
        return callsignFrom;
    }
    @Override
    public void setCallsignFrom(String callsignFrom) {
        this.callsignFrom = callsignFrom;
    }
    @Override
    public String getCallsignTo() {
        return callsignTo;
    }
    @Override
    public void setCallsignTo(String callsignTo) {
        this.callsignTo = callsignTo;
    }
    public boolean isAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
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
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    @Override
    public byte getDti() {
        return dti;
    }
    @Override
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
    public ObjectType getType() {
        return type;
    }
    public void setType(ObjectType type) {
        this.type = type;
    }
    public boolean isRemote() {
        return remote;
    }
    public void setRemote(boolean remote) {
        this.remote = remote;
    }
}

