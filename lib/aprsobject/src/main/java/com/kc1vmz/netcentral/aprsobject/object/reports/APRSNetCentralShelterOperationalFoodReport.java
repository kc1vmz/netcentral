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

public class APRSNetCentralShelterOperationalFoodReport extends APRSNetCentralReport {
    public final static int REPORT_INFOTYPE_UNKNOWN = 0;
    public final static int REPORT_INFOTYPE_ONHAND = 1;
    public final static int REPORT_INFOTYPE_REQUIRE = 2;
    public final static int REPORT_INFOTYPE_USED = 3;

    private ZonedDateTime date;
    private ZonedDateTime dateReported;
    private int infoType;
    private int breakfast;
    private int lunch;
    private int dinner; 
    private int snack;


    public APRSNetCentralShelterOperationalFoodReport(){
        super();
    }
    public APRSNetCentralShelterOperationalFoodReport(String objectName, int infoType, int breakfast, int lunch, int dinner, int snack, ZonedDateTime date, ZonedDateTime dateReported) {
        super();
        this.setObjectName(objectName);
        this.setReportObjectType(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER);
        this.setReportType(APRSNetCentralReportConstants.REPORT_TYPE_OPERATIONAL_FOOD);
        if ((infoType != REPORT_INFOTYPE_ONHAND) && (infoType != REPORT_INFOTYPE_REQUIRE) && (infoType != REPORT_INFOTYPE_USED)) {
            infoType = REPORT_INFOTYPE_UNKNOWN;
        }
        String data = String.format("%d%06d%06d%06d%06d%4d%02d%02d", infoType, breakfast, lunch, dinner, snack, 
                                        date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        this.setReportData(data);

        this.date = date;
        this.dateReported = dateReported;
        this.infoType = infoType;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner; 
        this.snack = snack;
    }
    public ZonedDateTime getDate() {
        return date;
    }
    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
    public ZonedDateTime getDateReported() {
        return dateReported;
    }
    public void setDateReported(ZonedDateTime dateReported) {
        this.dateReported = dateReported;
    }
    public int getInfoType() {
        return infoType;
    }
    public void setInfoType(int infoType) {
        this.infoType = infoType;
    }
    public int getBreakfast() {
        return breakfast;
    }
    public void setBreakfast(int breakfast) {
        this.breakfast = breakfast;
    }
    public int getLunch() {
        return lunch;
    }
    public void setLunch(int lunch) {
        this.lunch = lunch;
    }
    public int getDinner() {
        return dinner;
    }
    public void setDinner(int dinner) {
        this.dinner = dinner;
    }
    public int getSnack() {
        return snack;
    }
    public void setSnack(int snack) {
        this.snack = snack;
    }
    public static APRSNetCentralShelterOperationalFoodReport isValid(String objectName, String message) {
        APRSNetCentralShelterOperationalFoodReport ret = null;
        if ((message != null) && (message.length() == 37)) {
            //"SHOF%d%06d%06d%06d%06d%4d%02d%02d", timePeriod, breakfast, lunch, dinner, snack, dateReported.getYear(), dateReported.getMonthValue(), dateReported.getDayOfMonth()
            
            String objectType = message.substring(0, 2);
            String reportType = message.substring(2, 4);

            if (objectType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_OBJECT_TYPE_SHELTER)) {
                if (reportType.equalsIgnoreCase(APRSNetCentralReportConstants.REPORT_TYPE_OPERATIONAL_FOOD)) {
                    String infoType = message.substring(4, 5);
                    String breakfast = message.substring(5, 11);
                    String lunch = message.substring(11, 17);
                    String dinner = message.substring(17, 23);
                    String snack = message.substring(23, 29);
                    String year = message.substring(29, 33);
                    String month = message.substring(33, 35);
                    String day = message.substring(35);

                    try {
                        ZonedDateTime time = ZonedDateTime.now().withYear(Integer.parseInt(year)).withMonth(Integer.parseInt(month)).withDayOfMonth(Integer.parseInt(day)).withHour(0).withMinute(0);
                        ret = new APRSNetCentralShelterOperationalFoodReport(objectName, Integer.parseInt(infoType), Integer.parseInt(breakfast), Integer.parseInt(lunch),
                                                                        Integer.parseInt(dinner), Integer.parseInt(snack), time, ZonedDateTime.now());
                    } catch (Exception e) {
                        ret = null;
                    }
                }
            }
        }
        return ret;
    }

}
