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
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.TrackedStationType;
import netcentral.server.object.CompletedExpectedParticipant;
import netcentral.server.object.CompletedNet;
import netcentral.server.object.CompletedParticipant;
import netcentral.server.object.Net;
import netcentral.server.object.NetMessage;
import netcentral.server.object.Participant;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.User;
import netcentral.server.object.request.ObjectCreateRequest;
import netcentral.server.utils.NetParticipantReport;

@Singleton
public class UIAccessor {
    private static final Logger logger = LogManager.getLogger(UIAccessor.class);

    @Inject
    private NetAccessor netAccessor;
    @Inject
    private CompletedNetAccessor completedNetAccessor;
    @Inject
    private TrackedStationAccessor trackedStationAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;
    @Inject
    private NetMessageAccessor netMessageAccessor;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;
    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private NetParticipantReport netParticipantReport;
    @Inject
    private CompletedParticipantAccessor completedParticipantAccessor;
    @Inject
    private CompletedExpectedParticipantAccessor completedExpectedParticipantAccessor;

    public void endNet(User loggedInUser, String callsign) {
        logger.debug("Ending net " + callsign);

        Net net = netAccessor.getByCallsign(loggedInUser, callsign);
        List<Participant> participants = netParticipantAccessor.getAllParticipants(loggedInUser, net);

        // create the completed net
        completedNetAccessor.create(loggedInUser, net);
        ZonedDateTime time = ZonedDateTime.now();

        String message = String.format("APRS Net %s has been secured", net.getCallsign());
        NetMessage msg = new NetMessage(UUID.randomUUID().toString(), net.getCompletedNetId(), net.getCallsign(), message, time);
        netMessageAccessor.create(loggedInUser, msg);

        if ((participants != null) && (!participants.isEmpty())) {
            for (Participant participant : participants) {
                transceiverCommunicationAccessor.sendMessage(loggedInUser, net.getCallsign(), participant.getCallsign(), message);
            }
        }

        // delete the net
        netAccessor.delete(loggedInUser, callsign);
    }

    public String generateCompletedNetReport(User loggedInUser, CompletedNet net) {
        try {
            List<CompletedParticipant> participants = completedParticipantAccessor.getAllByCompletedNetId(loggedInUser, net.getCompletedNetId());

            if (participants != null) {
                Collections.sort(participants, new Comparator<CompletedParticipant>() {
                    @Override
                    public int compare(CompletedParticipant p1, CompletedParticipant p2) {
                        return p1.getCallsign().compareTo(p2.getCallsign());
                    }
                });
            }

            List<CompletedExpectedParticipant> expectedParticipants = completedExpectedParticipantAccessor.getAllByCompletedNetId(loggedInUser, net.getCompletedNetId());

            if (expectedParticipants != null) {
                Collections.sort(expectedParticipants, new Comparator<CompletedExpectedParticipant>() {
                    @Override
                    public int compare(CompletedExpectedParticipant p1, CompletedExpectedParticipant p2) {
                        return p1.getCallsign().compareTo(p2.getCallsign());
                    }
                });
            }

            String filename = netParticipantReport.createReport(net, participants, expectedParticipants);
            return filename;
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    public List<TrackedStation> getStations(User loggedInUser, boolean trackOnly) {
        List<TrackedStation> all = trackedStationAccessor.getAll(loggedInUser, null, null, null);
        List<TrackedStation> ret = new ArrayList<>();
        for (TrackedStation item: all) {
            if ((trackOnly) && (!item.isTrackingActive())) {
                continue;
            }
            if ((item.getType() == TrackedStationType.UNKNOWN) || (item.getType() == TrackedStationType.STATION) || (item.getType() == TrackedStationType.DSTAR)) {
                ret.add(item);
            }
        }
        return ret;
    }

    public List<TrackedStation> getInfrastructureStations(User loggedInUser, boolean trackOnly) {
        List<TrackedStation> all = trackedStationAccessor.getAll(loggedInUser, null, null, null);
        List<TrackedStation> ret = new ArrayList<>();
        for (TrackedStation item: all) {
            if ((trackOnly) && (!item.isTrackingActive())) {
                continue;
            }
            if ((item.getType() == TrackedStationType.DIGIPEATER) || (item.getType() == TrackedStationType.IGATE) || (item.getType() == TrackedStationType.REPEATER) || 
                                            (item.getType() == TrackedStationType.IS) || (item.getType() == TrackedStationType.WINLINK_GATEWAY) || 
                                            (item.getType() == TrackedStationType.BBS) || (item.getType() == TrackedStationType.MMDVM)) {
                ret.add(item);
            }
        }
        return ret;
    }

    public List<TrackedStation> getWeatherStations(User loggedInUser, boolean trackOnly) {
        List<TrackedStation> all = trackedStationAccessor.getAll(loggedInUser, null, null, "WEATHER");
        List<TrackedStation> ret = new ArrayList<>();
        for (TrackedStation item: all) {
            if ((trackOnly) && (!item.isTrackingActive())) {
                continue;
            }
            ret.add(item);
        }
        return ret;
    }

    public void identifyCallsign(User loggedInUser, String callsign) {
        transceiverCommunicationAccessor.sendMessage(loggedInUser, (loggedInUser.getCallsign() == null) ? null : loggedInUser.getCallsign().getCallsign(), "WHO-15", callsign);
    }

    public void sendNetMessage(User loggedInUser, Net net, String message) {
        if (net.isRemote()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net is remote");
        }

        List<Participant> participants = netParticipantAccessor.getAllParticipants(loggedInUser, net);
        ZonedDateTime time = ZonedDateTime.now();
        NetMessage msg = new NetMessage(UUID.randomUUID().toString(), net.getCompletedNetId(), net.getCallsign(), message, time);
        netMessageAccessor.create(loggedInUser, msg);
        aprsObjectAccessor.createAPRSMessageFromNetMessage(loggedInUser, net, msg, "CONSOLE");

        if ((participants != null) && (!participants.isEmpty())) {
            for (Participant participant : participants) {
                transceiverCommunicationAccessor.sendMessage(loggedInUser, net.getCallsign(), participant.getCallsign(), message);
            }
        }
    }

    public void upObject(User loggedInUser, ObjectCreateRequest messageRequest) {
        // given an object, create it on APRS and in our database
        if ((messageRequest.lat() != null) && (messageRequest.lon() != null)) {
            transceiverCommunicationAccessor.sendObject(loggedInUser, messageRequest.callsign(), messageRequest.callsign(), 
                                       messageRequest.description(), true, messageRequest.lat() , messageRequest.lon());
        }

        aprsObjectAccessor.upObject(loggedInUser, messageRequest);
    }

    public void downObject(User loggedInUser, ObjectCreateRequest messageRequest) {
        // given an object, create it on APRS and in our database
        if ((messageRequest.lat() != null) && (messageRequest.lon() != null)) {
            transceiverCommunicationAccessor.sendObject(loggedInUser, messageRequest.callsign(), messageRequest.callsign(), 
                                       messageRequest.description(), false, messageRequest.lat() , messageRequest.lon());
        }

        aprsObjectAccessor.downObject(loggedInUser, messageRequest);
    }
}
