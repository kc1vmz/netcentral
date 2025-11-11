package netcentral.server.object.request;


public class ObjectCreateRequestExternal {
    private String type;
    private String callsign;
    private String description;
    private String voiceFrequency;
    private String lat;
    private String lon;
    private boolean up;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLon() {
        return lon;
    }
    public void setLon(String lon) {
        this.lon = lon;
    }
    public boolean isUp() {
        return up;
    }
    public void setUp(boolean up) {
        this.up = up;
    }
    public String getVoiceFrequency() {
        return voiceFrequency;
    }
    public void setVoiceFrequency(String voiceFrequency) {
        this.voiceFrequency = voiceFrequency;
    }
}

