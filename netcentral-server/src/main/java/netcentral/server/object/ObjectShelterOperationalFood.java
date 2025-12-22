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

import netcentral.server.enums.ObjectShelterReportingTimeframe;

public class ObjectShelterOperationalFood {
    private ObjectShelterReportingTimeframe timeframe;
    private Integer breakfast;
    private Integer lunch;
    private Integer dinner;
    private Integer snack;

    public ObjectShelterOperationalFood(){
    }
    public ObjectShelterOperationalFood(ObjectShelterReportingTimeframe timeframe, Integer breakfast, Integer lunch, Integer dinner, Integer snack) {
        setTimeframe(timeframe);
        setBreakfast(breakfast);
        setLunch(lunch);
        setDinner(dinner);
        setSnack(snack);
    }

    public ObjectShelterReportingTimeframe getTimeframe() {
        return timeframe;
    }
    public void setTimeframe(ObjectShelterReportingTimeframe timeframe) {
        this.timeframe = timeframe;
    }
    public Integer getBreakfast() {
        return breakfast;
    }
    public void setBreakfast(Integer breakfast) {
        this.breakfast = breakfast;
    }
    public Integer getLunch() {
        return lunch;
    }
    public void setLunch(Integer lunch) {
        this.lunch = lunch;
    }
    public Integer getDinner() {
        return dinner;
    }
    public void setDinner(Integer dinner) {
        this.dinner = dinner;
    }
    public Integer getSnack() {
        return snack;
    }
    public void setSnack(Integer snack) {
        this.snack = snack;
    }
}

