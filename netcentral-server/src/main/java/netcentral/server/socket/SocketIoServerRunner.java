package netcentral.server.socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.config.NetConfigServerConfig;

@Singleton
public class SocketIoServerRunner implements ApplicationEventListener<StartupEvent> {
    private static final Logger logger = LogManager.getLogger(SocketIoServerRunner.class);

    @Inject
    private NetConfigServerConfig netConfigServerConfig;

    private final SocketIOServer server;

    public SocketIoServerRunner() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setOrigin("http://localhost"); 
        config.setPort(8881 /*netConfigServerConfig.getUpdatePort()*/); // Separate port for Socket.IO
        config.setHttpCompression(false);
        config.setWebsocketCompression(false);
        config.setUpgradeTimeout(10000000);
        config.setPingTimeout(10000000);
        config.setPingInterval(10000000);
        SocketConfig socketConfig = config.getSocketConfig();
        socketConfig.setReuseAddress(true);
        socketConfig.setSoLinger(0);
        socketConfig.setTcpKeepAlive(true);

        this.server = new SocketIOServer(config);
    }

    public void updateDashboard(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateDashboard", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateDashboard", e);
        }
    }

    public void updateAll(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateAll", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateAll", e);
        }
    }

    public void updateNet(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateNet", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateNet", e);
        }
    }

    public void updateScheduledNet(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateScheduledNet", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateScheduledNet", e);
        }
    }

    public void updateCompletedNet(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateCompletedNet", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateCompletedNet", e);
        }
    }

    public void updateTransceiver(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateTransceiver", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateTransceiver", e);
        }
    }

    public void updateUser(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateUser", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateUser", e);
        }
    }

    public void updateCallsign(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateCallsign", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateCallsign", e);
        }
    }

    public void updateTrackedStation(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateTrackedStation", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateTrackedStation", e);
        }
    }

    public void updateObject(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateObject", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateObject", e);
        }
    }

    public void updateNetMessage(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateNetMessage", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateNetMessage", e);
        }
    }

    public void updateNetParticipant(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateNetParticipant", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateNetParticipant", e);
        }
    }

    public void updateParticipant(String payload) {
        try {
            server.getBroadcastOperations().sendEvent("updateParticipant", payload);
        } catch (Exception e) {
            logger.error("Exception caught broadcasting updateParticipant", e);
        }
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        server.start();
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
    }
}