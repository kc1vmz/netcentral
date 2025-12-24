package com.kc1vmz.netcentral.common.object;

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

import com.kc1vmz.netcentral.common.enums.UserRole;

public class NetCentralServerUser {
    private String id;
    private String emailAddress;
    private Callsign callsign;
    private String password;
    private String accessToken;
    private UserRole role;
    private String firstName;
    private String lastName;

    public NetCentralServerUser(){
        this.role = UserRole.UNKNOWN;
    }
    public NetCentralServerUser(String id){
        this.id = id;
        this.role = UserRole.UNKNOWN;
    }
    public NetCentralServerUser(String id, UserRole role){
        this.id = id;
        this.role = role;
    }
    public NetCentralServerUser(String id, String emailAddress, String password, UserRole role, String callsign, String firstName, String lastName) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.password = password;
        this.role = role;
        if (callsign != null) {
            Callsign objCallsign = new Callsign();
            objCallsign.setCallsign(callsign);
            this.callsign = objCallsign;
        }
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public Callsign getCallsign() {
        return callsign;
    }
    public void setCallsign(Callsign callsign) {
        this.callsign = callsign;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public UserRole getRole() {
        return role;
    }
    public void setRole(UserRole role) {
        this.role = role;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
