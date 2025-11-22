package netcentral.server.accessor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ElectricalPowerType;
import netcentral.server.enums.RadioStyle;
import netcentral.server.enums.UserRole;
import netcentral.server.object.Net;
import netcentral.server.object.Participant;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.User;
import netcentral.server.record.CompletedParticipantRecord;
import netcentral.server.record.NetParticipantRecord;
import netcentral.server.repository.CompletedParticipantRepository;
import netcentral.server.repository.NetParticipantRepository;

@Singleton
public class NetParticipantAccessor {
    private static final Logger logger = LogManager.getLogger(NetParticipantAccessor.class);

    @Inject
    private NetParticipantRepository netParticipantRepository;
    @Inject
    private CompletedParticipantRepository completedParticipantRepository;
    @Inject
    private TrackedStationAccessor trackedStationAccessor;
    @Inject
    private ParticipantAccessor participantAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private UserAccessor userAccessor;


    public List<Participant> getAllParticipants(User loggedInUser, Net net) {
        List<Participant> ret = new ArrayList<>();
        if ((net == null) || (net.getCallsign() == null)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not found");
        }

        List<NetParticipantRecord> participantRecs = netParticipantRepository.findBynet_callsign(net.getCallsign());
        if ((participantRecs != null) && (!participantRecs.isEmpty())) {
            for (NetParticipantRecord rec: participantRecs) {
                ret.add(new Participant(rec.participant_callsign(), "", "", rec.start_time(), null, null, ElectricalPowerType.UNKNOWN, ElectricalPowerType.UNKNOWN, RadioStyle.UNKNOWN, 0, rec.tactical_callsign(), null));
            }
        }
        ret = hydrateParticipants(loggedInUser, ret);
        return ret;
    }

    private List<Participant> hydrateParticipants(User loggedInUser, List<Participant> in) {
        if ((in == null) || (in.isEmpty())) {
            return in;
        }
        List<Participant> ret = new ArrayList<>();
        for (Participant item : in) {
            Participant liveParticipant = participantAccessor.get(loggedInUser, item.getCallsign());
            liveParticipant.setStartTime(item.getStartTime());
            liveParticipant.setTacticalCallsign(item.getTacticalCallsign());
            // liveParticipant.setStatus(item.getStatus()); - already in liveParticipant

            // get curent location
            TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, item.getCallsign());
            liveParticipant.setLat(trackedStation.getLat());
            liveParticipant.setLon(trackedStation.getLon());
            liveParticipant.setElectricalPowerType(trackedStation.getElectricalPowerType());
            liveParticipant.setBackupElectricalPowerType(trackedStation.getBackupElectricalPowerType());
            liveParticipant.setRadioStyle(trackedStation.getRadioStyle());
            liveParticipant.setTransmitPower(trackedStation.getTransmitPower());
            liveParticipant.setLastHeardTime(trackedStation.getLastHeard());

            ret.add(liveParticipant);
        }
        return ret;
    }

    private Participant hydrateParticipant(User loggedInUser, Participant item) {
        if (item == null) {
            return item;
        }
        Participant liveParticipant = participantAccessor.get(loggedInUser, item.getCallsign());
        liveParticipant.setStartTime(item.getStartTime());
        liveParticipant.setTacticalCallsign(item.getTacticalCallsign());
        // liveParticipant.setStatus(item.getStatus()); - already in liveParticipant

        // get curent location
        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, item.getCallsign());
        liveParticipant.setLat(trackedStation.getLat());
        liveParticipant.setLon(trackedStation.getLon());
        liveParticipant.setElectricalPowerType(trackedStation.getElectricalPowerType());
        liveParticipant.setBackupElectricalPowerType(trackedStation.getBackupElectricalPowerType());
        liveParticipant.setRadioStyle(trackedStation.getRadioStyle());
        liveParticipant.setTransmitPower(trackedStation.getTransmitPower());
        liveParticipant.setLastHeardTime(trackedStation.getLastHeard());

        return liveParticipant;
    }

    public List<Participant> addParticipant(User loggedInUser, Net net, Participant participant) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if ((net == null) || (net.getCallsign() == null)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not provided");
        }
        if ((participant == null) || (participant.getCallsign() == null)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not provided");
        }
        try {
            String nid = net.getCallsign()+"."+participant.getCallsign();
            NetParticipantRecord rec = new NetParticipantRecord(nid,net.getCallsign(), participant.getCallsign(), ZonedDateTime.now(), null);
            netParticipantRepository.save(rec);
            participant.setStartTime(rec.start_time());
            changePublisherAccessor.publishNetParticipantUpdate(net.getCallsign(), "Create", hydrateParticipant(loggedInUser,participant));
        } catch (Exception e) {
            logger.warn("Exception caught adding participant", e);
        }

        return getAllParticipants(loggedInUser, net); 
    }

    public List<Participant> removeParticipant(User loggedInUser, Net net, Participant participant) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if ((net == null) || (net.getCallsign() == null)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Net not provided");
        }
        if ((participant == null) || (participant.getCallsign() == null)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not provided");
        }

        NetParticipantRecord participantRec = netParticipantRepository.find(net.getCallsign(), participant.getCallsign());
        if (participantRec == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Not a net participant");
        }

        TrackedStation trackedStation = trackedStationAccessor.getByCallsign(loggedInUser, participant.getCallsign());

        String completed_participant_id = UUID.randomUUID().toString(); // pre-assign instance id for completed net
        ZonedDateTime startTime = participantRec.start_time();
        completedParticipantRepository.save(new CompletedParticipantRecord(completed_participant_id, net.getCompletedNetId(), participant.getCallsign(), startTime, ZonedDateTime.now(),
                            (trackedStation != null) ? trackedStation.getElectricalPowerType().ordinal() : ElectricalPowerType.UNKNOWN.ordinal(),
                            (trackedStation != null) ? trackedStation.getRadioStyle().ordinal() : RadioStyle.UNKNOWN.ordinal(),
                            (trackedStation != null) ? trackedStation.getTransmitPower() : 0, participant.getTacticalCallsign()));

        netParticipantRepository.delete(participantRec);
        changePublisherAccessor.publishNetParticipantUpdate(net.getCallsign(), "Delete", hydrateParticipant(loggedInUser,participant));

        return getAllParticipants(loggedInUser, net); 
    }

    public List<Net> getAllNets(User loggedInUser, Participant participant) {
        List<Net> ret = new ArrayList<>();
        if ((participant == null) || (participant.getCallsign() == null)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Participant not found");
        }

        List<NetParticipantRecord> participantRecs = netParticipantRepository.findByparticipant_callsign(participant.getCallsign());
        if ((participantRecs != null) && (!participantRecs.isEmpty())) {
            for (NetParticipantRecord rec: participantRecs) {
                ret.add(new Net(rec.net_callsign(),null, null, null, null, null, null, null, false, null, false, null));
            }
        }
        return ret;
    }

    public void updateParticipant(User loggedInUser, Net net, Participant participant) {
        // update the tactical call sign
        NetParticipantRecord participantRec = netParticipantRepository.find(net.getCallsign(), participant.getCallsign());
        if (participantRec == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Not a net participant");
        }
        NetParticipantRecord rec = new NetParticipantRecord(participantRec.net_participant_id(), participantRec.net_callsign(), participantRec.participant_callsign(), 
                                                participantRec.start_time(), participant.getTacticalCallsign());
        netParticipantRepository.update(rec);
        participant.setStartTime(rec.start_time());
        changePublisherAccessor.publishNetParticipantUpdate(net.getCallsign(), "Update", hydrateParticipant(loggedInUser, participant));
    }

    public Participant deleteAll(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        loggedInUser = userAccessor.get(loggedInUser, loggedInUser.getId());
        if ((!loggedInUser.getRole().equals(UserRole.SYSTEM)) && (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            logger.debug("Insufficient role");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Insufficient role");
        }

        netParticipantRepository.deleteAll();
        return null;
    }

}
