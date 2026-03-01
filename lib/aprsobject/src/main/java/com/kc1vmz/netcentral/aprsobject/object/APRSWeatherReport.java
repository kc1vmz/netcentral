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

public class APRSWeatherReport implements APRSPacketInterface {
    private String id;
    private byte [] data;
    private byte [] header;
    private String callsignFrom;
    private String callsignTo;
    private String time;
    private ZonedDateTime ldtime;
    private int windDirection;
    private int windSpeed;
    private int gust;
    private int temperature;
    private int rainfallLast1Hr;
    private int rainfallLast24Hr;
    private int rainfallSinceMidnight;
    private int humidity;
    private int barometricPressure;
    private int luminosity;
    private int snowfallLast24Hr;
    private int rawRainCounter;
    private String lat;
    private String lon;
    private byte dti;
    private String prettyLdtime;

    public APRSWeatherReport(String id, String callsignFrom, String callsignTo, String time,  int windDirection, int windSpeed, int gust, int temperature,
                                int rainfallLast1Hr, int rainfallLast24Hr, int rainfallSinceMidnight, int humidity, 
                                int barometricPressure, int luminosity, int snowfallLast24Hr,
                                int rawRainCounter, String lat, String lon, ZonedDateTime ldtime) {
        this.id = id;
        this.callsignFrom = callsignFrom;
        this.callsignTo = callsignTo;
        this.time = time;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.gust = gust;
        this.temperature = temperature;
        this.rainfallLast1Hr = rainfallLast1Hr;
        this.rainfallLast24Hr = rainfallLast24Hr;
        this.rainfallSinceMidnight = rainfallSinceMidnight;
        this.humidity = humidity;
        this.barometricPressure = barometricPressure;
        this.luminosity = luminosity;
        this.snowfallLast24Hr = snowfallLast24Hr;
        this.rawRainCounter = rawRainCounter;
        this.lat = lat;
        this.lon = lon;
        this.ldtime = ldtime;
        this.prettyLdtime = PrettyZonedDateTimeFormatter.format(ldtime);
    }

    public APRSWeatherReport() {
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
    public int getWindDirection() {
        return windDirection;
    }
    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }
    public int getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }
    public int getGust() {
        return gust;
    }
    public void setGust(int gust) {
        this.gust = gust;
    }
    public int getTemperature() {
        return temperature;
    }
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    public int getRainfallLast1Hr() {
        return rainfallLast1Hr;
    }
    public void setRainfallLast1Hr(int rainfallLast1Hr) {
        this.rainfallLast1Hr = rainfallLast1Hr;
    }
    public int getRainfallLast24Hr() {
        return rainfallLast24Hr;
    }
    public void setRainfallLast24Hr(int rainfallLast24Hr) {
        this.rainfallLast24Hr = rainfallLast24Hr;
    }
    public int getRainfallSinceMidnight() {
        return rainfallSinceMidnight;
    }
    public void setRainfallSinceMidnight(int rainfallSinceMidnight) {
        this.rainfallSinceMidnight = rainfallSinceMidnight;
    }
    public int getHumidity() {
        return humidity;
    }
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
    public int getBarometricPressure() {
        return barometricPressure;
    }
    public void setBarometricPressure(int barometricPressure) {
        this.barometricPressure = barometricPressure;
    }
    public int getLuminosity() {
        return luminosity;
    }
    public void setLuminosity(int luminosity) {
        this.luminosity = luminosity;
    }
    public int getSnowfallLast24Hr() {
        return snowfallLast24Hr;
    }
    public void setSnowfallLast24Hr(int snowfallLast24Hr) {
        this.snowfallLast24Hr = snowfallLast24Hr;
    }
    public int getRawRainCounter() {
        return rawRainCounter;
    }
    public void setRawRainCounter(int rawRainCounter) {
        this.rawRainCounter = rawRainCounter;
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
