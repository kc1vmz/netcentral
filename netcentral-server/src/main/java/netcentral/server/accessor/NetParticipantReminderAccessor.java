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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.Net;
import netcentral.server.object.Participant;
import netcentral.server.object.User;

@Singleton
public class NetParticipantReminderAccessor {
    private static final Logger logger = LogManager.getLogger(NetParticipantReminderAccessor.class);
    private boolean stop = false;

    @Inject
    private TransceiverCommunicationAccessor transceiverMessageAccessor;
    @Inject
    private ParticipantAccessor participantAccessor;
    @Inject
    private NetParticipantAccessor netParticipantAccessor;


    public void sendParticipantReminders() {
        if (!stayRunning()) {
            return;
        }
        User user = new User();

        List<Participant> participants = participantAccessor.getAll(user, null);
        if (participants.isEmpty()) {
            return;
        }
        for (Participant participant : participants) {
            // from the list of participants, get the nets they are in
            try {
                List<Net> nets  = netParticipantAccessor.getAllNets(user, participant);
                if ((nets != null) && (!nets.isEmpty())) {
                    for (Net net : nets) {
                        // remind them about each net
                        logger.debug(String.format("Reminding participant %s in net %s", participant.getCallsign(), net.getCallsign()));
                        transceiverMessageAccessor.sendMessage(user, net.getCallsign(), participant.getCallsign(), String.format("Reminder that you are still checked into net %s", net.getCallsign()));
                    }
                }
            } catch (Exception e) {
                logger.error("Exception caught reminding net participant", e);
            }
        }
    }

    public boolean stayRunning() {
        return !stop;
    }
    public void shutdown() {
        stop = true;
    }
}
