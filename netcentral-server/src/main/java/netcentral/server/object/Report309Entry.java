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

import netcentral.server.utils.DateStrUtils;

public class Report309Entry {

    private String from;
    private String startTime;
    private String endTime;
    private String message;

    public Report309Entry(CompletedParticipant participant) {
        from = String.format("%s (%s)", participant.getCallsign(), (participant.getTacticalCallsign() == null) ? "": participant.getTacticalCallsign());
        startTime = DateStrUtils.getTimeStr(participant.getStartTime());
        endTime = DateStrUtils.getTimeStr(participant.getEndTime());
        message = String.format("%s:%s:%sW", participant.getElectricalPowerType(), participant.getRadioStyle(), participant.getTransmitPower());
    }

    public Report309Entry(CompletedExpectedParticipant participant) {
        from = String.format("%s", participant.getCallsign());
        startTime = "Did not participate";
        endTime = "";
        message = "";
    }

    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
