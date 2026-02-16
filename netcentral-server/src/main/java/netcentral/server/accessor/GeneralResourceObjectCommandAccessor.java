package netcentral.server.accessor;

import java.util.ArrayList;

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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.constants.APRSQueryType;
import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;
import com.kc1vmz.netcentral.aprsobject.object.reports.APRSNetCentralObjectAnnounceReport;
import com.kc1vmz.netcentral.common.constants.NetCentralQueryType;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.ResourceObjectKeyValuePair;
import netcentral.server.object.User;

@Singleton
public class GeneralResourceObjectCommandAccessor {
    private static final Logger logger = LogManager.getLogger(GeneralResourceObjectCommandAccessor.class);
    private static final String REPORT_COMMAND = "REPORT";

    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;
    @Inject
    private CallsignAceAccessor accessControlAccessor;
    @Inject
    private StatisticsAccessor statisticsAccessor;
    @Inject
    private ResourceObjectKVAccessor resourceObjectKVAccessor;
    @Inject
    private FederatedObjectReporterAccessor federatedObjectReporterAccessor;


    public void processMessage(User loggedInUser, APRSObject generalResourceObject, APRSMessage innerAPRSMessage, String transceiverSourceId) {
        // message is for this general resource object - act and respond
        String message = preStrip(innerAPRSMessage.getMessage());

        if (innerAPRSMessage.getMessageNumber() != null) {
            ackMessage(loggedInUser, innerAPRSMessage, transceiverSourceId);
        }

        if ((message == null) || (message.isEmpty()) || (message.isBlank())) {
            return;
        }

        if (message.startsWith("ack")) {
            return;
        }

        if (generalResourceObject.getCallsignFrom().equalsIgnoreCase(innerAPRSMessage.getCallsignFrom())) {
            // the message is from inside the house
            return;
        }

        // is this a directed query
        if (message.startsWith(APRSQueryType.PREFIX)) {
            processDirectedStatusQuery(loggedInUser, innerAPRSMessage, transceiverSourceId, generalResourceObject);
            return;
        }

        boolean canChange = allowedToChange(loggedInUser, generalResourceObject, innerAPRSMessage);

        switch (generalResourceObject.getType()) {
            case ObjectType.RESOURCE -> processGeneralResourceCommand(loggedInUser, generalResourceObject, innerAPRSMessage, transceiverSourceId, message, canChange);
            default -> processBadCommand(loggedInUser, generalResourceObject, innerAPRSMessage, transceiverSourceId);
        }
    }

    private boolean allowedToChange(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage) {
        return accessControlAccessor.isWriteable(loggedInUser, priorityObject, innerAPRSMessage.getCallsignFrom());
    }

    private String preStrip(String message) {
        // some devices put space at the beginning of a message :/ 
        String ret = message;

        while ((ret.length() > 0) && (ret.startsWith(" "))) {
            ret = ret.substring(1);
        }

        return ret;
    }

    private void processBadCommand(User loggedInUser, APRSObject priorityObject, APRSMessage innerAPRSMessage, String transceiverSourceId) {
        logger.warn("Unexpected or unauthorized message sent to general resource object - "+((priorityObject.getCallsignFrom() != null) ? priorityObject.getCallsignFrom() : "UNKNOWN"));
        logger.warn("Message: "+((innerAPRSMessage.getMessage() != null) ? innerAPRSMessage.getMessage() : "UNKNOWNMESSAGE"));
        if (!innerAPRSMessage.getCallsignFrom().equalsIgnoreCase(priorityObject.getCallsignFrom())) {
            // dont send bad command back to itself
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
        }
    }

    private void processGeneralResourceCommand(User loggedInUser, APRSObject generalResourceObject, APRSMessage innerAPRSMessage, String transceiverSourceId, String message, boolean canChange) {
        boolean commandAccepted = false;

        if (message == null) {
            return;
        }
        if (REPORT_COMMAND.equalsIgnoreCase(message)) {
            sendKVPairs(loggedInUser, generalResourceObject, innerAPRSMessage, transceiverSourceId, message);
            return;
        }

        // is this a federated announcement message 
        try {
            APRSNetCentralObjectAnnounceReport report = APRSNetCentralObjectAnnounceReport.isValid(generalResourceObject.getCallsignFrom(), message);
            if (report != null) {
                // ignore this announcement - we already know
                return;
            }
        } catch (Exception e) {
        }

        if (message.contains("=")) {
            // setting a key
            if (!canChange) {
                processBadCommand(loggedInUser, generalResourceObject, innerAPRSMessage, transceiverSourceId);
                return;
            }
            String [] messageSplit = message.split("=");
            if (messageSplit.length == 2) {
                // update
                commandAccepted = updateKVPair(loggedInUser, generalResourceObject, messageSplit[0], messageSplit[1]);
            } else if (messageSplit.length == 1) {
                // remove key
                commandAccepted = deleteKVPair(loggedInUser, generalResourceObject, messageSplit[0]);
            } else {
                processBadCommand(loggedInUser, generalResourceObject, innerAPRSMessage, transceiverSourceId);
                return;
            }
        } else {
            // send a key
            sendKVPair(loggedInUser, generalResourceObject, innerAPRSMessage, transceiverSourceId, message);
        }

        if (commandAccepted) {
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, generalResourceObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Update message accepted");
        }  else {
            if (!innerAPRSMessage.getCallsignFrom().equalsIgnoreCase(generalResourceObject.getCallsignFrom())) {
                // dont send bad command back to itself
                transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, generalResourceObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
            }
        }
    }

    private boolean updateKVPair(User loggedInUser, APRSObject generalResourceObject, String key, String value) {
        resourceObjectKVAccessor.update(loggedInUser, generalResourceObject.getId(), key, value, null);
        return true;
    }

    private boolean deleteKVPair(User loggedInUser, APRSObject generalResourceObject, String key) {
        resourceObjectKVAccessor.delete(loggedInUser, generalResourceObject.getId(), key);
        return true;
    }

    private void ackMessage(User loggedInUser, APRSMessage msg, String transceiverSourceId) {
        logger.info("Acknowledging message");
        statisticsAccessor.incrementAcksRequested();
        statisticsAccessor.incrementAcksSent();
        transceiverMessageAccessor.sendAckMessage(loggedInUser, transceiverSourceId, msg.getCallsignTo(), msg.getCallsignFrom(), "ack"+msg.getMessageNumber());
    }

    private void sendKVPairs(User loggedInUser, APRSObject generalResourceObject, APRSMessage innerAPRSMessage, String transceiverSourceId, String message) {
        try {
            List<ResourceObjectKeyValuePair> kvPairs = resourceObjectKVAccessor.get(loggedInUser, generalResourceObject.getId());
            if (kvPairs != null) {
                for (ResourceObjectKeyValuePair kvPair: kvPairs) {
                    transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, generalResourceObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), 
                                                            kvPair.getKey()+"="+kvPair.getValue());
                }
            } else {
                transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, generalResourceObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), 
                                                            "No values found");
            }
        } catch (Exception e) {
           logger.error("Exception caught sending KV pair", e);
        }
    }

    private void sendKVPair(User loggedInUser, APRSObject generalResourceObject, APRSMessage innerAPRSMessage, String transceiverSourceId, String message) {
        try {
            String value = resourceObjectKVAccessor.get(loggedInUser, generalResourceObject.getId(), message);
            if (value != null) {
                transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, generalResourceObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), 
                                                            message+"="+value);
            } else {
                transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, generalResourceObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), 
                                                            message+" not found");
            }
        } catch (Exception e) {
           logger.error("Exception caught sending KV pair", e);
        }
    }

    public void processDirectedStatusQuery(User loggedInUser, APRSMessage innerAPRSMessage, String transceiverSourceId, APRSObject aprsObject) {
        if (loggedInUser == null) {
            return;
        }
        if (innerAPRSMessage == null) {
            return;
        }
        if (innerAPRSMessage.getMessage() == null) {
            return;
        }
        if (!innerAPRSMessage.getMessage().startsWith(APRSQueryType.PREFIX)) {
            return;
        }

        try {
            String queryType = innerAPRSMessage.getMessage().substring(1);

            if (queryType.equalsIgnoreCase(NetCentralQueryType.COMMANDS)) {
                // list commands
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getGeneralObjectCommandsResponse(loggedInUser, aprsObject));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.INFO)) {
                // send commands and info
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getGeneralObjectCommandsResponse(loggedInUser, aprsObject));
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getGeneralObjectInfoResponse(loggedInUser, aprsObject));
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_OBJECTS)) {
                // announce object
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_POSITION)) {
                // send position packet
                if ((aprsObject.getLat() != null) && (aprsObject.getLon() != null)) {
                    transceiverMessageAccessor.sendObject(loggedInUser, aprsObject.getCallsignFrom(), aprsObject.getCallsignFrom(),
                                                            aprsObject.getComment(), aprsObject.isAlive(),
                                                            aprsObject.getLat(), aprsObject.getLon());
                }
            } else if (queryType.equalsIgnoreCase(APRSQueryType.APRS_STATUS)) {
                // send net status
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE)) {
                // send object type
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getGeneralObjectTypeResponse(loggedInUser, aprsObject));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.GENERAL_INFO)) {
                // send general info
                federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, getGeneralObjectInfoResponse(loggedInUser, aprsObject));
            } else if (queryType.equalsIgnoreCase(NetCentralQueryType.GENERAL_TOKEN_VALUE)) {
                // send KV pairs K=V,K=V...
                List<String> responses = getKVPairs(loggedInUser, aprsObject);
                if (!responses.isEmpty()) {
                    for (String message: responses) {
                        federatedObjectReporterAccessor.sendCommandResponse(loggedInUser, transceiverSourceId, innerAPRSMessage, message);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private String getGeneralObjectTypeResponse(User loggedInUser, APRSObject generalResourceObject) {
        return String.format("%s:%s", NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE, generalResourceObject.getType().toString());
    }

    private String getGeneralObjectCommandsResponse(User loggedInUser, APRSObject generalResourceObject) {
        return String.format("%s:%s,%s,%s,%s,%s,%s,%s", NetCentralQueryType.COMMANDS, NetCentralQueryType.COMMANDS, APRSQueryType.APRS_OBJECTS, APRSQueryType.APRS_POSITION, APRSQueryType.APRS_STATUS,
                                                NetCentralQueryType.GENERAL_INFO, NetCentralQueryType.GENERAL_TOKEN_VALUE, NetCentralQueryType.NET_CENTRAL_OBJECT_TYPE);
    }

    private String getGeneralObjectInfoResponse(User loggedInUser, APRSObject generalResourceObject) {
        return String.format("%s:%s,%s", NetCentralQueryType.GENERAL_INFO, generalResourceObject.getCallsignTo(), generalResourceObject.getComment());
    }

    private List<String> getKVPairs(User loggedInUser, APRSObject generalResourceObject) {
        List<String> ret = new ArrayList<>();

        try {
            List<ResourceObjectKeyValuePair> kvPairs = resourceObjectKVAccessor.get(loggedInUser, generalResourceObject.getId());
            if (kvPairs != null) {
                for (ResourceObjectKeyValuePair kvPair: kvPairs) {
                    ret.add(String.format("%s:%s=%s", NetCentralQueryType.GENERAL_TOKEN_VALUE, kvPair.getKey(), kvPair.getValue()));
                }
            }
        } catch (Exception e) {
        }

        return ret;
    }
}
