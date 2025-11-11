package netcentral.server.object;

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
