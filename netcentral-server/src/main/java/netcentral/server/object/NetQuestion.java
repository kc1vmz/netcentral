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
public class NetQuestion {
    private String netQuestionId;
    private String completedNetId;
    private int number;
    private ZonedDateTime askedTime;
    private String prettyAskedTime;
    private Boolean active;
    private int reminderMinutes;
    private String questionText;
    private ZonedDateTime nextReminderTime;
    private String prettyNextReminderTime;

    public NetQuestion() {
    }
    public NetQuestion(String netQuestionId, String completedNetId, int number, ZonedDateTime askedTime, Boolean active, int reminderMinutes, String questionText, ZonedDateTime nextReminderTime) {
        this.netQuestionId = netQuestionId;
        this.completedNetId = completedNetId;
        this.number = number;
        this.setAskedTime(askedTime);
        this.setNextReminderTime(nextReminderTime);
        this.active = active;
        this.reminderMinutes = reminderMinutes;
        this.questionText = questionText;
    }
    public NetQuestion(NetQuestion netQuestion) {
        if (netQuestion != null) {
            this.netQuestionId = netQuestion.getNetQuestionId();
            this.completedNetId = netQuestion.getCompletedNetId();
            this.number = netQuestion.getNumber();
            this.setAskedTime(netQuestion.getAskedTime());
            this.setNextReminderTime(netQuestion.getNextReminderTime());
            this.active = netQuestion.getActive();
            this.reminderMinutes = netQuestion.getReminderMinutes();
            this.questionText = netQuestion.getQuestionText();
        }
    }
    public String getNetQuestionId() {
        return netQuestionId;
    }
    public void setNetQuestionId(String netQuestionId) {
        this.netQuestionId = netQuestionId;
    }
    public String getCompletedNetId() {
        return completedNetId;
    }
    public void setCompletedNetId(String completedNetId) {
        this.completedNetId = completedNetId;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public ZonedDateTime getAskedTime() {
        return askedTime;
    }
    public void setAskedTime(ZonedDateTime askedTime) {
        this.askedTime = askedTime;
        this.prettyAskedTime = PrettyZonedDateTimeFormatter.format(askedTime);
    }
    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public int getReminderMinutes() {
        return reminderMinutes;
    }
    public void setReminderMinutes(int reminderMinutes) {
        this.reminderMinutes = reminderMinutes;
    }
    public String getQuestionText() {
        return questionText;
    }
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    public String getPrettyAskedTime() {
        return prettyAskedTime;
    }
    public void setPrettyAskedTime(String prettyAskedTime) {
        this.prettyAskedTime = prettyAskedTime;
    }
    public ZonedDateTime getNextReminderTime() {
        return nextReminderTime;
    }
    public void setNextReminderTime(ZonedDateTime nextReminderTime) {
        this.nextReminderTime = nextReminderTime;
        this.prettyNextReminderTime = PrettyZonedDateTimeFormatter.format(nextReminderTime);
    }
    public String getPrettyNextReminderTime() {
        return prettyNextReminderTime;
    }
    public void setPrettyNextReminderTime(String prettyNextReminderTime) {
        this.prettyNextReminderTime = prettyNextReminderTime;
    }
}
