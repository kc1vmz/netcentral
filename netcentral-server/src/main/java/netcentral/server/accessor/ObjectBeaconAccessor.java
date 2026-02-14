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

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.ResourceObjectKeyValuePair;
import netcentral.server.object.User;

@Singleton
public class ObjectBeaconAccessor {
    private static final Logger logger = LogManager.getLogger(ObjectBeaconAccessor.class);
    private boolean stopBeacon = false;

    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;
    @Inject
    private ResourceObjectKVAccessor resourceObjectKVAccessor;
    @Inject
    private FederatedObjectReporterAccessor federatedObjectReporterAccessor;

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
                if (netCentralObject.getType().equals(ObjectType.RESOURCE)) {
                    beaconData(user, netCentralObject);
                }
            } catch (Exception e) {
                logger.error("Error caught beaconing object", e);
            }
        }
 
        for (APRSObject netCentralObject : netCentralObjects) {
            federatedObjectReporterAccessor.announce(user, netCentralObject);
        }
 
    }

    private void beaconData(User user, APRSObject netCentralObject) {
        try {
            List<ResourceObjectKeyValuePair> kvPairs = resourceObjectKVAccessor.get(user, netCentralObject.getId());
            if ((kvPairs != null) && (!kvPairs.isEmpty())) {
                for (ResourceObjectKeyValuePair kvPair : kvPairs) {
                    if (kvPair.isBroadcast()) {
                        String message = String.format("%s:%s", kvPair.getKey(), kvPair.getValue());
                        transceiverMessageAccessor.sendMessage(user, netCentralObject.getCallsignFrom(), netCentralObject.getCallsignFrom(), message);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private List<APRSObject> getObjects(User user) {
        List<APRSObject> objects = new ArrayList<>();

        try {
            List<APRSObject> netCentralPriorityObjects = aprsObjectAccessor.getNetCentralObjects(user, true, true, true) ;
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
