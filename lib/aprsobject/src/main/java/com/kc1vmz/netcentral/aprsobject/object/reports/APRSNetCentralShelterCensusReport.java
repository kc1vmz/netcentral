package com.kc1vmz.netcentral.aprsobject.object.reports;

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

public class APRSNetCentralShelterCensusReport extends APRSNetCentralReport {
    private ZonedDateTime dateReported; 
    private int p03;
    private int p47;
    private int p812;
    private int p1318;
    private int p1965;
    private int p66;

    public APRSNetCentralShelterCensusReport(){
        super();
    }
    public APRSNetCentralShelterCensusReport(String objectName, int p03, int p47, int p812, int p1318, int p1965, int p66, ZonedDateTime dateReported) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_POPULATION_CENSUS);
        String data = String.format("%06d%06d%06d%06d%06d%06d%4d%02d%02d", p03, p47, p812, p1318, p1965, p66, 
                                        dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth());
        this.setReportData(data);
        this.p03 = p03;
        this.p47 = p47;
        this.p812 = p812;
        this.p1318 = p1318;
        this.p1965 = p1965;
        this.p66 = p66;
        this.dateReported = dateReported;
    }
    public int getP03() {
        return p03;
    }
    public void setP03(int p03) {
        this.p03 = p03;
    }
    public int getP47() {
        return p47;
    }
    public void setP47(int p47) {
        this.p47 = p47;
    }
    public int getP812() {
        return p812;
    }
    public void setP812(int p812) {
        this.p812 = p812;
    }
    public int getP1318() {
        return p1318;
    }
    public void setP1318(int p1318) {
        this.p1318 = p1318;
    }
    public int getP1965() {
        return p1965;
    }
    public void setP1965(int p1965) {
        this.p1965 = p1965;
    }
    public int getP66() {
        return p66;
    }
    public void setP66(int p66) {
        this.p66 = p66;
    }
    public ZonedDateTime getDateReported() {
        return dateReported;
    }
    public void setDateReported(ZonedDateTime dateReported) {
        this.dateReported = dateReported;
    }
    public static APRSNetCentralShelterCensusReport isValid(String objectName, String message) {
        APRSNetCentralShelterCensusReport ret = null;
        if ((message != null) && (message.length() == 48)) {
            //"SHPC%06d%06d%06d%06d%06d%06d%4d%02d%02d", p03, p47, p812, p1318, p1965, p66, dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth());
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_POPULATION_CENSUS)) {
                    String p03 = message.substring(4, 10);
                    String p47 = message.substring(10, 16);
                    String p812 = message.substring(16, 22);
                    String p1318 = message.substring(22, 28);
                    String p1965 = message.substring(28, 34);
                    String p66 = message.substring(34, 40);
                    String year = message.substring(40, 44);
                    String month = message.substring(44, 46);
                    String day = message.substring(46);

                    try {
                        ZonedDateTime time = ZonedDateTime.now().withYear(Integer.parseInt(year)).withMonth(Integer.parseInt(month)).withDayOfMonth(Integer.parseInt(day)).withHour(0).withMinute(0);
                        ret = new APRSNetCentralShelterCensusReport(objectName, Integer.parseInt(p03), Integer.parseInt(p47), Integer.parseInt(p812),
                                             Integer.parseInt(p1318), Integer.parseInt(p1965), Integer.parseInt(p66), time);
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }
}
