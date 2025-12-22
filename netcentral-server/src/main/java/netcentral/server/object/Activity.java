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
