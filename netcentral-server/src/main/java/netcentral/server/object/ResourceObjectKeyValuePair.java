package netcentral.server.object;

public class ResourceObjectKeyValuePair {
    private String key;
    private String value;
    private boolean broadcast;

    public ResourceObjectKeyValuePair() {
    }

    public ResourceObjectKeyValuePair(String key, String value, boolean broadcast) {
        this.key = key;
        this.value = value;
        this.broadcast = broadcast;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public boolean isBroadcast() {
        return broadcast;
    }
    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }
}
