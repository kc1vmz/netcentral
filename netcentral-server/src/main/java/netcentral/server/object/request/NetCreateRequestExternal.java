package netcentral.server.object.request;

import java.util.ArrayList;
import java.util.List;

import io.micronaut.serde.annotation.Serdeable;
import netcentral.server.object.ExpectedParticipant;
import netcentral.server.object.Net;

@Serdeable
public class NetCreateRequestExternal {
    //@NotBlank String callsign, @NotBlank String name, String description, String voiceFrequency, String lat, String lon, String announce, boolean checkinReminder, String checkinMessage, boolean open,  boolean participantInviteAllowed, String expectedCallsigns
    private String callsign;
    private String name;
    private String description;
    private String voiceFrequency;
    private String lat;
    private String lon;
    private String announce;
    private String checkinReminder;
    private String checkinMessage;
    private boolean open;
    private boolean participantInviteAllowed; 
    private String expectedCallsigns;
    private String creatorName;
   
    
    public NetCreateRequestExternal() {
    }

    public NetCreateRequestExternal(NetCreateRequest req, String creatorName) {
        this.callsign = req.callsign();
        this.name = req.name();
        this.description = req.description();
        this.voiceFrequency = req.voiceFrequency();
        this.lat = req.lat();
        this.lon = req.lon();
        this.announce = req.announce();
        this.creatorName = req.creatorName();
        this.checkinReminder = req.checkinReminder();
        this.checkinMessage = req.checkinMessage();
        this.open = req.open();
        this.participantInviteAllowed = req.participantInviteAllowed();
        this.expectedCallsigns = req.expectedCallsigns();
    }

    public NetCreateRequestExternal(String callsign, String name, String description, String voiceFrequency, String lat, String lon, 
                                                    String announce, String checkinReminder, String checkinMessage,
                                                    boolean open, boolean participantInviteAllowed, String expectedCallsigns, String creatorName) {
        this.callsign = callsign;
        this.name = name;
        this.description = description;
        this.voiceFrequency = voiceFrequency;
        this.lat = lat;
        this.lon = lon;
        this.announce = announce;
        this.creatorName = creatorName;
        this.checkinReminder = checkinReminder;
        this.checkinMessage = checkinMessage;
        this.open = open;
        this.participantInviteAllowed = participantInviteAllowed;
        this.expectedCallsigns = expectedCallsigns;
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
    public String getAnnounce() {
        return announce;
    }
    public void setAnnounce(String announce) {
        this.announce = announce;
    }
    public Net getNet() {
        Net ret = new Net(getCallsign(), getName(), getDescription(), getVoiceFrequency(), null, null, 
                                    getLat(), getLon(), 
                                    (getAnnounce().equalsIgnoreCase("true")) ? true : false,
                                    getCreatorName(),  
                                    (getCheckinReminder().equalsIgnoreCase("true")) ? true : false,
                                    getCheckinMessage(), isOpen(), isParticipantInviteAllowed());
        return ret;
    }

    public List<ExpectedParticipant> getExpectedParticipants() {
        List<ExpectedParticipant> ret = new ArrayList<>();

        try {
            if (expectedCallsigns != null) {
                String [] callsigns = expectedCallsigns.split("[\\s,]+");
                if (callsigns != null) {
                    for (String callsign : callsigns) {
                        if ((callsign != null) && (callsign.length()>0)) {
                            ret.add(new ExpectedParticipant(callsign));
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        return ret;
    }

    public String getCheckinReminder() {
        return checkinReminder;
    }
    public void setCheckinReminder(String checkinReminder) {
        this.checkinReminder = checkinReminder;
    }
    public String getCheckinMessage() {
        return checkinMessage;
    }
    public void setCheckinMessage(String checkinMessage) {
        this.checkinMessage = checkinMessage;
    }
    public boolean isOpen() {
        return open;
    }
    public void setOpen(boolean open) {
        this.open = open;
    }
    public boolean isParticipantInviteAllowed() {
        return participantInviteAllowed;
    }
    public void setParticipantInviteAllowed(boolean participantInviteAllowed) {
        this.participantInviteAllowed = participantInviteAllowed;
    }
    public String getExpectedCallsigns() {
        return expectedCallsigns;
    }
    public void setExpectedCallsigns(String expectedCallsigns) {
        this.expectedCallsigns = expectedCallsigns;
    }
    public String getCreatorName() {
        return creatorName;
    }
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
