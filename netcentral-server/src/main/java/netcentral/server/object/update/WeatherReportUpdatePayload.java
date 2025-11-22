package netcentral.server.object.update;

import com.kc1vmz.netcentral.aprsobject.object.APRSWeatherReport;

public class WeatherReportUpdatePayload {
    private String callsign;
    private String action;
    private APRSWeatherReport object;

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public APRSWeatherReport getObject() {
        return object;
    }
    public void setObject(APRSWeatherReport object) {
        this.object = object;
    }
}
