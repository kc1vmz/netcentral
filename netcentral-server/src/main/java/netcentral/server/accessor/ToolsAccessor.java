package netcentral.server.accessor;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.WinlinkSessionState;
import netcentral.server.object.User;
import netcentral.server.object.WinlinkSessionCacheEntry;
import netcentral.server.object.request.NTSSendRadiogramRequest;
import netcentral.server.object.request.WinlinkSendMessageRequest;
import netcentral.server.object.response.NTSSendRadiogramResponse;
import netcentral.server.object.response.WinlinkSendMessageResponse;

/*
 * https://winlink.org/APRSLink
 * 
 * LOGIN -
    Send any characters to initiate login. The CMS will respond with a challenge consisting of three digits who's values represent positions of characters within your password. 
    See 'RESP'.

    RESP -
    This is a six character response to the login challenge. Respond with three password characters corresponding to the positions in the challenge plus three additional 
    characters of your choosing (in any order). 
    Example: Password is AbC123. Login challenge is: 425. You send '1b2Qz5'. 
    AbZ21t would also be valid since it contains the characters 1, b, and 2 which correspond to the 4th, 2nd, and 5th character in your password.

    LOGOFF -
    Your login will expire after approximately two hours. You can manually log out anytime by issuing the "B" or "BYE" command.

    MESSAGE (each line is an APRS message):
    SP sam@iam.com Test Message
    Some text for the body of the message.
    Some more text for the message.
    /EX
 */
@Singleton
public class ToolsAccessor {
    private static final Logger logger = LogManager.getLogger(ToolsAccessor.class);

    @Inject
    private WinlinkSessionCacheAccessor winlinkSessionCacheAccessor;
    @Inject
    private TransceiverCommunicationAccessor transceiverCommunicationAccessor;

    private static final String WINLINK_GATEWAY_CALLSIGN = "WLNK-1";
    private static final String NTS_GATEWAY_CALLSIGN = "NTSGTE";

    public String getWinlinkGatewayCallsign() {
        return WINLINK_GATEWAY_CALLSIGN;
    }

    public WinlinkSendMessageResponse initiateWinlinkMessage(User loggedInUser, WinlinkSendMessageRequest request) {
        logger.debug(String.format("Sending Winlink message from callsign %s to recipient %s", request.callsign(), request.recipient()));
        WinlinkSendMessageResponse response = new WinlinkSendMessageResponse();

        WinlinkSessionCacheEntry entry = new WinlinkSessionCacheEntry(request.callsign(), request.password(), request.recipient(), request.subject(), request.message()) ;

        boolean added = winlinkSessionCacheAccessor.add(request.callsign(), entry);
        if (added) {
            response.setStatus(entry.getState().toString());
            response.setSuccess(true);
        } else {
            response.setStatus(WinlinkSessionState.TOO_MANY_SIMULTANEOUS_REQUESTS.toString());
            response.setSuccess(false);
        }

        sendWinlinkInitiatingMessage(loggedInUser, entry);

        // need to persist in cache for state machine
        // WAITING, CHALLENGING, NOT_VALIDATED, SENDING
        // callsign, password, recipient, message, 3 digit challenge, challenge password, state, messagetime
        // clear password and challenge password once validated

        // put in cache
        // send message for login initiation to WLNK-1 - "Net Central login request from CALLSIGN"
        // return from this call

        // in message processor, listen for messages from WLNK-1
        // check if callsign in the cache
        // if so, 
        //  if in WAITING state, put three digit value in cache, determine six character response and send it; mark as CHALLENGING in cache
        //  if in CHALLENGING state, check if response starts with "Hello CALLSIGN"; if so, mark as SENDING and send message in chunks
        //
        // ack all messsages from WLNK-1 sent from cached callsign ?
        //
        // when entire message sent, send /EX then send BYE
        // remove from cache

        return response;
    }

    /*
     * This is a six character response to the login challenge. 
     * Respond with three password characters corresponding to the positions in the challenge plus three additional characters of your choosing (in any order). 
     * Example: Password is AbC123. Login challenge is: 425. You send '1b2Qz5'. 
     * AbZ21t would also be valid since it contains the characters 1, b, and 2 which correspond to the 4th, 2nd, and 5th character in your password.
     * 
     */
    public String calculateWinlinkSixDigitResponse(WinlinkSessionCacheEntry entry) {
        String ret = "";
        Random random = new Random();
        char filler1 = (char) (random.nextInt(26) + 'a'); 
        char filler2 = (char) (random.nextInt(26) + 'A'); 
        char filler3 = (char) (random.nextInt(26) + 'a'); 
        int method = random.nextInt(6);

        int digit1 = Integer.parseInt(entry.getThreeDigitChallenge().substring(0, 1));
        int digit2 = Integer.parseInt(entry.getThreeDigitChallenge().substring(1, 2));
        int digit3 = Integer.parseInt(entry.getThreeDigitChallenge().substring(2));

        String password = entry.getPassword();
        char password1 = password.charAt(digit1-1);
        char password2 = password.charAt(digit2-1);
        char password3 = password.charAt(digit3-1);

        switch (method) {
            // six different ways to put the string together
            case 0:
                ret = ""+filler1 + password1 +  filler2 + password2 +  filler3 + password3;
                break;
            case 1:
                ret = ""+filler3 + password2 +  filler1 + password1 +  filler2 + password3;
                break;
            case 2:
                ret = ""+filler2 + password3 +  filler3 + password2 +  filler1 + password1;
                break;
            case 3:
                ret = ""+password1 + filler1 +  password2 + filler2 +  password3 + filler3;
                break;
            case 4:
                ret = ""+password3 + filler2 +  password1 + filler1 +  password2 + filler3;
                break;
            default :
                ret = ""+password2 + filler3 +  password3 + filler2 +  password1 + filler1;
                break;
        }
        return ret;
    }

    private void sendWinlinkInitiatingMessage(User loggedInUser, WinlinkSessionCacheEntry entry) {
        // send message for login initiation to WLNK-1 - "Net Central login request from CALLSIGN"
        logger.debug("Sending Winlink initiating message for callsign "+entry.getCallsign());
        String message = String.format("Net Central login request from %s", entry.getCallsign());
        transceiverCommunicationAccessor.sendMessage(loggedInUser, entry.getCallsign(), WINLINK_GATEWAY_CALLSIGN, message);
    }

    public void sendWinlinkMessage(User loggedInUser, WinlinkSessionCacheEntry entry) {
        // send message to WLNK-1 
        logger.debug("Sending Winlink  message for callsign "+entry.getCallsign());

        String messageCopyOriginal = entry.getMessage();

        if (messageCopyOriginal.length() > 20) {
            // large message (20 is arbitrary, could be longer)
            String header = String.format("SP %s %s", entry.getRecipient(), entry.getSubject());
            transceiverCommunicationAccessor.sendMessage(loggedInUser, entry.getCallsign(), WINLINK_GATEWAY_CALLSIGN, header);

            String fullMessage = messageCopyOriginal;
            while (fullMessage.length() != 0) {
                String partialMessage = "";
                if (fullMessage.length() > 50) {
                    partialMessage = prepareMessage(fullMessage.substring(0, 50));
                    fullMessage = fullMessage.substring(50);
                } else {
                    partialMessage = prepareMessage(fullMessage);
                    fullMessage = "";
                }
                
                transceiverCommunicationAccessor.sendMessage(loggedInUser, entry.getCallsign(), WINLINK_GATEWAY_CALLSIGN, partialMessage);
            }

            // terminate the message
            transceiverCommunicationAccessor.sendMessage(loggedInUser, entry.getCallsign(), WINLINK_GATEWAY_CALLSIGN, "/EX");
        } else {
            // send short message
            String messageText = "";
            if ((entry.getSubject() == null) || (entry.getSubject().isEmpty())) {
                messageText = messageCopyOriginal;
            } else {
                messageText = String.format("(%s) %s", entry.getSubject(), messageCopyOriginal);               
            }
            String shortMessage = String.format("SMS %s %s", entry.getRecipient(), messageText);
            transceiverCommunicationAccessor.sendMessage(loggedInUser, entry.getCallsign(), WINLINK_GATEWAY_CALLSIGN, shortMessage);
        }
        // terminate the session
        transceiverCommunicationAccessor.sendMessage(loggedInUser, entry.getCallsign(), WINLINK_GATEWAY_CALLSIGN, "BYE");
        // remove from cache
        winlinkSessionCacheAccessor.remove(entry.getCallsign());
    }

    private String prepareMessage(String message) {
        // send login challenge message 
        logger.debug("Preparing Winlink message");
        String ret = message;

        if (ret.contains("\n")) {
            ret = ret.replace("\n", "");
        }
        if (ret.startsWith("ack")) {
            ret = "a c k"+ret.substring(3);
        } else if (ret.startsWith("rej")) {
            ret = "r e j"+ret.substring(3);
        }

        return ret;
    }

    public void sendWinlinkChallengeMessage(User loggedInUser, WinlinkSessionCacheEntry entry) {
        // send login challenge message 
        logger.debug("Sending Winlink challenge message for callsign "+entry.getCallsign());
        transceiverCommunicationAccessor.sendMessage(loggedInUser, entry.getCallsign(), WINLINK_GATEWAY_CALLSIGN, entry.getSixDigitResponse());
    }


    public WinlinkSendMessageResponse getStatusWinlinkMessage(User loggedInUser, String callsign) {
        logger.debug(String.format("Getting status for Winlink message from callsign %s", callsign));
        WinlinkSendMessageResponse response = new WinlinkSendMessageResponse();

        WinlinkSessionCacheEntry entry = winlinkSessionCacheAccessor.find(callsign);
        if (entry != null) {
            response.setStatus(entry.getState().toString());
            response.setSuccess(true);
        } else {
            response.setStatus(WinlinkSessionState.NOT_FOUND.toString());
            response.setSuccess(false);
        }
        return response;
    }

    public WinlinkSessionCacheEntry getWinlinkMessageEntry(User loggedInUser, String callsign) {
        logger.debug(String.format("Getting Winlink message entry from callsign %s", callsign));

        WinlinkSessionCacheEntry entry = winlinkSessionCacheAccessor.find(callsign);
        return entry;
    }

    public WinlinkSessionCacheEntry updateWinlinkMessageEntry(User loggedInUser, String callsign, WinlinkSessionCacheEntry entry) {
        logger.debug(String.format("Updating Winlink message entry from callsign %s", callsign));

        return winlinkSessionCacheAccessor.update(callsign, entry);
    }

    public WinlinkSendMessageResponse deleteStatusWinlinkMessage(User loggedInUser, String callsign) {
        logger.debug(String.format("Getting status for Winlink message from callsign %s", callsign));
        WinlinkSendMessageResponse response = new WinlinkSendMessageResponse();

        WinlinkSessionCacheEntry entry = winlinkSessionCacheAccessor.find(callsign);
        if (entry != null) {
            winlinkSessionCacheAccessor.remove(callsign);
            response.setStatus(WinlinkSessionState.REMOVED.toString());
            response.setSuccess(true);
        } else {
            response.setStatus(WinlinkSessionState.NOT_FOUND.toString());
            response.setSuccess(false);
        }
        return response;
    }

    public NTSSendRadiogramResponse initiateNTSRadiogram(User loggedInUser, NTSSendRadiogramRequest action) {
        NTSSendRadiogramResponse response = new NTSSendRadiogramResponse();

        if ((action.precedence() == null) || (action.callsignOrigin() == null) || (action.locationOrigin() == null) || (action.dateFiled() == null) || (action.name() == null) || 
            (action.signature() == null) || (action.message1() == null) || (action.check() == null)) {
                response.setSuccess(false);
                response.setStatus("Missing information");
                return response;
        } else {
                response.setSuccess(true);
                response.setStatus("");
        }

        if (!verifyCount(action)) {
            response.setSuccess(false);
            response.setStatus("Check mismatch");
            return response;
        }

        String lineQTC = String.format("QTC 1");
        String lineNumber = String.format("N#\\%s\\%s\\%s\\%s\\%s\\%s\\%s\\%s", action.number().toUpperCase(), action.precedence().toUpperCase(), action.hx().toUpperCase(), 
                                                action.callsignOrigin().toUpperCase(), action.check(), action.locationOrigin().toUpperCase(), action.timeFiled(), action.dateFiled().toUpperCase());
        String lineA = String.format("NA\\%s\\%s\\%s\\%s", action.name().toUpperCase(), action.address().toUpperCase(), action.cityState().toUpperCase(), action.operatorNote().toUpperCase());
        String lineP = String.format("NP\\%s", preparePhoneNumber(action.phoneNumber()));
        String lineE = String.format("NE\\%s", prepareEmail(action.emailAddress()));
        String line1 = String.format("N1\\%s %s %s %s %s", action.message1().toUpperCase(), action.message2().toUpperCase(), action.message3().toUpperCase(), action.message4().toUpperCase(), action.message5().toUpperCase());
        String line2 = String.format("N2\\%s %s %s %s %s", action.message6().toUpperCase(), action.message7().toUpperCase(), action.message8().toUpperCase(), action.message9().toUpperCase(), action.message10().toUpperCase());
        String line3 = String.format("N3\\%s %s %s %s %s", action.message11().toUpperCase(), action.message12().toUpperCase(), action.message13().toUpperCase(), action.message14().toUpperCase(), action.message15().toUpperCase());
        String line4 = String.format("N4\\%s %s %s %s %s", action.message16().toUpperCase(), action.message17().toUpperCase(), action.message18().toUpperCase(), action.message19().toUpperCase(), action.message20().toUpperCase());
        String line5 = String.format("N5\\%s %s %s %s %s", action.message21().toUpperCase(), action.message22().toUpperCase(), action.message23().toUpperCase(), action.message24().toUpperCase(), action.message25().toUpperCase());
        String lineSig = String.format("NS\\%s", action.signature().toUpperCase());

        transceiverCommunicationAccessor.sendMessage(loggedInUser, action.callsignOrigin(), NTS_GATEWAY_CALLSIGN, lineQTC);
        transceiverCommunicationAccessor.sendMessage(loggedInUser, action.callsignOrigin(), NTS_GATEWAY_CALLSIGN, lineNumber);
        transceiverCommunicationAccessor.sendMessage(loggedInUser, action.callsignOrigin(), NTS_GATEWAY_CALLSIGN, lineA);
        if (lineP.length() > 4) {
            transceiverCommunicationAccessor.sendMessage(loggedInUser, action.callsignOrigin(), NTS_GATEWAY_CALLSIGN, lineP);
        }
        if (lineE.length() > 4) {
            transceiverCommunicationAccessor.sendMessage(loggedInUser, action.callsignOrigin(), NTS_GATEWAY_CALLSIGN, lineE);
        }

        transceiverCommunicationAccessor.sendMessage(loggedInUser, action.callsignOrigin(), NTS_GATEWAY_CALLSIGN, line1);

        int checkVal = Integer.parseInt(action.check());
        if (checkVal > 5) {
            transceiverCommunicationAccessor.sendMessage(loggedInUser, action.callsignOrigin(), NTS_GATEWAY_CALLSIGN, line2);
        }
        if (checkVal > 10) {
            transceiverCommunicationAccessor.sendMessage(loggedInUser, action.callsignOrigin(), NTS_GATEWAY_CALLSIGN, line3);
        }
        if (checkVal > 15) {
            transceiverCommunicationAccessor.sendMessage(loggedInUser, action.callsignOrigin(), NTS_GATEWAY_CALLSIGN, line4);
        }
        if (checkVal > 20) {
            transceiverCommunicationAccessor.sendMessage(loggedInUser, action.callsignOrigin(), NTS_GATEWAY_CALLSIGN, line5);
        }
        transceiverCommunicationAccessor.sendMessage(loggedInUser, action.callsignOrigin(), NTS_GATEWAY_CALLSIGN, lineSig);
        /*
        QTC 1
         * N#\number\precedence\handling\originator\check\place\time\date
            NA\address_line1\address_line2\address_line3\address_line4
            NP\phone number
            N1\line 1 of NTS message text
            N2\line 2 of NTS message text
            N3\line 3 of NTS message text
            N4\line 4 of NTS message text
            N5\line 5 of NTS message text
            N6\line 6 of NTS message text
            NS\Signature block
            NR\Received from\date_time\sent_to\date_time   (not required)
         */

        /*
         * String number, String precedence, String hk, String callsignOrigin, String locationOrigin, String timeFiled, String dateFiled,
                String name, String address, String cityState, String phoneNumber, String emailAddress, String signature, String operatorNote,
                String message1, String message2, String message3, String message4, String message5, String message6, String message7, String message8, String message9, String message10,
                String message11, String message12, String message13, String message14, String message15, String message16, String message17, String message18, String message19, String message20,
                String message21, String message22, String message23, String message24, String message25, String check
         */
        return response;
    }

    private boolean verifyCount(NTSSendRadiogramRequest action) {
        int checkVal = Integer.parseInt(action.check());
        boolean ret = false;
        int count = 0;

        count += canCount(action.message1());
        count += canCount(action.message2());
        count += canCount(action.message3());
        count += canCount(action.message4());
        count += canCount(action.message5());
        count += canCount(action.message6());
        count += canCount(action.message7());
        count += canCount(action.message8());
        count += canCount(action.message9());
        count += canCount(action.message10());
        count += canCount(action.message11());
        count += canCount(action.message12());
        count += canCount(action.message13());
        count += canCount(action.message14());
        count += canCount(action.message15());
        count += canCount(action.message16());
        count += canCount(action.message17());
        count += canCount(action.message18());
        count += canCount(action.message19());
        count += canCount(action.message20());
        count += canCount(action.message21());
        count += canCount(action.message22());
        count += canCount(action.message23());
        count += canCount(action.message24());
        count += canCount(action.message25());

        if (count == checkVal) {
            ret = true;
        }
        return ret;
    }
    private int canCount(String message) {
        if ((message != null) && (!message.isEmpty())) {
            return 1;
        }
        return 0;
    }

    private String preparePhoneNumber(String phoneNumber) {
        String ret = phoneNumber;
        if (ret == null) {
            return ret;
        }
        if (ret.contains("(")) {
            ret = ret.replace("(", "");
        }
        if (ret.contains(")")) {
            ret = ret.replace(")", "");
        }
        if (ret.contains("-")) {
            ret = ret.replace("-", "");
        }
        return ret.toUpperCase();
    }

    private String prepareEmail(String emailAddress) {
        String ret = emailAddress;
        if (ret == null) {
            return ret;
        }
        if (ret.contains("@")) {
            String [] pieces = ret.split("@");
            ret = pieces[0] + " ATSIGN " + pieces[1];
        }

        if (ret.contains(".")) {
            String [] pieces = ret.split("\\.");
            ret = pieces[0] + " DOT " + pieces[1];
        }

        return ret.toUpperCase();
    }
}
