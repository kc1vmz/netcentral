package netcentral.server.accessor;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.object.APRSObject;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.User;

@Singleton
public class ObjectBeaconAccessor {
    private static final Logger logger = LogManager.getLogger(ObjectBeaconAccessor.class);
    private boolean stopBeacon = false;

    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;


    public void beaconObjects() {
        if (!beaconStayRunning()) {
            return;
        }
        User user = new User();

        // need to beacon each NetCentral object

        List<APRSObject> netCentralObjects = getObjects(user);
        if (netCentralObjects.isEmpty()) {
            return;
        }

        for (APRSObject netCentralObject : netCentralObjects) {
            logger.debug("Beaconing object "+netCentralObject.getCallsignFrom());
            try {
                transceiverMessageAccessor.sendObject(user, netCentralObject.getCallsignFrom(), netCentralObject.getCallsignFrom(),
                                                            netCentralObject.getComment(), netCentralObject.isAlive(),
                                                            netCentralObject.getLat(), netCentralObject.getLon());
            } catch (Exception e) {
                logger.error("Error caught beaconing object", e);
            }
        }
    }

    private List<APRSObject> getObjects(User user) {
        List<APRSObject> objects = new ArrayList<>();

        try {
            List<APRSObject> netCentralPriorityObjects = aprsObjectAccessor.getNetCentralObjects(user, false, true) ;
            if ((netCentralPriorityObjects != null) && (!netCentralPriorityObjects.isEmpty())) {
                objects.addAll(netCentralPriorityObjects);
            }
        } catch (Exception e) {
            logger.error("Exception caught getting priority objects", e);
        }

        return objects;
    }

    public boolean beaconStayRunning() {
        return !stopBeacon;
    }
    public void shutdownBeacon() {
        stopBeacon = true;
    }
}
