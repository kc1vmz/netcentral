package netcentral.server.object;

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

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class CompletedNet {
    private String callsign;
    private String name;
    private String description;
    private String voiceFrequency;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private String completedNetId;
    private String prettyStartTime;
    private String prettyEndTime;
    private String creatorName;
    private String checkinMessage;
    private boolean open;
    private boolean participantInviteAllowed;

    public CompletedNet() {
    }
    public CompletedNet(String callsign, String name, String description, String voiceFrequency, ZonedDateTime startTime, ZonedDateTime endTime, String completedNetId,
                                String creatorName, String checkinMessage, boolean open, boolean participantInviteAllowed) {
        this.callsign = callsign;
        this.name = name;
        this.description = description;
        this.voiceFrequency = voiceFrequency;
        this.completedNetId = completedNetId;
        setStartTime(startTime);
        setEndTime(endTime);
        this.creatorName = creatorName;
        this.checkinMessage = checkinMessage;
        this.open = open;
        this.participantInviteAllowed = participantInviteAllowed;
    }
    public CompletedNet(CompletedNet net) {
        if (net != null) {
            this.callsign = net.getCallsign();
            this.name = net.getName();
            this.description = net.getDescription();
            this.voiceFrequency = net.getVoiceFrequency();
            this.completedNetId = net.getCompletedNetId();
            setStartTime(net.getStartTime());
            setEndTime(net.getEndTime());
            this.creatorName = net.getCreatorName();
            this.checkinMessage = net.getCheckinMessage();
            this.open = net.isOpen();
            this.participantInviteAllowed = net.isParticipantInviteAllowed();
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
    public ZonedDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
        this.prettyEndTime = PrettyZonedDateTimeFormatter.format(endTime);
    }
    public String getPrettyStartTime() {
        return prettyStartTime;
    }
    public void setPrettyStartTime(String prettyStartTime) {
        this.prettyStartTime = prettyStartTime;
    }
    public String getPrettyEndTime() {
        return prettyEndTime;
    }
    public void setPrettyEndTime(String prettyEndTime) {
        this.prettyEndTime = prettyEndTime;
    }
    public String getCreatorName() {
        return creatorName;
    }
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
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
}
