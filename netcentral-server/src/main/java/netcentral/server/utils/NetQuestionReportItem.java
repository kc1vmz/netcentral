package netcentral.server.utils;

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

public class NetQuestionReportItem {
    private String label;
    private ZonedDateTime time;
    private String callsign;
    private String text;

    public NetQuestionReportItem() {
    }
    public NetQuestionReportItem(ZonedDateTime time, String text) {
        this.time = time;
        this.text = text;
        this.label = "Q";
        this.callsign = "---------";
    }
    public NetQuestionReportItem(ZonedDateTime time, String text, String callsign) {
        this.time = time;
        this.text = text;
        this.callsign = callsign;
        this.label = "A";
    }
    public NetQuestionReportItem(String text) {
        this.text = text;
        this.label = "A";
    }

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public ZonedDateTime getTime() {
        return time;
    }
    public void setTime(ZonedDateTime time) {
        this.time = time;
    }
    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
