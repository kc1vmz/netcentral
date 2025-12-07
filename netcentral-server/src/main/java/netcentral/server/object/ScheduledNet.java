package netcentral.server.object;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

import io.micronaut.serde.annotation.Serdeable;
import netcentral.server.enums.ScheduledNetType;

@Serdeable
public class ScheduledNet {
    private String callsign;
    private String name;
    private String description;
    private String voiceFrequency;
    private String lat;
    private String lon;
    private boolean announce;
    private String creatorName;
    private ScheduledNetType type;
    private int dayStart;
    private int timeStart;
    private int duration;
    private ZonedDateTime lastStartTime;
    private ZonedDateTime nextStartTime;
    private String prettyLastStartTime;
    private String prettyNextStartTime;
    private boolean checkinReminder;
    private String checkinMessage;
    private boolean open;
    private boolean participantInviteAllowed;
    
    public ScheduledNet() {
    }

    public ScheduledNet(String callsign, String name, String description, ScheduledNetType type, String voiceFrequency, String lat, String lon,
                                boolean announce, String creatorName, int dayStart, int timeStart, int duration, boolean checkinReminder, String checkinMessage,
                            boolean open, boolean participantInviteAllowed ) {
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
        this.timeStart = timeStart;
        this.duration = duration;
        setLastStartTime(ZonedDateTime.now().minusYears(1));
        setNextStartTime(calculateNextStartTime());
        this.checkinReminder = checkinReminder;
        this.checkinMessage = checkinMessage;
        this.open = open;
        this.participantInviteAllowed = participantInviteAllowed;
    }

    public ScheduledNet(String callsign, String name, String description, ScheduledNetType type, String voiceFrequency, 
                                String lat, String lon, boolean announce, String creatorName, int dayStart, int timeStart, 
                                int duration, ZonedDateTime lastStartTime, ZonedDateTime nextStartTime, boolean checkinReminder, String checkinMessage,
                                boolean open, boolean participantInviteAllowed) {
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
        this.timeStart = timeStart;
        this.duration = duration;
        setLastStartTime(lastStartTime);
        setNextStartTime(nextStartTime);
        this.checkinReminder = checkinReminder;
        this.checkinMessage = checkinMessage;
        this.open = open;
        this.participantInviteAllowed = participantInviteAllowed;
    }

    public ScheduledNet(ScheduledNet scheduledNet) {
        if (scheduledNet != null) {
            this.callsign = scheduledNet.getCallsign();
            this.name = scheduledNet.getName();
            this.description = scheduledNet.getDescription();
            this.voiceFrequency = scheduledNet.getVoiceFrequency();
            this.lat = scheduledNet.getLat();
            this.lon = scheduledNet.getLon();
            this.announce = scheduledNet.isAnnounce();
            this.creatorName = scheduledNet.getCreatorName();
            this.type = scheduledNet.getType();
            this.dayStart = scheduledNet.getDayStart();
            this.timeStart = scheduledNet.getTimeStart();
            this.duration = scheduledNet.getDuration();
            setLastStartTime(scheduledNet.getLastStartTime());
            setNextStartTime(scheduledNet.getNextStartTime());
            this.checkinReminder = scheduledNet.isCheckinReminder();
            this.checkinMessage = scheduledNet.getCheckinMessage();
            this.open = scheduledNet.isOpen();
            this.participantInviteAllowed = scheduledNet.isParticipantInviteAllowed();
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
    public ScheduledNetType getType() {
        return type;
    }
    public void setType(ScheduledNetType type) {
        this.type = type;
    }
    public int getDayStart() {
        return dayStart;
    }
    public void setDayStart(int dayStart) {
        this.dayStart = dayStart;
    }
    public int getTimeStart() {
        return timeStart;
    }
    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public ZonedDateTime getLastStartTime() {
        return lastStartTime;
    }
    public void setLastStartTime(ZonedDateTime lastStartTime) {
        this.lastStartTime = lastStartTime;
        this.prettyLastStartTime = PrettyZonedDateTimeFormatter.format(lastStartTime);
    }
    public ZonedDateTime getNextStartTime() {
        return nextStartTime;
    }
    public void setNextStartTime(ZonedDateTime nextStartTime) {
        this.nextStartTime = nextStartTime;
        this.prettyNextStartTime = PrettyZonedDateTimeFormatter.format(nextStartTime);
    }
    public String getPrettyLastStartTime() {
        return prettyLastStartTime;
    }
    public void setPrettyLastStartTime(String prettyLastStartTime) {
        this.prettyLastStartTime = prettyLastStartTime;
    }
    public String getPrettyNextStartTime() {
        return prettyNextStartTime;
    }
    public void setPrettyNextStartTime(String prettyNextStartTime) {
        this.prettyNextStartTime = prettyNextStartTime;
    }
    public ZonedDateTime calculateNextStartTime() {
        ZonedDateTime time;
        int timeStart = this.getTimeStart();
        int hour = (timeStart) / 100;
        int minute = timeStart - ((timeStart / 100) * 100);

        if (this.type.equals(ScheduledNetType.UNKNOWN)) {
            time = ZonedDateTime.now().plusYears(100);
        } else if (this.type.equals(ScheduledNetType.ONE_TIME_ONLY)) {
            time = this.getNextStartTime();
        } else if (this.type.equals(ScheduledNetType.DAILY)) {
            time = ZonedDateTime.now();
            time = ZonedDateTime.of(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), hour, minute, 0, 0, ZoneId.systemDefault());
            // may be earlier than now
            if (time.isBefore(this.getLastStartTime())) {
                time = time.plusDays(1);  // move up to next day
            }
        } else if (this.type.equals(ScheduledNetType.WEEKLY)) {
            time = ZonedDateTime.now();
            int day = time.getDayOfWeek().getValue();
            int dayAdd = day - this.getDayStart();
            if (dayAdd < 0) {
                dayAdd *= -1;  
            }
            int dayOfMonth = time.getDayOfMonth() + dayAdd;
            time = ZonedDateTime.of(time.getYear(), time.getMonthValue(), dayOfMonth, hour, minute, 0, 0, ZoneId.systemDefault());
            if (time.isBefore(ZonedDateTime.now())) {
                time = time.plusDays(7);  // move up to next week
            }
        } else if (this.type.equals(ScheduledNetType.MONTHLY_DAY)) {
            time = ZonedDateTime.now();
            int day = time.getDayOfMonth();
            int dayAdd = day - this.getDayStart();
            if (dayAdd < 0) {
                dayAdd *= -1;  
            }
            int dayOfMonth = time.getDayOfMonth() + dayAdd;
            time = ZonedDateTime.of(time.getYear(), time.getMonthValue(), dayOfMonth, hour, minute, 0, 0, ZoneId.systemDefault());
            if (time.isBefore(ZonedDateTime.now())) {
                time = time.plusMonths(1);  // move up to next month
            }
        } else if (this.type.equals(ScheduledNetType.MONTHLY_RELATIVE)) {
            // need to find the Nth day of the current month, and if past, bump a month up
            time = ZonedDateTime.now();
            int correctMonth = time.getMonthValue();
            time = ZonedDateTime.of(time.getYear(), time.getMonthValue(), 1, hour, minute, 0, 0, ZoneId.systemDefault());

            // first of month
            int day = time.getDayOfWeek().getValue();  
            int dayOfWeek = (this.getDayStart() - (this.getDayStart() / 10) * 10) / 10;  // two digits - 1-5, 6=last  ; 1-7 day of week
            int weekOffset = (this.getDayStart() / 10)-1;// number of weeks to move up from first week
            if (day < dayOfWeek) {
                time = time.plusDays(dayOfWeek - day);
            } else {
                time = time.plusWeeks(1).minusDays(day-dayOfWeek);
            }
            if (weekOffset > 5) {
                weekOffset = 5;
            }
            time = time.plusWeeks(weekOffset);
            // may be out of month = lets walk back into month
            while (time.getMonthValue() != correctMonth) {
                time = time.minusWeeks(1);
            }
            // may be earlier than now
            if (time.isBefore(ZonedDateTime.now())) {
                time = time.plusMonths(1);  // move up to next month
            }
        } else {
            time = ZonedDateTime.now().plusYears(100);
        }
        return time;
    }
    public boolean isCheckinReminder() {
        return checkinReminder;
    }
    public void setCheckinReminder(boolean checkinReminder) {
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
}
