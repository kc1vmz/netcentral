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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetMessageReport;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.UserRole;
import netcentral.server.object.Net;
import netcentral.server.object.NetMessage;
import netcentral.server.object.User;
import netcentral.server.record.NetMessageRecord;
import netcentral.server.repository.NetMessageRepository;

@Singleton
public class NetMessageAccessor {
    private static final Logger logger = LogManager.getLogger(NetMessageAccessor.class);

    @Inject
    private NetAccessor netAccessor;
    @Inject
    private NetMessageRepository netMessageRepository;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private UserAccessor userAccessor;
    @Inject
    private NetCentralServerConfigAccessor netCentralServerConfigAccessor;

    public List<NetMessage> getAll(User loggedInUser, String callsignNet, String completedNetId) {
        List<NetMessage> ret = new ArrayList<>();

        if (completedNetId == null) {
            // get the active net
            Net net = netAccessor.get(loggedInUser, callsignNet);
            if (net == null) {
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net callsign not found");
            }
            completedNetId = net.getCompletedNetId();
        }

        List<NetMessageRecord> recs = netMessageRepository.findBycompleted_net_id(completedNetId);
        if ((recs != null) && (!recs.isEmpty())) {
            for (NetMessageRecord rec : recs) {
                ret.add(new NetMessage(rec.net_message_id(), rec.completed_net_id(), rec.callsign_from(), rec.message(), rec.received_time(), rec.recipient()));
            }
        }

        Collections.sort(ret, new Comparator<NetMessage>() {
            @Override
            public int compare(NetMessage obj1, NetMessage obj2) {
                return -1*obj1.getReceivedTime().compareTo(obj2.getReceivedTime());
            }
        });

        return ret;
    }

    public NetMessage get(User loggedInUser, String id) {
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NetMessage id not provided");
        }
        Optional<NetMessageRecord> recOpt = netMessageRepository.findById(id);
        if (!recOpt.isPresent()) {
            logger.debug("NetMessage not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NetMessage not found");
        }
        NetMessageRecord rec = recOpt.get();
        return new NetMessage(rec.net_message_id(), rec.completed_net_id(), rec.callsign_from(), rec.message(), rec.received_time(), rec.recipient());
    }


    public NetMessage create(User loggedInUser, NetMessage obj) {
        if (obj == null) {
            logger.debug("NetMessage is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NetMessage not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        String messageId = UUID.randomUUID().toString();
        obj.setId(messageId);
        NetMessageRecord src = new NetMessageRecord(messageId, obj.getCompletedNetId(), obj.getCallsignFrom(), obj.getMessage(), ZonedDateTime.now(), obj.getRecipient());
        netMessageRepository.save(src);
        changePublisherAccessor.publishNetMessageUpdate(obj.getCompletedNetId(), ChangePublisherAccessor.CREATE, obj);
        return obj;
    }

    public Net deleteAll(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        if ((!loggedInUser.getRole().equals(UserRole.SYSTEM)) && (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            logger.debug("Insufficient role");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Insufficient role");
        }

        netMessageRepository.deleteAll();
        return null;
    }

    public boolean processFederatedNetReport(User loggedInUser, Net net, APRSMessage aprsMessage, String transceiverSourceId) {
        if (loggedInUser == null) {
            return false;
        }
        if (net == null) {
            return false;
        }
        if (aprsMessage == null) {
            return false;
        }
        if (aprsMessage.getMessage() == null) {
            return false;
        }

        try {
            APRSNetCentralNetMessageReport report = APRSNetCentralNetMessageReport.isValid(aprsMessage.getCallsignFrom(), aprsMessage.getMessage());
            if (report != null) {
                if (netCentralServerConfigAccessor.isFederated()) {
                    // act upon valid report because we are federated
                    NetMessage netMessage = new NetMessage(net.getCallsign(), net.getCompletedNetId(), 
                                            "", // TODO- who sent it
                                            report.getMessageText(), ZonedDateTime.now(),
                                        report.getRecipient().equals(APRSNetCentralNetMessageReport.RECIPIENT_ALL) ? NetMessage.RECIPIENT_ENTIRE_NET : NetMessage.RECIPIENT_NET_CONTROL);
                    // handle a net message being sent
                    create(loggedInUser, netMessage);
                }
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

}
