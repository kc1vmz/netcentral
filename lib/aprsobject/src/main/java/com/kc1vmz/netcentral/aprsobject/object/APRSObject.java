package com.kc1vmz.netcentral.aprsobject.object;

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

    public APRSObject(){
    }
    public APRSObject(String id, String callsignFrom, String callsignTo, boolean alive, String lat, String lon, String time, ZonedDateTime ldtime, String comment, ObjectType type) {
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
}

