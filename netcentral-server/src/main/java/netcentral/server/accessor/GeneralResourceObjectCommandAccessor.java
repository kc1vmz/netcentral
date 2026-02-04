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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.enums.ObjectType;
import com.kc1vmz.netcentral.aprsobject.object.APRSMessage;
import com.kc1vmz.netcentral.aprsobject.object.APRSObject;


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
        transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, priorityObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
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
            transceiverMessageAccessor.sendMessage(loggedInUser, transceiverSourceId, generalResourceObject.getCallsignFrom(), innerAPRSMessage.getCallsignFrom(), "Bad or unauthorized message.");
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
}
