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

import netcentral.server.utils.ConvertLonLat;

public class RenderedMapItem {
    private double longitude;
    private double latitude;
    private String name;
    private String title;
    private static double INVALID_VALUE = 1000;
    private boolean isInfrastructure = false;
    private boolean isObject = false;
    private Object itemObject;

    public RenderedMapItem() {
        longitude = INVALID_VALUE;
        latitude = INVALID_VALUE;
        isInfrastructure = false;
        isObject = false;
        itemObject = null;
    }
    public RenderedMapItem(double longitude, double latitude, String name, String title, Object object) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.title = title;
        isInfrastructure = false;
        isObject = false;
        this.itemObject = object;
    }
    public RenderedMapItem(String longitude, String latitude, String name, String title, Object object) {
        this.longitude = ConvertLonLat.convertLongitude(longitude);
        this.latitude = ConvertLonLat.convertLatitude(latitude);
        this.name = name;
        this.title = title;
        isInfrastructure = false;
        isObject = false;
        this.itemObject = object;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isValid() {
        if ((getLongitude() == 1000) || (getLatitude() == 1000)) {
            return false;
        }
        return true;
    }
    public boolean isInfrastructure() {
        return isInfrastructure;
    }
    public void setInfrastructure(boolean isInfrastructure) {
        this.isInfrastructure = isInfrastructure;
    }
    public boolean isObject() {
        return isObject;
    }
    public void setObject(boolean isObject) {
        this.isObject = isObject;
    }
    public Object getItemObject() {
        return itemObject;
    }
    public void setItemObject(Object itemObject) {
        this.itemObject = itemObject;
    }
}
