package netcentral.server.object;

import netcentral.server.utils.DateStrUtils;

public class Report309Entry {

    private String from;
    private String startTime;
    private String endTime;
    private String message;

    public Report309Entry(CompletedParticipant participant) {
        from = String.format("%s (%s)", participant.getCallsign(), (participant.getTacticalCallsign() == null) ? "": participant.getTacticalCallsign());
        startTime = DateStrUtils.getTimeStr(participant.getStartTime());
        endTime = DateStrUtils.getTimeStr(participant.getEndTime());
        message = String.format("%s:%s:%sW", participant.getElectricalPowerType(), participant.getRadioStyle(), participant.getTransmitPower());
    }

    public Report309Entry(CompletedExpectedParticipant participant) {
        from = String.format("%s", participant.getCallsign());
        startTime = "Did not participate";
        endTime = "";
        message = "";
    }

    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
