package netcentral.server.object;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Net {
    private String callsign;
    private String name;
    private String description;
    private String voiceFrequency;
    private ZonedDateTime startTime;
    private String completedNetId;
    private Integer participantCount;
    private String prettyStartTime;
    private String lat;
    private String lon;
    private boolean announce;
    private String creatorName;
    private boolean checkinReminder;

    public Net() {
    }
    public Net(String callsign, String name, String description, String voiceFrequency, ZonedDateTime startTime, String completedNetId, String lat, String lon, boolean announce, String creatorName, boolean checkinReminder) {
        this.callsign = callsign;
        this.name = name;
        this.description = description;
        this.voiceFrequency = voiceFrequency;
        setStartTime(startTime);
        this.completedNetId = completedNetId;
        this.lat = lat;
        this.lon = lon;
        this.announce = announce;
        this.creatorName = creatorName;
        this.checkinReminder = checkinReminder;
    }
    public Net(Net net) {
        if (net != null) {
            this.callsign = net.getCallsign();
            this.name = net.getName();
            this.description = net.getDescription();
            this.voiceFrequency = net.getVoiceFrequency();
            setStartTime(net.getStartTime());
            this.completedNetId = net.getCompletedNetId();
            this.participantCount = net.getParticipantCount();
            this.lat = net.getLat();
            this.lon = net.getLon();
            this.announce = net.isAnnounce();
            this.creatorName = net.getCreatorName();
            this.checkinReminder = net.isCheckinReminder();
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getVoiceFrequency() {
        return voiceFrequency;
    }
    public void setVoiceFrequency(String voiceFrequency) {
        this.voiceFrequency = voiceFrequency;
    }
    public ZonedDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
        this.prettyStartTime = PrettyZonedDateTimeFormatter.format(startTime);
    }
    public String getCompletedNetId() {
        return completedNetId;
    }
    public void setCompletedNetId(String completedNetId) {
        this.completedNetId = completedNetId;
    }
    public Integer getParticipantCount() {
        return participantCount;
    }
    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }
    public String getPrettyStartTime() {
        return prettyStartTime;
    }
    public void setPrettyStartTime(String prettyStartTime) {
        this.prettyStartTime = prettyStartTime;
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
    public boolean isAnnounce() {
        return announce;
    }
    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }
    public String getCreatorName() {
        return creatorName;
    }
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    public boolean isCheckinReminder() {
        return checkinReminder;
    }
    public void setCheckinReminder(boolean checkinReminder) {
        this.checkinReminder = checkinReminder;
    }
}
