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

public class APRSPosition implements APRSPacketInterface {

    private String comment;
    private String lat;
    private String lon;
    private String time;
    private ZonedDateTime ldtime;
    private int power;
    private int height;
    private int gain;
    private String directivity;
    private int range;
    private int strength;
    private byte [] data;
    private byte [] header;
    private String callsignFrom;
    private String callsignTo;
    private byte dti;
    private boolean hasWeatherReport;
    private String weatherReport;
    private String prettyLdtime;

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLon() {
        if ((lon != null) && (lon.length() == 8)) {
            return "0"+lon;
        }
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
    public int getPower() {
        return power;
    }
    public void setPower(int power) {
        this.power = power;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getGain() {
        return gain;
    }
    public void setGain(int gain) {
        this.gain = gain;
    }
    public String getDirectivity() {
        return directivity;
    }
    public void setDirectivity(String directivity) {
        this.directivity = directivity;
    }
    public int getRange() {
        return range;
    }
    public void setRange(int range) {
        this.range = range;
    }
    public int getStrength() {
        return strength;
    }
    public void setStrength(int strength) {
        this.strength = strength;
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
    public boolean isHasWeatherReport() {
        return hasWeatherReport;
    }
    public void setHasWeatherReport(boolean hasWeatherReport) {
        this.hasWeatherReport = hasWeatherReport;
    }
    public String getWeatherReport() {
        return weatherReport;
    }
    public void setWeatherReport(String weatherReport) {
        this.weatherReport = weatherReport;
    }
    public String getPrettyLdtime() {
        return prettyLdtime;
    }
    public void setPrettyLdtime(String prettyLdtime) {
        this.prettyLdtime = prettyLdtime;
    }
}
