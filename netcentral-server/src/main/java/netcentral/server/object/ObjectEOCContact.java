package netcentral.server.object;

import java.time.ZonedDateTime;

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

public class ObjectEOCContact {
    private String directorName;
    private String incidentCommanderName;
    private ZonedDateTime reportTime;

    public ObjectEOCContact() {
    }
    public ObjectEOCContact(String directorName, String incidentCommanderName, ZonedDateTime reportTime) {
        setDirectorName(directorName);
        setIncidentCommanderName(incidentCommanderName);
        setReportTime(reportTime);
    }
    public String getDirectorName() {
        return directorName;
    }
    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }
    public String getIncidentCommanderName() {
        return incidentCommanderName;
    }
    public void setIncidentCommanderName(String incidentCommanderName) {
        this.incidentCommanderName = incidentCommanderName;
    }
    public ZonedDateTime getReportTime() {
        return reportTime;
    }
    public void setReportTime(ZonedDateTime reportTime) {
        this.reportTime = reportTime;
    }
}
