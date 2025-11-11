package netcentral.transceiver.kenwood.object;

public class APRSAuthenticationInfo {

    private String callsign;
    private String password;

    public APRSAuthenticationInfo(String callsign, String password) {
        setCallsign(callsign);
        setPassword(password);        
    }
    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
