package netcentral.server.utils;

import java.time.ZonedDateTime;

public class NetQuestionReportItem {
    private String label;
    private ZonedDateTime time;
    private String callsign;
    private String text;

    public NetQuestionReportItem() {
    }
    public NetQuestionReportItem(ZonedDateTime time, String text) {
        this.time = time;
        this.text = text;
        this.label = "Q";
        this.callsign = "---------";
    }
    public NetQuestionReportItem(ZonedDateTime time, String text, String callsign) {
        this.time = time;
        this.text = text;
        this.callsign = callsign;
        this.label = "A";
    }
    public NetQuestionReportItem(String text) {
        this.text = text;
        this.label = "A";
    }

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public ZonedDateTime getTime() {
        return time;
    }
    public void setTime(ZonedDateTime time) {
        this.time = time;
    }
    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
