package netcentral.server.object;

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

import netcentral.server.enums.WinlinkSessionState;

public class WinlinkSessionCacheEntry {
    private String callsign;
    private String password;
    private String recipient;
    private String subject;
    private String message;
    private WinlinkSessionState state;
    private String threeDigitChallenge;
    private String sixDigitResponse;
    private ZonedDateTime startTime;

    public WinlinkSessionCacheEntry() {
        this.startTime = ZonedDateTime.now();
    }
    public WinlinkSessionCacheEntry(WinlinkSessionCacheEntry obj) {
        setCallsign(obj.getCallsign());
        setPassword(obj.getPassword());
        setRecipient(obj.getRecipient());
        setSubject(obj.getSubject());
        setMessage(obj.getMessage());
        setState(obj.getState());
        setThreeDigitChallenge(obj.getThreeDigitChallenge());
        setSixDigitResponse(obj.getSixDigitResponse());
        setStartTime(obj.getStartTime());
    }
    public WinlinkSessionCacheEntry(String callsign, String password, String recipient, String subject, String message) {
        this.callsign = callsign;
        this.password = password;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.state = WinlinkSessionState.WAITING;
        this.startTime = ZonedDateTime.now();
    }
    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public WinlinkSessionState getState() {
        return state;
    }
    public void setState(WinlinkSessionState state) {
        this.state = state;
    }
    public String getThreeDigitChallenge() {
        return threeDigitChallenge;
    }
    public void setThreeDigitChallenge(String threeDigitChallenge) {
        this.threeDigitChallenge = threeDigitChallenge;
    }
    public String getSixDigitResponse() {
        return sixDigitResponse;
    }
    public void setSixDigitResponse(String sixDigitResponse) {
        this.sixDigitResponse = sixDigitResponse;
    }
    public ZonedDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
