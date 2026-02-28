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
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.constants.APRSQueryType;
import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetAnnounceReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetCheckInOutReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetSecureReport;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralNetStartReport;
import com.kc1vmz.netcentral.common.constants.NetCentralQueryType;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.RadioStyle;
import netcentral.server.enums.UserRole;
import netcentral.server.object.Net;
import netcentral.server.object.Participant;
import netcentral.server.object.User;
import netcentral.server.object.request.ObjectCreateRequest;
import netcentral.server.record.NetRecord;
import netcentral.server.repository.NetRepository;

@Singleton
public class NetAccessor {
    private static final Logger logger = LogManager.getLogger(NetAccessor.class);

    @Inject
    ApplicationContext applicationContext;
    @Inject
    private NetRepository netRepository;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;
    @Inject
    private ParticipantAccessor participantAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;
    @Inject
    private NetCentralServerConfigAccessor netCentralServerConfigAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private UserAccessor userAccessor;
    @Inject
    private FederatedObjectReporterAccessor federatedObjectReporterAccessor;

    public List<Net> getAll(User loggedInUser, String root) {
        List<NetRecord> recs = netRepository.findAll();
        List<Net> ret = new ArrayList<>();

        if (!recs.isEmpty()) {
            for (NetRecord rec : recs) {
                if ((root != null) && (!rec.callsign().startsWith(root))) {
                    // only take those with the optional root
                    continue;
                }
                ret.add(new Net(rec.callsign(), rec.name(), rec.description(), rec.vfreq(), rec.start_time(), rec.completed_net_id(), rec.lat(), rec.lon(), rec.announce(), rec.creator_name(), rec.checkin_reminder(), 
                                    rec.checkin_message(), rec.open(), rec.participant_invite_allowed(), rec.remote()));
            }
        }

        return ret;
    }

    public Net get(User loggedInUser, String id) {
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net id not provided");
        }
        Optional<NetRecord> recOpt = netRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not found");
        }
        NetRecord rec = recOpt.get();
        return new Net(rec.callsign(), rec.name(), rec.description(), rec.vfreq(), rec.start_time(), rec.completed_net_id(), rec.lat(), rec.lon(), rec.announce(), rec.creator_name(), rec.checkin_reminder(),
                                rec.checkin_message(), rec.open(), rec.participant_invite_allowed(), rec.remote());
    }

    public Net getByCallsign(User loggedInUser, String callsign) {
        if (callsign == null) {
            logger.debug("Net not provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not provided");
        }
        try {
            Optional<NetRecord> recOpt = netRepository.findById(callsign);
            NetRecord rec = recOpt.get();
            if (rec == null) {
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not found");
            }
            return new Net(rec.callsign(), rec.name(), rec.description(), rec.vfreq(), rec.start_time(), rec.completed_net_id(), rec.lat(), rec.lon(), rec.announce(), rec.creator_name(), rec.checkin_reminder(),
                                rec.checkin_message(), rec.open(), rec.participant_invite_allowed(), rec.remote());
        } catch (Exception e) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not found");
        }
    }

    public Net create(User loggedInUser, Net obj) {
        if (obj == null) {
            logger.debug("Net is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        try {
            Optional<NetRecord> existingOpt = netRepository.findById(obj.getCallsign());
            if (existingOpt.get() != null) {
                logger.debug("Net already exists");
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net already exists");
            }
        } catch (Exception e) {
        }

        String creatorName = obj.getCreatorName();
        if (creatorName == null) {
            creatorName = loggedInUser.getEmailAddress();
        }
        String completed_net_id = UUID.randomUUID().toString(); // pre-assign instance id for completed net
        NetRecord src = new NetRecord(obj.getCallsign(), (obj.getVoiceFrequency() != null) ? obj.getVoiceFrequency() : "", obj.getName(), 
                                            (obj.getDescription() != null) ? obj.getDescription() : "",  
                                            ZonedDateTime.now(), completed_net_id, obj.getLat(), obj.getLon(), obj.isAnnounce(),
                                            creatorName, obj.isCheckinReminder(), obj.getCheckinMessage(),
                                            obj.isOpen(), obj.isParticipantInviteAllowed(), obj.isRemote());
        NetRecord rec = netRepository.save(src);
        if (rec != null) {
            obj.setCompletedNetId(completed_net_id);
            if ((obj.isAnnounce()) && (obj.getLat() != null) && (obj.getLon() != null)) {
                // announce the object and send the bulletin
                if (!obj.isRemote()) {
                    // only for local nets
                    String objectMessage = String.format("Net %s", obj.getName());
                    String announcement = "";
                    if ((obj.getVoiceFrequency() != null) && (!obj.getVoiceFrequency().isEmpty())) {
                        if ((obj.getVoiceFrequency().length() == 10) || (obj.getVoiceFrequency().length() == 20)) {
                            // must be properly formatted 10 or 20 byte - see below
                            announcement = String.format("%s APRS Net %s started", obj.getVoiceFrequency(), obj.getCallsign());
                        }
                    }
                    if (announcement.isEmpty()) {
                        announcement = String.format("APRS Net %s started", obj.getCallsign());
                    }
                    transceiverCommunicationAccessor.sendObject(loggedInUser, obj.getCallsign(), obj.getCallsign(), objectMessage, true, obj.getLat(), obj.getLon());
                    transceiverCommunicationAccessor.sendBulletin(loggedInUser, obj.getCallsign(), netCentralServerConfigAccessor.getBulletinAnnounce(), announcement);

                    federatedObjectReporterAccessor.announce(loggedInUser, obj);
                }

                // also put the object into our ecosystem
                ObjectCreateRequest objectCreateRequest = new ObjectCreateRequest(ObjectType.NET.ordinal(), obj.getCallsign(), obj.getDescription(), obj.getLat(), obj.getLon());
                upObject(loggedInUser, objectCreateRequest);

                if (!obj.isRemote()) {
                    statisticsAccessor.incrementNetsStarted();
                }
            }
            changePublisherAccessor.publishNetUpdate(obj.getCompletedNetId(), ChangePublisherAccessor.CREATE, obj);
            return obj;
        }

        logger.debug("Net not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not created");
    }

    public void upObject(User loggedInUser, ObjectCreateRequest objectCreateRequest) {
        APRSObjectAccessor la = applicationContext.getBean(APRSObjectAccessor.class);
        if (la != null) {
            la.upObject(loggedInUser, objectCreateRequest);
        }
    }

    public void downObject(User loggedInUser, ObjectCreateRequest objectCreateRequest) {
        APRSObjectAccessor la = applicationContext.getBean(APRSObjectAccessor.class);
        if (la != null) {
            la.downObject(loggedInUser, objectCreateRequest);
        }
    }

    public void deleteObject(User loggedInUser, ObjectCreateRequest objectCreateRequest) {
        APRSObjectAccessor la = applicationContext.getBean(APRSObjectAccessor.class);
        if (la != null) {
            la.deleteObject(loggedInUser, objectCreateRequest);
        }
    }

    public Net update(User loggedInUser, String id, Net obj) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net id not provided");
        }
        if (obj == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not provided");
        }
        if ((obj.getCallsign() != null) && (!id.equals(obj.getCallsign()))){
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Callsigns to not match");
        }
        if ((obj.getCallsign() == null) || (obj.getCallsign().isEmpty()) || (obj.getCallsign().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid callsign");
        }
        if ((obj.getName() == null) || (obj.getName().isEmpty()) || (obj.getName().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid name");
        }

        Optional<NetRecord> recOpt = netRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not found");
        }
        NetRecord rec = recOpt.get();
        NetRecord updatedRec = new NetRecord(rec.callsign(), (obj.getVoiceFrequency() != null) ? obj.getVoiceFrequency() : "", obj.getName(), 
                                            (obj.getDescription() != null) ? obj.getDescription() : "",  
                                            obj.getStartTime(), obj.getCompletedNetId(), obj.getLat(), obj.getLon(), obj.isAnnounce(), rec.creator_name(), obj.isCheckinReminder(), obj.getCheckinMessage(),
                                            obj.isOpen(), obj.isParticipantInviteAllowed(), obj.isRemote());

        netRepository.update(updatedRec);
        obj = get(loggedInUser, id);

        // announce changes in object
        if (obj != null) {
            if ((obj.isAnnounce()) && (obj.getLat() != null) && (obj.getLon() != null) && (!obj.isRemote())) {
                transceiverCommunicationAccessor.sendObject(loggedInUser, obj.getCallsign(), obj.getCallsign(), String.format("Net %s", obj.getName()), true, obj.getLat(), obj.getLon());
            }
        }

        changePublisherAccessor.publishNetUpdate(rec.completed_net_id(), ChangePublisherAccessor.UPDATE, obj);

        return obj;

    }

    public Net delete(User loggedInUser, String id) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net id not provided");
        }

        Net net = null;

        Optional<NetRecord> recOpt = netRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not found");
        }

        try {
            net = get(loggedInUser, id);

            if (!net.isRemote()) {
                List<Participant> participants = netParticipantAccessor.getAllParticipants(loggedInUser, net);
                if ((participants != null) && (!participants.isEmpty())) {
                    // if there are participants, remove them before delete
                    for (Participant participant : participants)  {
                        try {
                            transceiverCommunicationAccessor.sendMessage(loggedInUser, net.getCallsign(), participant.getCallsign(), String.format("APRS Net %s ended", net.getCallsign()));
                            netParticipantAccessor.removeParticipant(loggedInUser, net, participant);

                            // if they are not participating in other nets, remove the participant completely
                            List<Net> participantNets = netParticipantAccessor.getAllNets(loggedInUser, participant);
                            if ((participantNets == null) || (participantNets.isEmpty())) {
                                participantAccessor.delete(loggedInUser, participant.getCallsign());
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            net = null;
            logger.error("Could not clean up net participants");
        }

        NetRecord rec = recOpt.get();
        netRepository.delete(rec);

        if (net != null) {
            if (!net.isRemote()) {
                if ((rec.announce()) && (rec.lat() != null) && (rec.lon() != null)) {
                    // announce the object and send the bulletin
                    transceiverCommunicationAccessor.sendObject(loggedInUser, rec.callsign(), rec.callsign(), String.format("Net %s", rec.name()), false, rec.lat(), rec.lon());
                    transceiverCommunicationAccessor.sendBulletin(loggedInUser, rec.callsign(), netCentralServerConfigAccessor.getBulletinAnnounce(), String.format("APRS Net %s ended", rec.callsign()));
                }

                federatedObjectReporterAccessor.secure(loggedInUser, net);

                statisticsAccessor.incrementNetsClosed();
            }

            // also remove the object from our ecosystem
            ObjectCreateRequest objectCreateRequest = new ObjectCreateRequest(ObjectType.NET.ordinal(), rec.callsign(), rec.description(), rec.lat(), rec.lon());
            deleteObject(loggedInUser, objectCreateRequest);

            changePublisherAccessor.publishNetUpdate(rec.completed_net_id(), ChangePublisherAccessor.DELETE, net);
        }

        return null;
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

        netRepository.deleteAll();
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
            APRSNetCentralNetAnnounceReport report = APRSNetCentralNetAnnounceReport.isValid(aprsMessage.getCallsignFrom(), aprsMessage.getMessage());
            if (report != null) {
                // handle a new net announcement - ignore it
                return true;
            }
        } catch (Exception e) {
        }

        try {
            APRSNetCentralNetCheckInOutReport report = APRSNetCentralNetCheckInOutReport.isValid(aprsMessage.getCallsignFrom(), aprsMessage.getMessage());
            if (report != null) {
                // handle a check in / out
                if (netCentralServerConfigAccessor.isFederated()) {
                    // act upon valid report because we are federated
                    ZonedDateTime now = ZonedDateTime.now();

                    Participant participant = getParticipant(loggedInUser, report.getReportData()); 
                    if (participant == null) {
                        participant = new Participant(report.getReportData(), "Unknown", null, now, 
                                                            null, null, ElectricalPowerType.UNKNOWN, ElectricalPowerType.UNKNOWN, RadioStyle.UNKNOWN,
                                                            0, null, now);
                        participant = participantAccessor.create(loggedInUser, participant);
                    }

                    if (report.isCheckIn()) {
                        netParticipantAccessor.addParticipant(loggedInUser, net, participant);
                    } else {
                        netParticipantAccessor.removeParticipant(loggedInUser, net, participant);
                    }
                }
                return true;
            }
        } catch (Exception e) {
        }

        try {
            APRSNetCentralNetSecureReport report = APRSNetCentralNetSecureReport.isValid(aprsMessage.getCallsignFrom(), aprsMessage.getMessage());
            if (report != null) {
                // handle a net being secured - just delete it
                delete(loggedInUser, aprsMessage.getCallsignFrom());
                return true;
            }
        } catch (Exception e) {
        }

        try {
            APRSNetCentralNetStartReport report = APRSNetCentralNetStartReport.isValid(aprsMessage.getCallsignFrom(), aprsMessage.getMessage());
            if (report != null) {
                // handle a net being started
                net.setStartTime(report.getStartTime());
                update(loggedInUser, net.getCallsign(), net);
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

    private Participant getParticipant(User loggedInUser, String callsign) {
        Participant ret = null;
        try {
            ret = participantAccessor.get(loggedInUser, callsign);
        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

    public void processDirectedStatusQuery(User loggedInUser, Net net, APRSMessage aprsMessage, String transceiverSourceId) {
        if (loggedInUser == null) {
            return;
        }
        if (net == null) {
            return;
        }
        if (aprsMessage == null) {
            return;
        }
        if (aprsMessage.getMessage() == null) {
            return;
        }
        if (!aprsMessage.getMessage().startsWith(APRSQueryType.PREFIX)) {
            return;
        }

        try {
            String queryType = aprsMessage.getMessage().substring(1);

            if (queryType.equalsIgnoreCase(NetCentralQueryType.COMMANDS)) {
                // list commands
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, aprsMessage, getNetCommandsResponse(loggedInUser, net));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.INFO)) {
                // send commands and info
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, aprsMessage, getNetCommandsResponse(loggedInUser, net));
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, aprsMessage, getNetInfoResponse(loggedInUser, net));
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_OBJECTS)) {
                // announce net object
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_POSITION)) {
                // send net position packet
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_STATUS)) {
                // send net status
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE)) {
                // send object type
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, aprsMessage, getNetObjectTypeResponse(loggedInUser, aprsMessage));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.NET_INFO)) {
                // send net information
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, aprsMessage, getNetInfoResponse(loggedInUser, net));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.NET_PARTICIPANTS)) {
                // send net participants
                // send KV pairs K=V,K=V...
                List<String> responses = getNetParticipantsResponse(loggedInUser, net);
                if (!responses.isEmpty()) {
                    for (String message: responses) {
                        federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, aprsMessage, message);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private String getNetObjectTypeResponse(User loggedInUser, APRSMessage message) {
        return String.format("%s:%s", NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE, ObjectType.NET.toString());
    }

    private List<String> getNetParticipantsResponse(User loggedInUser, Net net) {
        List<String> ret = new ArrayList<>();
        try {
            List<Participant> participants = netParticipantAccessor.getAllParticipants(loggedInUser, net, false);
            int count = 0;
            while (count < participants.size()){
                int callsignsLeft = 5;
                String item = String.format("%s:", NetCentralQueryType.NET_PARTICIPANTS);
                while ((callsignsLeft > 0) && (count < participants.size())) {
                    item += (" "+ participants.get(count).getCallsign());
                    count++;
                    callsignsLeft--;
                }
                ret.add(item);
            }
        } catch (Exception e) {
        }

        return ret;
    }

    private String getNetCommandsResponse(User loggedInUser, Net net) {
        return String.format("%s:%s,%s,%s,%s,%s,%s", NetCentralQueryType.COMMANDS, NetCentralQueryType.COMMANDS, APRSQueryType.APRS_OBJECTS, APRSQueryType.APRS_POSITION, APRSQueryType.APRS_STATUS,
                                                    NetCentralQueryType.NET_INFO, NetCentralQueryType.NET_PARTICIPANTS);
    }

    private String getNetInfoResponse(User loggedInUser, Net net) {
        return String.format("%s:%s,%s", NetCentralQueryType.GENERAL_INFO, net.getCallsign(), net.getDescription());
    }
}


/*
 * 
 * https://www.aprs.org/info/freqspec.txt
 * 
 * EXAMPLE POSITION/OBJECT comments when OBJECT NAME is not FFF.FFxyz:

   1st 10-BYTES  Frequency Description
   ----------    -----------------------------------------------------
   FFF.FF MHz    Freq to nearest 10 KHz
   FFF.FFFMHz    Freq to nearest  1 KHz
    
   Examples:
   146.52 MHz Enroute Alabama
   147.105MHz AARC Radio Club
   146.82 MHz T107 AARC Repeater (Tone of 107.2)
   146.835MHz C107 R25m AARC     (CTCSS of 107.3 and range of 25 mi)
   146.805MHz D256 R25k Repeater (DCS code and range of 25 km)
   146.40 MHz T067 +100 Repeater (67.8 tone and +1.00 MHz offset)
   442.440MHz T107 -500 Repeater (107.2 tone and 5 MHz offset)
   145.50 MHz t077 Simplex       (Tone of 77.X Hz and NARROW band)

   2nd 10-BYTES  Optional Added fields  (with leading space)
   ----------    -----------------------------------------------------
   _Txxx RXXm    Optional PL tone and nominal range in miles
   _Cxxx -060    Optional CTCSS tone and -600 KHz offset
   _Dxxx RXXk    Optional DCS code and nominal range in kilometers
   _1750 RXXk    Optional 1750 tone, range in km, wide modulation
   _l750 RXXk    Optional 1750 tone narrow modulation (lower case L)
   _Toff -000    Optional NO-PL, No DCS, no Tone, forced simplex
   _Txxx +060    Optional Offset of +600 KHz (up to 9.90 MHz)
   _Exxm Wxxm    East range and West range if different (N,S,E,W)
   _txxx RXXm    Lower case first letter means NARROW modulation
   _FFF.FFFrx    Alternate receiver Frequency if not standard offset

If a frequency is included in the first 10 bytes then "MHz" in mixed 
case is required to be transmitted.  (Case should be case insensitive 
on receipt to allow for manual typos).  Notice that the second 10 byte 
fields begin with a SPACE shown above as "_" (9 useable bytes) for 
better reading of the packet when combined with a frequency in the first
ten bytes.  Do not include the "_" but put a SPACE there in your actual 
packet.  (8 May 2012 clarification: if the first 10 bytes do not 
contain a frequency, then left justify the TONE and OFFSET without
the leading space).  Here is the raw packet format for the comments:
    
    FFF.FFFMHz comment...           one frequency
    FFF.FF MHz FFF.FFFrx comment... for separate TX and RX 
    FFF.FF MHz T107 R25m comment... for TX, tone and range
    FFF.FF MHz T107 oXXX R25m ...   for TX, tone and range
    Tnnn oXXX R25m comments....     for a FREQ OBJ <08 May clarity>
    Tnnn R25m comments....          for a FREQ OBJ standard offsets
    Tnnn comments....               for a FREQ OBJ Tone only

 */