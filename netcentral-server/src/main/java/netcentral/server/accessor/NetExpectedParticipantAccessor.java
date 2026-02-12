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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.UserRole;
import netcentral.server.object.ExpectedParticipant;
import netcentral.server.object.Net;
import netcentral.server.object.ScheduledNet;
import netcentral.server.object.User;
import netcentral.server.record.ExpectedParticipantRecord;
import netcentral.server.repository.NetExpectedParticipantRepository;

@Singleton
public class NetExpectedParticipantAccessor {
    private static final Logger logger = LogManager.getLogger(NetExpectedParticipantAccessor.class);

    @Inject
    private NetExpectedParticipantRepository netExpectedParticipantRepository;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private UserAccessor userAccessor;


    public ExpectedParticipant get(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("null callsign");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant callsign not provided");
        }
        List<ExpectedParticipantRecord> recs = null;
        try {
            List<ExpectedParticipantRecord> recsTemp = netExpectedParticipantRepository.findBycallsign(callsign);
            recs = recsTemp;
        } catch (Exception e) {
        }

        if ((recs != null) && (!recs.isEmpty())) {
            ExpectedParticipantRecord rec = recs.get(0);
            return new ExpectedParticipant(rec.callsign());
        }

        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not found");
    }

    private List<ExpectedParticipant> getExpectedParticipants(User loggedInUser, String netCallsign, String netCompletedId) {
        List<ExpectedParticipant> ret = new ArrayList<>();

        try { 
            List<ExpectedParticipantRecord> participantRecsTemp = netExpectedParticipantRepository.findBynet_callsign(netCallsign);
            List<ExpectedParticipantRecord> participantRecs = new ArrayList<>();

            if (participantRecsTemp != null) {
                for (ExpectedParticipantRecord participantRecord : participantRecsTemp) {
                    if ((netCompletedId == null) && (participantRecord.completed_net_id() == null)) {
                        participantRecs.add(participantRecord);
                    } else if ((netCompletedId != null) && (participantRecord.completed_net_id() != null)) {
                        participantRecs.add(participantRecord);
                    }
                }
            }

            if ((participantRecs != null) && (!participantRecs.isEmpty())) {
                for (ExpectedParticipantRecord rec: participantRecs) {
                    ret.add(new ExpectedParticipant(rec.callsign()));
                }
            }
        } catch (Exception e) {
            logger.warn("Exception caught retrieving expected participants", e);
        }

        return ret;
    }

    private List<ExpectedParticipant> addExpectedParticipant(User loggedInUser, String netCallsign, String completedNetId, ExpectedParticipant participant) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (netCallsign == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not provided");
        }
        if ((participant == null) || (participant.getCallsign() == null)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not provided");
        }
        try {
            String cnId;
            if (completedNetId == null) {
                cnId = "SCH";
            } else {
                cnId = "RUN";
            }

            String nid = netCallsign+"."+participant.getCallsign()+"."+cnId;
            ExpectedParticipantRecord rec = new ExpectedParticipantRecord(nid, netCallsign, completedNetId, participant.getCallsign());
            netExpectedParticipantRepository.save(rec);
            changePublisherAccessor.publishNetExpectedParticipantUpdate(netCallsign, ChangePublisherAccessor.CREATE, participant);
        } catch (Exception e) {
            logger.warn("Exception caught adding expected participant", e);
        }

        return getExpectedParticipants(loggedInUser, netCallsign, completedNetId); 
    }

    private List<ExpectedParticipant> removeExpectedParticipant(User loggedInUser, String netCallsign,  String netCompletedId, ExpectedParticipant participant) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (netCallsign == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not provided");
        }
        if ((participant == null) || (participant.getCallsign() == null)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not provided");
        }

        List<ExpectedParticipantRecord> recs = null;
        try {
            List<ExpectedParticipantRecord> recsTemp = netExpectedParticipantRepository.findBycallsign(participant.getCallsign());
            recs = recsTemp;

            for (ExpectedParticipantRecord rec : recs) {
                if ( ((netCompletedId == null) && (rec.completed_net_id() == null) && (netCallsign.equals(rec.net_callsign()))) ||
                     ((netCompletedId != null) && (rec.completed_net_id().equals(netCompletedId) && (netCallsign.equals(rec.net_callsign()))))) {
                    // we have our record between running and scheduled

                    changePublisherAccessor.publishNetExpectedParticipantUpdate(netCallsign, ChangePublisherAccessor.DELETE, participant);
                    netExpectedParticipantRepository.delete(rec);
                    break;
                }
            }
        } catch (Exception e) {
        }

        return getExpectedParticipants(loggedInUser, netCallsign, netCompletedId); 
    }

    public List<ExpectedParticipant> getExpectedParticipants(User loggedInUser, Net net) {
        if (net.isRemote()) {
            return new ArrayList<>();
        }
        return getExpectedParticipants(loggedInUser, net.getCallsign(), net.getCompletedNetId());
    }

    public List<ExpectedParticipant> addExpectedParticipant(User loggedInUser, Net net, ExpectedParticipant participant) {
        if (net.isRemote()) {
            return new ArrayList<>();
        }
        return addExpectedParticipant(loggedInUser, net.getCallsign(), net.getCompletedNetId(), participant);
    }

    public List<ExpectedParticipant> removeExpectedParticipant(User loggedInUser, Net net, ExpectedParticipant participant) {
        if (net.isRemote()) {
            return new ArrayList<>();
        }
        return removeExpectedParticipant(loggedInUser, net.getCallsign(), net.getCompletedNetId(), participant);
    }

    public List<ExpectedParticipant> getExpectedParticipants(User loggedInUser, ScheduledNet net) {
        return getExpectedParticipants(loggedInUser, net.getCallsign(), null);
    }

    public List<ExpectedParticipant> addExpectedParticipant(User loggedInUser, ScheduledNet net, ExpectedParticipant participant) {
        return addExpectedParticipant(loggedInUser, net.getCallsign(), null, participant);
    }

    public List<ExpectedParticipant> removeExpectedParticipant(User loggedInUser, ScheduledNet net, ExpectedParticipant participant) {
        return removeExpectedParticipant(loggedInUser, net.getCallsign(), null, participant);
    }

    public ExpectedParticipant deleteAll(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        if ((!loggedInUser.getRole().equals(UserRole.SYSTEM)) && (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            logger.debug("Insufficient role");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Insufficient role");
        }

        netExpectedParticipantRepository.deleteAll();
        return null;
    }
}
