package netcentral.server.object;

import java.time.ZonedDateTime;

import com.kc1vmz.netcentral.aprsobject.utils.PrettyZonedDateTimeFormatter;

public class Activity {
    private String id;
    private ZonedDateTime time;
    private String text;
    private User user;
    private String prettyTime;

    public Activity() {
    }
    public Activity(String id, ZonedDateTime time, String text, User user) {
        this.id = id;
        this.time = time;
        this.text = text;
        this.user = user;
        this.prettyTime = PrettyZonedDateTimeFormatter.format(time);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public ZonedDateTime getTime() {
        return time;
    }
    public void setTime(ZonedDateTime time) {
        this.time = time;
        this.prettyTime = PrettyZonedDateTimeFormatter.format(time);
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getPrettyTime() {
        return prettyTime;
    }
    public void setPrettyTime(String prettyTime) {
        this.prettyTime = prettyTime;
    }
}
