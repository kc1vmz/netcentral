package com.kc1vmz.netcentral.parser.exception;

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

public class ParserException extends AgwException {
    private String callsign;
    private String packetText;
    private String description;

    public ParserException(String description) {
        super(description);
    }
    public ParserException(String callsign, String packetText, String description) {
        super(description);
        this.callsign = callsign;
        this.packetText = packetText;
    }
    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getPacketText() {
        return packetText;
    }
    public void setPacketText(String packetText) {
        this.packetText = packetText;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
