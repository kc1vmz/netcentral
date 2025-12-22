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
public class NetQuestionAnswer {
    private String netQuestionAnswerId;
    private String netQuestionId;
    private String completedNetId;
    private ZonedDateTime answeredTime;
    private String prettyAnsweredTime;
    private String answerText;
    private String callsign;

    public NetQuestionAnswer() {
    }
    public NetQuestionAnswer(String netQuestionAnswerId, String netQuestionId, String completedNetId, String callsign, ZonedDateTime answeredTime, String answerText) {
        this.netQuestionAnswerId = netQuestionAnswerId;
        this.netQuestionId = netQuestionId;
        this.completedNetId = completedNetId;
        this.setAnsweredTime(answeredTime);
        this.callsign = callsign;
        this.answerText = answerText;
    }
    public NetQuestionAnswer(NetQuestionAnswer netQuestionAnswer) {
        if (netQuestionAnswer != null) {
            this.netQuestionAnswerId = netQuestionAnswer.getNetQuestionAnswerId();
            this.netQuestionId = netQuestionAnswer.getNetQuestionId();
            this.completedNetId = netQuestionAnswer.getCompletedNetId();
            this.setAnsweredTime(netQuestionAnswer.getAnsweredTime());
            this.callsign = netQuestionAnswer.getCallsign();
            this.answerText = netQuestionAnswer.getAnswerText();
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
    public String getNetQuestionAnswerId() {
        return netQuestionAnswerId;
    }
    public void setNetQuestionAnswerId(String netQuestionAnswerId) {
        this.netQuestionAnswerId = netQuestionAnswerId;
    }
    public ZonedDateTime getAnsweredTime() {
        return answeredTime;
    }
    public void setAnsweredTime(ZonedDateTime answeredTime) {
        this.prettyAnsweredTime = PrettyZonedDateTimeFormatter.format(answeredTime);
        this.answeredTime = answeredTime;
    }
    public String getPrettyAnsweredTime() {
        return prettyAnsweredTime;
    }
    public void setPrettyAnsweredTime(String prettyAnsweredTime) {
        this.prettyAnsweredTime = prettyAnsweredTime;
    }
    public String getAnswerText() {
        return answerText;
    }
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
}
