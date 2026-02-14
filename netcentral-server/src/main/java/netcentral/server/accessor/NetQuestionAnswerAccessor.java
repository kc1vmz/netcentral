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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.UserRole;
import netcentral.server.object.Net;
import netcentral.server.object.NetQuestion;
import netcentral.server.object.NetQuestionAnswer;
import netcentral.server.object.User;
import netcentral.server.record.NetQuestionAnswerRecord;
import netcentral.server.repository.NetQuestionAnswerRepository;

@Singleton
public class NetQuestionAnswerAccessor {
    private static final Logger logger = LogManager.getLogger(NetQuestionAnswerAccessor.class);

    @Inject
    private NetQuestionAnswerRepository netQuestionAnswerRepository;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private FederatedObjectReporterAccessor federatedObjectReporterAccessor;

    public List<NetQuestionAnswer> getAll(User loggedInUser, String netQuestionId) {
        List<NetQuestionAnswerRecord> recs = netQuestionAnswerRepository.findBynet_question_id(netQuestionId);
        List<NetQuestionAnswer> ret = new ArrayList<>();

        if (!recs.isEmpty()) {
            for (NetQuestionAnswerRecord rec : recs) {
                ret.add(new NetQuestionAnswer(rec.net_question_answer_id(), rec.net_question_id(), rec.completed_net_id(), rec.callsign(), rec.answered_time(), rec.answer_text()));
            }
        }

        Collections.sort(ret, new Comparator<NetQuestionAnswer>() {
            @Override
            public int compare(NetQuestionAnswer obj1, NetQuestionAnswer obj2) {
                return -1*obj1.getAnsweredTime().compareTo(obj2.getAnsweredTime());
            }
        });

        return ret;
    }

    public NetQuestionAnswer create(User loggedInUser, NetQuestion question, NetQuestionAnswer obj, Net net) {
        if (obj == null) {
            logger.debug("Net Question Answer is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net Question Answer not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (obj.getAnswerText() == null) {
            logger.debug("No question text");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "No answer text");
        }

        if (obj.getAnswerText().length() > 50) {
            obj.setAnswerText(obj.getAnswerText().substring(0, 50));
        }

        String id = UUID.randomUUID().toString();
        obj.setNetQuestionAnswerId(id);
        NetQuestionAnswerRecord src = new NetQuestionAnswerRecord(id, obj.getNetQuestionId(), obj.getCompletedNetId(), obj.getAnsweredTime(), obj.getCallsign(), obj.getAnswerText());
        NetQuestionAnswerRecord rec = netQuestionAnswerRepository.save(src);
        if (rec != null) {
            changePublisherAccessor.publishNetQuestionAnswerUpdate(obj.getNetQuestionId(), ChangePublisherAccessor.CREATE, obj);

            // thank participant for answer
            if (!net.isRemote()) {
                transceiverCommunicationAccessor.sendMessage(loggedInUser, net.getCallsign(), obj.getCallsign(), "Thank you for your answer");

                federatedObjectReporterAccessor.announce(loggedInUser, net, question, obj);
            }
            return obj;
        }
        logger.debug("Net Question Answer not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net Question Answer not created");
    }

    public void deleteAllData(User loggedInUser) {
        if ((loggedInUser == null) || (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            // no privs
            logger.error("No privileges to allow delete all");
            return;
        }

        netQuestionAnswerRepository.deleteAll();
    }
}
