package netcentral.server.object;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class NetMessage {
    private String id;
    private String completedNetId;
    private String callsignFrom;
    private String message;
    private ZonedDateTime receivedTime;
    private String prettyReceivedTime;

    public NetMessage() {
    }
    public NetMessage(String id, String completedNetId, String callsignFrom, String message, ZonedDateTime receivedTime) {
        this.id = id;
        this.completedNetId = completedNetId;
        this.callsignFrom = callsignFrom;
        this.message = message;
        setReceivedTime(receivedTime);
    }
    public NetMessage(NetMessage netMessage) {
        if (netMessage != null) {
            this.id = netMessage.getCompletedNetId();
            this.completedNetId = netMessage.getCompletedNetId();
            this.callsignFrom = netMessage.getCallsignFrom();
            this.message = netMessage.getMessage();
            setReceivedTime(netMessage.getReceivedTime());
        }
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCompletedNetId() {
        return completedNetId;
    }
    public void setCompletedNetId(String completedNetId) {
        this.completedNetId = completedNetId;
    }
    public String getCallsignFrom() {
        return callsignFrom;
    }
    public void setCallsignFrom(String callsignFrom) {
        this.callsignFrom = callsignFrom;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public ZonedDateTime getReceivedTime() {
        return receivedTime;
    }
    public void setReceivedTime(ZonedDateTime receivedTime) {
        this.receivedTime = receivedTime;
        this.prettyReceivedTime = PrettyZonedDateTimeFormatter.format(receivedTime);
    }
    public String getPrettyReceivedTime() {
        return prettyReceivedTime;
    }
    public void setPrettyReceivedTime(String prettyReceivedTime) {
        this.prettyReceivedTime = prettyReceivedTime;
    }
}
