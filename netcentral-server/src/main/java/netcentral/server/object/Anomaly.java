package netcentral.server.object;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Anomaly {
    private String id;
    private String callsign;
    private String packetText;
    private String description;
    private ZonedDateTime time;
    private String prettyTime;

    public Anomaly() {
    }
    public Anomaly(String id, String callsign, String packetText, String description, ZonedDateTime time) {
        this.id = id;
        this.callsign = callsign;
        this.packetText = packetText;
        this.description = description;
        setTime(time);
    }
    public Anomaly(Anomaly anomaly) {
        if (anomaly != null) {
            this.id = anomaly.getId();
            this.callsign = anomaly.getCallsign();
            this.description = anomaly.getDescription();
            this.packetText = anomaly.getPacketText();
            setTime(anomaly.getTime());
        }
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
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPacketText() {
        return packetText;
    }
    public void setPacketText(String packetText) {
        this.packetText = packetText;
    }
    public ZonedDateTime getTime() {
        return time;
    }
    public void setTime(ZonedDateTime time) {
        this.time = time;
        this.prettyTime = PrettyZonedDateTimeFormatter.format(time);
    }
    public String getPrettyTime() {
        return prettyTime;
    }
    public void setPrettyTime(String prettyTime) {
        this.prettyTime = prettyTime;
    }
}
