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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.object.APRSWeatherReport;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.Callsign;
import netcentral.server.object.CallsignAce;
import netcentral.server.object.ExpectedParticipant;
import netcentral.server.object.IgnoreStation;
import netcentral.server.object.Net;
import netcentral.server.object.NetMessage;
import netcentral.server.object.NetQuestion;
import netcentral.server.object.NetQuestionAnswer;
import netcentral.server.object.Participant;
import netcentral.server.object.ScheduledNet;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.update.CallsignACEUpdatePayload;
import netcentral.server.object.update.CallsignUpdatePayload;
import netcentral.server.object.update.GenericUpdatePayload;
import netcentral.server.object.update.NetExpectedParticipantUpdatePayload;
import netcentral.server.object.update.NetMessageUpdatePayload;
import netcentral.server.object.update.NetParticipantUpdatePayload;
import netcentral.server.object.update.NetQuestionAnswerUpdatePayload;
import netcentral.server.object.update.NetQuestionUpdatePayload;
import netcentral.server.object.update.NetUpdatePayload;
import netcentral.server.object.update.ObjectUpdatePayload;
import netcentral.server.object.update.TrackedStationUpdatePayload;
import netcentral.server.object.update.WeatherReportUpdatePayload;
import netcentral.server.socket.SocketIoServerRunner;

@Singleton
public class ChangePublisherAccessor {
    private static final Logger logger = LogManager.getLogger(ChangePublisherAccessor.class);

    private int dashboardUpdateCount = 0;
    private ZonedDateTime lastDashboardUpdate = ZonedDateTime.now();

    public static final String CREATE = "Create";
    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    @Inject
    private SocketIoServerRunner socketIoServerRunner;

    private synchronized int updateDashboardCount(boolean reset) {
        if (reset) {
            dashboardUpdateCount = 0;
        }
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

    private void publishDashboardUpdate(boolean force) {
        if (updateDashboardCount(force) == 0) {
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

    private void publishNetQuestionUpdate(String payload) {
        socketIoServerRunner.updateNetQuestion(payload);
    }

    private void publishNetQuestionAnswerUpdate(String payload) {
        socketIoServerRunner.updateNetQuestionAnswer(payload);
    }

    private void publishTrackedStationUpdate(String payload) {
        socketIoServerRunner.updateTrackedStation(payload);
    }

    private void publishIgnoredUpdate(String payload) {
        socketIoServerRunner.updateIgnored(payload);
    }

    private void publishWeatherReportUpdate(String payload) {
        socketIoServerRunner.updateWeatherReport(payload);
    }

    private void publishCallsignACEUpdate(String payload) {
        socketIoServerRunner.updateCallsignACE(payload);
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

    private void publishNetExpectedParticipantUpdate(String payload) {
        socketIoServerRunner.updateNetExpectedParticipant(payload);
    }

    private void publishParticipantUpdate(String payload) {
        socketIoServerRunner.updateParticipant(payload);
    }

    public void publishNetUpdate(String completedNetId, String action, Net obj) {
        publishDashboardUpdate(true);
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
            logger.error("Exception caught publishing net update", e);
        }
    }

    public void publishAllUpdate() {
        try {
            publishAllUpdate("");
        } catch (Exception e) {
            logger.error("Exception caught publishing net update", e);
        }
    }

    public void publishScheduledNetUpdate(String callsign, String action, ScheduledNet obj) {
        publishDashboardUpdate(true);
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
            logger.error("Exception caught publishing scheduled net update", e);
        }
    }

    public void publishNetMessageUpdate(String callsign, String action, NetMessage obj) {
        publishDashboardUpdate(true);
        NetMessageUpdatePayload payload = new NetMessageUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(obj);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishNetMessageUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing net message update", e);
        }
    }

    public void publishNetParticipantUpdate(String callsign, String action, Participant obj) {
        publishDashboardUpdate(true);
        NetParticipantUpdatePayload payload = new NetParticipantUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(obj);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishNetParticipantUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing net participant update", e);
        }
    }

    public void publishNetExpectedParticipantUpdate(String callsign, String action, ExpectedParticipant obj) {
        publishDashboardUpdate(true);
        NetExpectedParticipantUpdatePayload payload = new NetExpectedParticipantUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(obj);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishNetExpectedParticipantUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing net expected participant update", e);
        }
    }

    public void publishParticipantUpdate(String callsign, String action, Participant obj) {
        publishDashboardUpdate(true);
        NetParticipantUpdatePayload payload = new NetParticipantUpdatePayload(); // same object as netparticipant
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(obj);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishParticipantUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing participant update", e);
        }
    }

    public void publishCompletedNetUpdate(String completedNetId, String action) {
        publishDashboardUpdate(true);

        GenericUpdatePayload payload = new GenericUpdatePayload();
        payload.setId(completedNetId);
        payload.setAction(action);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishCompletedNetUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing net update", e);
        }
    }

    public void publishTransceiverUpdate(String id, String action) {
        publishDashboardUpdate(true);

        GenericUpdatePayload payload = new GenericUpdatePayload();
        payload.setId(id);
        payload.setAction(action);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishTransceiverUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing transceiver update", e);
        }
    }

    public void publishUserUpdate(String id, String action) {
        publishDashboardUpdate(true);

        GenericUpdatePayload payload = new GenericUpdatePayload();
        payload.setId(id);
        payload.setAction(action);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishUserUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing user update", e);
        }
    }

    public void publishCallsignUpdate(String callsign, String action, Callsign callsignObject) {
        publishDashboardUpdate(false);

        CallsignUpdatePayload payload = new CallsignUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(callsignObject);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishCallsignUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing callsign update", e);
        }
   }

    public void publishNetQuestionUpdate(String callsign, String action, NetQuestion netQuestionObject) {
        NetQuestionUpdatePayload payload = new NetQuestionUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(netQuestionObject);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishNetQuestionUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing NetQuestion update", e);
        }
   }

    public void publishNetQuestionAnswerUpdate(String id, String action, NetQuestionAnswer netQuestionObject) {
        NetQuestionAnswerUpdatePayload payload = new NetQuestionAnswerUpdatePayload();
        payload.setId(id);
        payload.setAction(action);
        payload.setObject(netQuestionObject);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishNetQuestionAnswerUpdate(json);
        } catch (Exception e) {
            logger.error("Exception caught publishing NetQuestionAnswer update", e);
        }
   }

   public void publishTrackedStationUpdate(String callsign, String action, TrackedStation object) {
        publishDashboardUpdate(false);

        TrackedStationUpdatePayload payload = new TrackedStationUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(object);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishTrackedStationUpdate(json); // simple JSON
        } catch (Exception e) {
            logger.error("Exception caught publishing tracked station update", e);
        }
    }

    public void publishIgnoredUpdate(String callsign, String action, IgnoreStation object) {
        publishDashboardUpdate(false);

        TrackedStationUpdatePayload payload = new TrackedStationUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        TrackedStation trackedStation = new TrackedStation();
        trackedStation.setCallsign(object.getCallsign());
        trackedStation.setType(object.getType());
        trackedStation.setId(object.getCallsign());
        trackedStation.setLat(object.getLat());
        trackedStation.setLon(object.getLon());
        trackedStation.setLastHeard(object.getIgnoreStartTime());  // reuse this variable
        payload.setObject(trackedStation);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishIgnoredUpdate(json); // simple JSON
        } catch (Exception e) {
            logger.error("Exception caught publishing ignored update", e);
        }
    }

    public void publishCallsignACEUpdate(String callsign, String action, CallsignAce object) {
        CallsignACEUpdatePayload payload = new CallsignACEUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(object);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishCallsignACEUpdate(json); // simple JSON
        } catch (Exception e) {
            logger.error("Exception caught publishing tracked station update", e);
        }
    }

    public void publishWeatherReportUpdate(String callsign, String action, APRSWeatherReport object) {
        WeatherReportUpdatePayload payload = new WeatherReportUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(object);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishWeatherReportUpdate(json); // simple JSON
        } catch (Exception e) {
            logger.error("Exception caught publishing tracked station update", e);
        }
    }

    public void publishObjectUpdate(String callsign, String action, APRSObject object) {
        publishDashboardUpdate(false);
        ObjectUpdatePayload payload = new ObjectUpdatePayload();
        payload.setCallsign(callsign);
        payload.setAction(action);
        payload.setObject(object);

        try {
            ObjectWriter ow = getObjectWriter();
            String json = ow.writeValueAsString(payload);

            publishObjectUpdate(json); // simple JSON
        } catch (Exception e) {
            logger.error("Exception caught publishing object update", e);
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
