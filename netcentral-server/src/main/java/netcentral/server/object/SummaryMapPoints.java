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

import java.util.List;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class SummaryMapPoints {
    private double centerLongitude;
    private double centerLatitude;
    // bounding box for map
    private double minLatitude = -90;
    private double minLongitude = -180;
    private double maxLatitude = 90;
    private double maxLongitude = 180;
    private List<RenderedMapItem> items;

    public SummaryMapPoints() {
    }
    public SummaryMapPoints(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
        setMinLatitude(minLatitude);
        setMaxLatitude(maxLatitude);
        setMinLongitude(minLongitude);
        setMaxLongitude(maxLongitude);

        setCenterLatitude((maxLatitude + minLatitude)/2);
        setCenterLongitude((maxLongitude + minLongitude)/2);
    }
    public double getCenterLongitude() {
        return centerLongitude;
    }
    public void setCenterLongitude(double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }
    public double getCenterLatitude() {
        return centerLatitude;
    }
    public void setCenterLatitude(double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }
    public double getMinLatitude() {
        return minLatitude;
    }
    public void setMinLatitude(double minLatitude) {
        this.minLatitude = minLatitude;
    }
    public double getMinLongitude() {
        return minLongitude;
    }
    public void setMinLongitude(double minLongitude) {
        this.minLongitude = minLongitude;
    }
    public double getMaxLatitude() {
        return maxLatitude;
    }
    public void setMaxLatitude(double maxLatitude) {
        this.maxLatitude = maxLatitude;
    }
    public double getMaxLongitude() {
        return maxLongitude;
    }
    public void setMaxLongitude(double maxLongitude) {
        this.maxLongitude = maxLongitude;
    }
    public List<RenderedMapItem> getItems() {
        return items;
    }
    public void setItems(List<RenderedMapItem> items) {
        this.items = items;
    }
}
