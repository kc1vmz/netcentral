package netcentral.server.object;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class SummaryDashboard {
    private Integer activeNets;
    private Integer activeNetParticipants;
    private Integer scheduledNets;
    private Integer stationsHeard;
    private Integer digipeatersHeard;
    private Integer iGatesHeard;
    private Integer weatherStationsHeard;
    private Integer trackedStations;
    private Integer callsignsHeard;
    private Integer winlinkGatewaysHeard;
    private Integer internetServersHeard;
    private Integer bbsHeard;
    private Integer mmdvmHeard;
    private Integer othersHeard;
    private Integer repeatersHeard;
    private Integer closedNets;
    private Integer closedNetParticipants;
    private Integer users;
    private Integer loggedInUsers;
    private Integer registeredTransceiversTotal;
    private Integer registeredTransceiversReceive;
    private Integer registeredTransceiversTransmit;

    public Integer getActiveNets() {
        return activeNets;
    }
    public void setActiveNets(Integer activeNets) {
        this.activeNets = activeNets;
    }
    public Integer getActiveNetParticipants() {
        return activeNetParticipants;
    }
    public void setActiveNetParticipants(Integer activeNetParticipants) {
        this.activeNetParticipants = activeNetParticipants;
    }
    public Integer getStationsHeard() {
        return stationsHeard;
    }
    public void setStationsHeard(Integer stationsHeard) {
        this.stationsHeard = stationsHeard;
    }
    public Integer getDigipeatersHeard() {
        return digipeatersHeard;
    }
    public void setDigipeatersHeard(Integer digipeatersHeard) {
        this.digipeatersHeard = digipeatersHeard;
    }
    public Integer getiGatesHeard() {
        return iGatesHeard;
    }
    public void setiGatesHeard(Integer iGatesHeard) {
        this.iGatesHeard = iGatesHeard;
    }
    public Integer getWeatherStationsHeard() {
        return weatherStationsHeard;
    }
    public void setWeatherStationsHeard(Integer weatherStationsHeard) {
        this.weatherStationsHeard = weatherStationsHeard;
    }
    public Integer getTrackedStations() {
        return trackedStations;
    }
    public void setTrackedStations(Integer trackedStations) {
        this.trackedStations = trackedStations;
    }
    public Integer getCallsignsHeard() {
        return callsignsHeard;
    }
    public void setCallsignsHeard(Integer callsignsHeard) {
        this.callsignsHeard = callsignsHeard;
    }
    public Integer getWinlinkGatewaysHeard() {
        return winlinkGatewaysHeard;
    }
    public void setWinlinkGatewaysHeard(Integer winlinkGatewaysHeard) {
        this.winlinkGatewaysHeard = winlinkGatewaysHeard;
    }
    public Integer getInternetServersHeard() {
        return internetServersHeard;
    }
    public void setInternetServersHeard(Integer internetServersHeard) {
        this.internetServersHeard = internetServersHeard;
    }
    public Integer getOthersHeard() {
        return othersHeard;
    }
    public void setOthersHeard(Integer othersHeard) {
        this.othersHeard = othersHeard;
    }
    public Integer getRepeatersHeard() {
        return repeatersHeard;
    }
    public void setRepeatersHeard(Integer repeatersHeard) {
        this.repeatersHeard = repeatersHeard;
    }
    public Integer getClosedNets() {
        return closedNets;
    }
    public void setClosedNets(Integer closedNets) {
        this.closedNets = closedNets;
    }
    public Integer getClosedNetParticipants() {
        return closedNetParticipants;
    }
    public void setClosedNetParticipants(Integer closedNetParticipants) {
        this.closedNetParticipants = closedNetParticipants;
    }
    public Integer getUsers() {
        return users;
    }
    public void setUsers(Integer users) {
        this.users = users;
    }
    public Integer getLoggedInUsers() {
        return loggedInUsers;
    }
    public void setLoggedInUsers(Integer loggedInUsers) {
        this.loggedInUsers = loggedInUsers;
    }
    public Integer getRegisteredTransceiversTotal() {
        return registeredTransceiversTotal;
    }
    public void setRegisteredTransceiversTotal(Integer registeredTransceiversTotal) {
        this.registeredTransceiversTotal = registeredTransceiversTotal;
    }
    public Integer getRegisteredTransceiversReceive() {
        return registeredTransceiversReceive;
    }
    public void setRegisteredTransceiversReceive(Integer registeredTransceiversReceive) {
        this.registeredTransceiversReceive = registeredTransceiversReceive;
    }
    public Integer getRegisteredTransceiversTransmit() {
        return registeredTransceiversTransmit;
    }
    public void setRegisteredTransceiversTransmit(Integer registeredTransceiversTransmit) {
        this.registeredTransceiversTransmit = registeredTransceiversTransmit;
    }
    public Integer getBbsHeard() {
        return bbsHeard;
    }
    public void setBbsHeard(Integer bbsHeard) {
        this.bbsHeard = bbsHeard;
    }
    public Integer getMmdvmHeard() {
        return mmdvmHeard;
    }
    public void setMmdvmHeard(Integer mmdvmHeard) {
        this.mmdvmHeard = mmdvmHeard;
    }
    public Integer getScheduledNets() {
        return scheduledNets;
    }
    public void setScheduledNets(Integer scheduledNets) {
        this.scheduledNets = scheduledNets;
    }
}
