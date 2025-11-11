package netcentral.server.object;

import java.util.List;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class SummaryMapPoints {
    private double centerLongitude;
    private double centerLatitude;
    // bounding box for map
    private double minLatitude;
    private double minLongitude;
    private double maxLatitude;
    private double maxLongitude;
    private List<RenderedMapItem> items;

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
