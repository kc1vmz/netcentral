package netcentral.server.accessor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.ScheduledNetType;
import netcentral.server.object.Net;
import netcentral.server.object.NetMessage;
import netcentral.server.object.Participant;
import netcentral.server.object.ScheduledNet;
import netcentral.server.object.User;


@Singleton
public class NetSchedulerAccessor {
    private static final Logger logger = LogManager.getLogger(NetSchedulerAccessor.class);

    @Inject
    private ScheduledNetAccessor scheduledNetAccessor;
    @Inject
    private NetAccessor netAccessor;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;
    @Inject
    private CompletedNetAccessor completedNetAccessor;
    @Inject
    private NetMessageAccessor netMessageAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private boolean stop = false;

    public void startAndStopNets() {
        writeLock.lock();
        try {
            // determine who to start
            ZonedDateTime now = ZonedDateTime.now();
            startScheduledNets(now);
            stopScheduledNets(now);
        } finally {
            writeLock.unlock();
        }
    }

    private void stopScheduledNets(ZonedDateTime now) {
        List<Net> nets = null;
        User user = new User();
        
        try {
            nets = netAccessor.getAll(null, null);
        } catch (Exception e){
        }

        if (nets != null) {
            for (Net net : nets) {
                try {
                    ScheduledNet scheduledNet = scheduledNetAccessor.get(user, net.getCallsign());
                    if (scheduledNet != null) {
                        // there is a scheduled net by this name
                        if (now.isAfter(net.getStartTime().plusHours(scheduledNet.getDuration()))) {
                            closeScheduledNet(user, net, now);
                            if (scheduledNet.getType().equals(ScheduledNetType.ONE_TIME_ONLY)) {
                                // delete it upon completion
                                deleteScheduledNet(user, scheduledNet);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private void deleteScheduledNet(User user, ScheduledNet scheduledNet) {
        scheduledNetAccessor.delete(user, scheduledNet.getCallsign());
    }

    private void closeScheduledNet(User loggedInUser, Net net, ZonedDateTime now) {
        logger.debug("Ending net " + net.getCallsign());

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
        netAccessor.delete(loggedInUser, net.getCallsign());
    }

    private void startScheduledNets(ZonedDateTime now) {
        List<ScheduledNet> scheduledNets = scheduledNetAccessor.getAll(null, null);
        if (scheduledNets != null) {
            for (ScheduledNet scheduledNet : scheduledNets) {
                if (scheduledNet.getNextStartTime().isBefore(now)) {
                    startScheduledNet(scheduledNet, now);
                }
            }
        }
    }

    private void startScheduledNet(ScheduledNet scheduledNet, ZonedDateTime now) {
        Net net = null;
        User user = new User();

        try {
            net = netAccessor.getByCallsign(null, scheduledNet.getCallsign());
        } catch (Exception e) {
        }

        if (net == null) {
            // net not started
            String completedNetId = UUID.randomUUID().toString();
            net = new Net(scheduledNet.getCallsign(), scheduledNet.getName(), scheduledNet.getDescription(), 
                scheduledNet.getVoiceFrequency(), now, completedNetId, scheduledNet.getLat(), scheduledNet.getLon(), scheduledNet.isAnnounce(), 
                scheduledNet.getCreatorName(), scheduledNet.isCheckinReminder(), scheduledNet.getCheckinMessage());
            try {
                net = netAccessor.create(user, net);
                if (net == null) {
                    logger.error(String.format("Net %s not created", scheduledNet.getCallsign()));
                    return;
                }
                // update next and last start times
                scheduledNet.setLastStartTime(now);
                scheduledNet.setNextStartTime(scheduledNet.calculateNextStartTime());
                scheduledNetAccessor.update(user, scheduledNet.getCallsign(), scheduledNet);
            } catch (Exception e) {
                logger.info("Exception caught looking for net " + scheduledNet.getCallsign(), e);
            }
        } else {
//            logger.info(String.format("Net %s is already started", scheduledNet.getCallsign()));
        }
    }

    public void shutdown() {
        stop = true;
    }

    public boolean stayRunning() {
        return !stop;
    }
}
