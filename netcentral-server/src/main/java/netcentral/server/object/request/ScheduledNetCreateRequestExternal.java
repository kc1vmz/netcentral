package netcentral.server.object.request;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import io.micronaut.serde.annotation.Serdeable;
import netcentral.server.enums.ScheduledNetType;
import netcentral.server.object.ExpectedParticipant;
import netcentral.server.object.ScheduledNet;

@Serdeable
public class ScheduledNetCreateRequestExternal {
    //@NotBlank String callsign, @NotBlank String name, String description, Integer type, String voiceFrequency, String lat, String lon, String announce, int dayStart, String timeStartStr, int duration
    private String callsign;
    private String name;
    private String description;
    private Integer type;
    private String voiceFrequency;
    private String lat;
    private String lon;
    private String announce;
    private int dayStart;
    private String timeStartStr;
    private int duration;
    private String creatorName;
    private String checkinReminder;
    private String checkinMessage;
    private boolean open;
    private boolean participantInviteAllowed; 
    private String expectedCallsigns;
   
    public ScheduledNetCreateRequestExternal() {
    }

    //ScheduledNetCreateRequest(@NotBlank String callsign, @NotBlank String name, String description, Integer type, String voiceFrequency, String lat, String lon, String announce, int dayStart, String timeStartStr, int duration)
    public ScheduledNetCreateRequestExternal(ScheduledNetCreateRequest req, String creatorName) {
        this.callsign = req.callsign();
        this.name = req.name();
        this.description = req.description();
        this.voiceFrequency = req.voiceFrequency();
        this.lat = req.lat();
        this.lon = req.lon();
        this.announce = req.announce();
        this.creatorName = creatorName;
        this.type = req.type();
        this.dayStart = req.dayStart();
        this.timeStartStr = req.timeStartStr();
        this.duration = req.duration();
        this.checkinReminder = req.checkinReminder();
        this.checkinMessage = req.checkinMessage();
        this.open = req.open();
        this.participantInviteAllowed = req.participantInviteAllowed();
        this.expectedCallsigns = req.expectedCallsigns();
    }

    public ScheduledNetCreateRequestExternal(String callsign, String name, String description, Integer type, String voiceFrequency, String lat, String lon, 
                                                    String announce, int dayStart, String timeStartStr, int duration, String creatorName, String checkinReminder, String checkinMessage,
                                                    boolean open, boolean participantInviteAllowed, String expectedCallsigns) {
        this.callsign = callsign;
        this.name = name;
        this.description = description;
        this.voiceFrequency = voiceFrequency;
        this.lat = lat;
        this.lon = lon;
        this.announce = announce;
        this.creatorName = creatorName;
        this.type = type;
        this.dayStart = dayStart;
        this.timeStartStr = timeStartStr;
        this.duration = duration;
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
    public String getCreatorName() {
        return creatorName;
    }
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    public int getDayStart() {
        return dayStart;
    }
    public void setDayStart(int dayStart) {
        this.dayStart = dayStart;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public String getAnnounce() {
        return announce;
    }
    public void setAnnounce(String announce) {
        this.announce = announce;
    }
    public String getTimeStartStr() {
        return timeStartStr;
    }
    public void setTimeStartStr(String timeStartStr) {
        this.timeStartStr = timeStartStr;
    }
    public ScheduledNet getScheduledNet() {
        ScheduledNet ret = new ScheduledNet();
        if (ScheduledNetType.values()[getType()].equals(ScheduledNetType.ONE_TIME_ONLY)) {
            ZonedDateTime nextStartTime = convertTimeStartStringToZDT(getTimeStartStr());
            ZonedDateTime lastStartTime= ZonedDateTime.now();
            ret = new ScheduledNet(getCallsign(), getName(), getDescription(),  ScheduledNetType.values()[getType()], getVoiceFrequency(), 
                                                getLat(), getLon(), 
                                                (getAnnounce().equalsIgnoreCase("true")) ? true : false,
                                                getCreatorName(), 
                                                getDayStart(), 0, getDuration(), lastStartTime, nextStartTime,
                                                (getCheckinReminder().equalsIgnoreCase("true")) ? true : false, getCheckinMessage(),isOpen(), isParticipantInviteAllowed()
                                            );
        } else {
            ret = new ScheduledNet(getCallsign(), getName(), getDescription(),  ScheduledNetType.values()[getType()], getVoiceFrequency(), 
                                    getLat(), getLon(), 
                                    (getAnnounce().equalsIgnoreCase("true")) ? true : false,
                                    getCreatorName(),  
                                    getDayStart(), convertTimeStartStringToInt(getTimeStartStr()), getDuration(),
                                    (getCheckinReminder().equalsIgnoreCase("true")) ? true : false, getCheckinMessage(), isOpen(), isParticipantInviteAllowed());
        }
        ret.setNextStartTime(ret.calculateNextStartTime());

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

    private int convertTimeStartStringToInt(String timeStartStr) {
        int ret = 0;
        // string HH:MM (24hr time)
        if (timeStartStr == null) {
            return ret;
        }
        String [] parts = timeStartStr.split(":");
        if ((parts != null) && (parts.length == 2)) {
            // return HHMM in decimal ex: 1745
            ret = ((Integer.parseInt(parts[0])) * 100) + (Integer.parseInt(parts[1]));
        }
        return ret;
    }

    private ZonedDateTime convertTimeStartStringToZDT(String timeStartStr) {
        // form YYYY-MM-DD HH:MM
        String [] dtValues = timeStartStr.split(" ");
        if ((dtValues != null) && (dtValues.length == 2)) {
            String [] ymdValues = dtValues[0].split("-");
            if ((ymdValues != null) && (ymdValues.length == 3)) {
                String [] hmValues = dtValues[1].split(":");
                if ((hmValues != null) && (hmValues.length == 2)) { 
                    return ZonedDateTime.of(Integer.parseInt(ymdValues[0]), Integer.parseInt(ymdValues[1]), Integer.parseInt(ymdValues[2]), 
                                                Integer.parseInt(hmValues[0]), Integer.parseInt(hmValues[1]), 0, 0, ZoneId.systemDefault());
                }
            }
        }
        return ZonedDateTime.now().plusYears(300);
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
}
