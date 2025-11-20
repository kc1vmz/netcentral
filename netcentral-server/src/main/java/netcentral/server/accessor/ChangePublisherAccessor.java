package netcentral.server.accessor;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.Callsign;
import netcentral.server.object.Net;
import netcentral.server.object.NetMessage;
import netcentral.server.object.Participant;
import netcentral.server.object.ScheduledNet;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.update.CallsignUpdatePayload;
import netcentral.server.object.update.GenericUpdatePayload;
import netcentral.server.object.update.NetMessageUpdatePayload;
import netcentral.server.object.update.NetParticipantUpdatePayload;
import netcentral.server.object.update.NetUpdatePayload;
import netcentral.server.object.update.ObjectUpdatePayload;
import netcentral.server.object.update.TrackedStationUpdatePayload;
import netcentral.server.socket.SocketIoServerRunner;

@Singleton
public class ChangePublisherAccessor {
    private static final Logger logger = LogManager.getLogger(ChangePublisherAccessor.class);

    private int dashboardUpdateCount = 0;
    private ZonedDateTime lastDashboardUpdate = ZonedDateTime.now();

    @Inject
    private SocketIoServerRunner socketIoServerRunner;

    private synchronized int updateDashboardCount() {
        if (dashboardUpdateCount < 100) {
            dashboardUpdateCount++;
            ZonedDateTime now = ZonedDateTime.now();
            if (now.minusSeconds(10).isAfter(lastDashboardUpdate)) {
                dashboardUpdateCount= 0;
                lastDashboardUpdate = now;
            }
        } else {
            dashboardUpdateCount = 0;
        }
        return dashboardUpdateCount;
    }

    private void publishDashboardUpdate() {
        if (updateDashboardCount() == 0) {
            logger.info("Publishing Dashboard update");
            socketIoServerRunner.updateDashboard(null);
        }
    }

    private void publishAllUpdate(String payload) {
        socketIoServerRunner.updateAll(payload);
    }

    private void publishNetUpdate(String payload) {
        socketIoServerRunner.updateNet(payload);
    }

    private void publishScheduledNetUpdate(String payload) {
        socketIoServerRunner.updateScheduledNet(payload);
    }
    
    private void publishCompletedNetUpdate(String payload) {
        socketIoServerRunner.updateCompletedNet(payload);
    }

    private void publishTransceiverUpdate(String payload) {
        socketIoServerRunner.updateTransceiver(payload);
    }

    private void publishUserUpdate(String payload) {
        socketIoServerRunner.updateUser(payload);
    }

    private void publishCallsignUpdate(String payload) {
        socketIoServerRunner.updateCallsign(payload);
    }

    private void publishTrackedStationUpdate(String payload) {
        socketIoServerRunner.updateTrackedStation(payload);
    }

    private void publishObjectUpdate(String payload) {
        socketIoServerRunner.updateObject(payload);
    }

    private void publishNetMessageUpdate(String payload) {
        socketIoServerRunner.updateNetMessage(payload);
    }

    private void publishNetParticipantUpdate(String payload) {
        socketIoServerRunner.updateNetParticipant(payload);
    }

    private void publishParticipantUpdate(String payload) {
        socketIoServerRunner.updateParticipant(payload);
    }

    public void publishNetUpdate(String completedNetId, String action, Net obj) {
        publishDashboardUpdate();
        NetUpdatePayload payload = new NetUpdatePayload();
        payload.setId(completedNetId);
        payload.setAction(action);
        payload.setObjectNow(obj);
        payload.setScheduled(false);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishNetUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing net update");
        }
    }

    public void publishAllUpdate() {
        try {
            publishAllUpdate("");
        } catch (Exception e) {
            logger.error("Exception caught publishing net update");
        }
    }

    public void publishScheduledNetUpdate(String callsign, String action, ScheduledNet obj) {
        publishDashboardUpdate();
        NetUpdatePayload payload = new NetUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObjectScheduled(obj);
        payload.setScheduled(true);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishScheduledNetUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing scheduled net update");
        }
    }

    public void publishNetMessageUpdate(String callsign, String action, NetMessage obj) {
        publishDashboardUpdate();
        NetMessageUpdatePayload payload = new NetMessageUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(obj);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishNetMessageUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing net message update");
        }
    }

    public void publishNetParticipantUpdate(String callsign, String action, Participant obj) {
        publishDashboardUpdate();
        NetParticipantUpdatePayload payload = new NetParticipantUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(obj);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishNetParticipantUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing net participant update");
        }
    }

    public void publishParticipantUpdate(String callsign, String action, Participant obj) {
        publishDashboardUpdate();
        NetParticipantUpdatePayload payload = new NetParticipantUpdatePayload(); // same object as netparticipant
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(obj);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishParticipantUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing participant update");
        }
    }

    public void publishCompletedNetUpdate(String completedNetId, String action) {
        publishDashboardUpdate();

        GenericUpdatePayload payload = new GenericUpdatePayload();
        payload.setId(completedNetId);
        payload.setAction(action);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishCompletedNetUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing net update");
        }
    }

    public void publishTransceiverUpdate(String id, String action) {
        publishDashboardUpdate();

        GenericUpdatePayload payload = new GenericUpdatePayload();
        payload.setId(id);
        payload.setAction(action);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishTransceiverUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing transceiver update");
        }
    }

    public void publishUserUpdate(String id, String action) {
        publishDashboardUpdate();

        GenericUpdatePayload payload = new GenericUpdatePayload();
        payload.setId(id);
        payload.setAction(action);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishUserUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing user update");
        }
    }

    public void publishCallsignUpdate(String callsign, String action, Callsign callsignObject) {
        publishDashboardUpdate();

        CallsignUpdatePayload payload = new CallsignUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(callsignObject);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishCallsignUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing callsign update");
        }
   }

    public void publishTrackedStationUpdate(String callsign, String action, TrackedStation object) {
        publishDashboardUpdate();

        TrackedStationUpdatePayload payload = new TrackedStationUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(object);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishTrackedStationUpdate(json); // simple JSON
        } catch (Exception e) {
            logger.error("Exception caught publishing tracked station update");
        }
    }

    public void publishObjectUpdate(String callsign, String action, APRSObject object) {
        publishDashboardUpdate();
        ObjectUpdatePayload payload = new ObjectUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(object);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishObjectUpdate(json); // simple JSON
        } catch (Exception e) {
            logger.error("Exception caught publishing object update");
        }
    }

    private ObjectWriter getObjectWriter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();

        return ow;
    }

}
