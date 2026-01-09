package com.kc1vmz.netcentral.common.object;

/*
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

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

public class InternetServer {
    private String id;
    private String ipAddress;
    private String name;
    private String description;
    private String loginCallsign;
    private String query;

    public InternetServer() {
    }

    public InternetServer(String id, String ipAddress, String name, String description, String loginCallsign, String query) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.name = name;
        this.description = description;
        this.loginCallsign = loginCallsign;
        this.query = query;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLoginCallsign() {
        return loginCallsign;
    }
    public void setLoginCallsign(String loginCallsign) {
        this.loginCallsign = loginCallsign;
    }
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
