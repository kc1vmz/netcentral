package netcentral.server.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.APRSObject;

import netcentral.server.object.Participant;
import netcentral.server.object.RenderedMapItem;
import netcentral.server.object.TrackedStation;

public class MapMarkerFactory {
    private static final Logger logger = LogManager.getLogger(MapMarkerFactory.class);

    private List<RenderedMapItem> items;
    private String longitudes;
    private String latitudes;
    private String displayInfos;
    private String titles;
    private String sep;
    private String centerLongitude;
    private String centerLatitude;
    private String minLongitude;
    private String minLatitude;
    private String maxLongitude;
    private String maxLatitude;

    public MapMarkerFactory() {
    }

    public void processParticipants(List<Participant> participants, String sep) {
        this.sep = sep;
        items = new ArrayList<>();
        if (participants != null) {
            for (Participant participant : participants) {
                if ((participant.getLat() == null)  || (participant.getLon() == null)) {
                    continue;
                }
                String name = String.format("Callsign: %s Status: %s Voice: %s", participant.getCallsign(), 
                                (participant.getStatus() == null) ? "Unknown" : participant.getStatus(), 
                                (participant.getVoiceFrequency() == null) ? "Unknown" : participant.getVoiceFrequency());
                RenderedMapItem item = new RenderedMapItem(
                                                participant.getLon(), participant.getLat(), name, participant.getCallsign(), participant);
                if ((item != null) && (item.isValid())) {
                    items.add(item);
                }
            }
        }

        createMapInfo();
    }
    public void processTrackedStation(TrackedStation trackedStation, String sep) {
        List<TrackedStation> trackedStations = new ArrayList<>();
        trackedStations.add(trackedStation);
        processTrackedStations(trackedStations, sep);
    }
    
    public void processTrackedStations(List<TrackedStation> trackedStations, String sep) {
        this.sep = sep;
        items = new ArrayList<>();
        if (trackedStations != null) {
            for (TrackedStation trackedStation : trackedStations) {
                if ((trackedStation.getLat() == null)  || (trackedStation.getLon() == null)) {
                    continue;
                }
                try {
                    String name = String.format("Callsign: %s Name: %s Status: %s Rx Freq: %s Tx Freq: %s Last heard: %s", trackedStation.getCallsign(), 
                                        trackedStation.getName(), 
                                        (trackedStation.getStatus() == null) ? "Unknown" : trackedStation.getStatus(), 
                                        (trackedStation.getFrequencyRx() == null) ? "Unknown" : trackedStation.getFrequencyRx(), 
                                        (trackedStation.getFrequencyTx() == null) ? "Unknown" : trackedStation.getFrequencyTx(), 
                                        (trackedStation.getLastHeard() == null) ? null : trackedStation.getLastHeard());
                    RenderedMapItem item = new RenderedMapItem(
                        trackedStation.getLon(), trackedStation.getLat(), name, trackedStation.getCallsign(), trackedStation);
                    if ((item != null) && (item.isValid())) {
                        items.add(item);
                    }
                } catch (Exception e) {
                    logger.error("Exception caught processing stations", e);
                }
            }
        }

        createMapInfo();
    }

    public void processObject(APRSObject obj, String sep) {
        List<APRSObject> objs = new ArrayList<>();
        objs.add(obj);
        processObjects(objs, sep);
    }

    public void processObjects(List<APRSObject> objects, String sep) {
        this.sep = sep;
        items = new ArrayList<>();
        if (objects != null) {
            for (APRSObject obj : objects) {
                try {
                    if ((obj.getLat() == null)  || (obj.getLon() == null)) {
                        continue;
                    }
                    String name = String.format("Callsign: %s Status: %s Last heard: %s", 
                                        obj.getCallsignFrom(), 
                                        obj.isAlive() ? "Alive" : "Dead", 
                                        (obj.getLdtime() != null) ? obj.getLdtime().toString() : "");
                    RenderedMapItem item = new RenderedMapItem(
                        obj.getLon(), obj.getLat(), name, obj.getCallsignFrom(), obj);
                    if ((item != null) && (item.isValid())) {
                        item.setObject(true);
                        items.add(item);
                    }
                } catch (Exception e) {
                    logger.error("Exception processing objects", e);
                }
            }
        }

        createMapInfo();
    }

    private void createMapInfo() {
        if (items == null) {
            return;
        }
        StringBuilder lats = new StringBuilder();
        StringBuilder lons = new StringBuilder();
        StringBuilder displayInfos = new StringBuilder();
        StringBuilder titles = new StringBuilder();
        boolean isFirst = true;
        for (RenderedMapItem item : items) {
            if (!isFirst) {
                lats.append(this.sep);
                lons.append(this.sep);
                displayInfos.append(this.sep);
                titles.append(this.sep);
            }
            lats.append(""+item.getLatitude());
            lons.append(""+item.getLongitude());
            displayInfos.append(""+item.getName());
            titles.append(item.getTitle());
            isFirst = false;
        }

        this.longitudes = lons.toString();
        this.latitudes = lats.toString();
        this.displayInfos = displayInfos.toString();
        this.titles = titles.toString();

        determineCenter();
    }

    private void determineCenter() {
        double lonLow = 1000;
        double lonHigh = -1000;
        double latLow = 1000;
        double latHigh = -1000;

        for (RenderedMapItem item : items) {
            if (item.getLongitude() < lonLow) {
                lonLow = item.getLongitude();
            }
            if (item.getLongitude() > lonHigh) {
                lonHigh = item.getLongitude();
            }
            if (item.getLatitude() < latLow) {
                latLow = item.getLatitude();
            }
            if (item.getLatitude() > latHigh) {
                latHigh = item.getLatitude();
            }
        }

        centerLongitude = ""+(lonLow + ((lonHigh - lonLow) / 2));
        centerLatitude = ""+(latLow + ((latHigh - latLow) / 2));
        minLatitude = String.valueOf(latLow);
        maxLatitude = String.valueOf(latHigh);
        minLongitude = String.valueOf(lonLow);
        maxLongitude = String.valueOf(lonHigh);
    }
    public String getLongitudes() {
        return this.longitudes;

    }
    public String getLatitudes() {
        return this.latitudes;
    }
    public String getDisplayInfos() {
        return this.displayInfos;
    }

    public String getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(String centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public String getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(String centerLatitude) {
        this.centerLatitude = centerLatitude;
    }
    public int getCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    public String getMinLongitude() {
        return minLongitude;
    }

    public void setMinLongitude(String minLongitude) {
        this.minLongitude = minLongitude;
    }

    public String getMinLatitude() {
        return minLatitude;
    }

    public void setMinLatitude(String minLatitude) {
        this.minLatitude = minLatitude;
    }

    public String getMaxLongitude() {
        return maxLongitude;
    }

    public void setMaxLongitude(String maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

    public String getMaxLatitude() {
        return maxLatitude;
    }

    public void setMaxLatitude(String maxLatitude) {
        this.maxLatitude = maxLatitude;
    }
}
