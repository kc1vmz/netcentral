package netcentral.server.object;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Callsign {
    private String callsign;
    private String name;
    private String country;
    private String state;
    private String license;

    public Callsign() {
    }
    public Callsign(String callsign, String name, String country, String state, String license) {
        this.callsign = callsign;
        this.name = name;
        this.country = country;
        this.state = state;
        this.license = license;
    }
    public Callsign(Callsign callsign) {
        if (callsign != null) {
            this.callsign = callsign.getCallsign();
            this.name = callsign.getName();
            this.country = callsign.getCountry();
            this.state = callsign.getState();
            this.license = callsign.getLicense();
        }
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getLicense() {
        return license;
    }
    public void setLicense(String license) {
        this.license = license;
    }
}
