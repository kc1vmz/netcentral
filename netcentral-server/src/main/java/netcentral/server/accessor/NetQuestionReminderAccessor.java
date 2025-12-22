package netcentral.server.accessor;

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
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.Net;
import netcentral.server.object.NetQuestion;
import netcentral.server.object.NetQuestionAnswer;
import netcentral.server.object.Participant;
import netcentral.server.object.User;

@Singleton
public class NetQuestionReminderAccessor {
    private static final Logger logger = LogManager.getLogger(NetQuestionReminderAccessor.class);
    private boolean stop = false;

    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;
    @Inject 
    private NetAccessor netAccessor;
    @Inject
    private NetQuestionAccessor netQuestionAccessor;
    @Inject
    private NetQuestionAnswerAccessor netQuestionAnswerAccessor;


    public void sendQuestionReminders() {
        if (!stayRunning()) {
            return;
        }
        User user = new User();

        List<Net> nets = new ArrayList<>();

        try {
             List<Net> netsTemp = netAccessor.getAll(user, null);
             nets = netsTemp;
        } catch (Exception e) {
        }

        if (nets.isEmpty()) {
            return;
        }

        for (Net net : nets) {
            List<NetQuestion> netQuestions = new ArrayList<>();
            List<Participant> netParticipants = new ArrayList<>();

            try {
                List<NetQuestion> netQuestionsTemp = netQuestionAccessor.getAll(user, net.getCompletedNetId());
                netQuestions = netQuestionsTemp;
            } catch (Exception e) {
            }

            if (netQuestions.isEmpty()) {
                continue;
            }

            try {
                List<Participant> netParticipantsTemp = netParticipantAccessor.getAllParticipants(user, net);
                netParticipants = netParticipantsTemp;
            } catch (Exception e) {
            }

            if (netParticipants.isEmpty()) {
                continue;
            }

            // for each question, get answers. see if each participant answered the question. if not ask them again
            List<NetQuestionAnswer> netQuestionAnswers = new ArrayList<>();
            for (NetQuestion netQuestion : netQuestions) {
                if (netQuestion.getNextReminderTime().isAfter(ZonedDateTime.now())) {
                    continue;   // not time to do reminder
                }
                try {
                    List<NetQuestionAnswer> netQuestionAnswersTemp = netQuestionAnswerAccessor.getAll(user, netQuestion.getNetQuestionId());
                    netQuestionAnswers = netQuestionAnswersTemp;
                } catch (Exception e) {
                }

                for (Participant netParticipant : netParticipants) {
                    boolean found = false;

                    for (NetQuestionAnswer netQuestionAnswer : netQuestionAnswers) {
                        if (netQuestionAnswer.getCallsign().equals(netParticipant.getCallsign())) {
                            found = true;
                            break;
                        }
                    }
                   
                    if (!found) {
                        // send a reminder
                        try {
                            String logMessage = String.format("Question reminder sent to %s for question %d in net %s", netParticipant.getCallsign(), netQuestion.getNumber(), net.getCallsign());
                            logger.info(logMessage);
                            String message = String.format("Net msg %d: %s", netQuestion.getNumber(), netQuestion.getQuestionText());
                            transceiverMessageAccessor.sendMessage(user, net.getCallsign(), netParticipant.getCallsign(), "Remember to answer the following question for net "+net.getCallsign());
                            transceiverMessageAccessor.sendMessage(user, net.getCallsign(), netParticipant.getCallsign(), message);
                        } catch (Exception e) {
                        }
                    }
                }

                try {

                    ZonedDateTime nextReminderTime = netQuestion.getNextReminderTime();
                    if (nextReminderTime == null) {
                        nextReminderTime = netQuestion.getAskedTime();
                    }
                    nextReminderTime = nextReminderTime.plusMinutes(netQuestion.getReminderMinutes());
                    netQuestion.setNextReminderTime(nextReminderTime);
                    netQuestionAccessor.update(user, netQuestion.getNetQuestionId(), netQuestion);
                } catch (Exception e) {
                }
            }
        }
    }

    public boolean stayRunning() {
        return !stop;
    }
    public void shutdown() {
        stop = true;
    }
}
