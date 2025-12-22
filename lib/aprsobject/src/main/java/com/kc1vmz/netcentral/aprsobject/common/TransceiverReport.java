package com.kc1vmz.netcentral.aprsobject.common;

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

import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralReport;

public class TransceiverReport {

    private String transceiverId;
    private APRSNetCentralReport report;
    private String callsignFrom;

    public APRSNetCentralReport getReport() {
        return report;
    }
    public void setReport(APRSNetCentralReport report) {
        this.report = report;
    }
    public String getTransceiverId() {
        return transceiverId;
    }
    public void setTransceiverId(String transceiverId) {
        this.transceiverId = transceiverId;
    }
    public String getCallsignFrom() {
        return callsignFrom;
    }
    public void setCallsignFrom(String callsignFrom) {
        this.callsignFrom = callsignFrom;
    }

}
