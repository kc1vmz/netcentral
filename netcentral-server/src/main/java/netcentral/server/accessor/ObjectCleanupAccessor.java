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

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.auth.SessionAccessor;
import netcentral.server.config.NetConfigServerConfig;
import netcentral.server.enums.UserRole;
import netcentral.server.object.User;


@Singleton
public class ObjectCleanupAccessor {
    private static final Logger logger = LogManager.getLogger(ObjectCleanupAccessor.class);

    @Inject
    private APRSObjectAccessor aprsObjectAccessor;
    @Inject
    private NetConfigServerConfig netConfigServerConfig;
    @Inject
    private SessionAccessor sessionAccessor;
    @Inject
    private TrackedStationAccessor trackedStationAccessor;
    @Inject
    private CallsignAccessor callsignAccessor;
    @Inject
    private CallsignAceAccessor callsignAceAccessor;
    @Inject
    private ChangePublisherAccessor changePublisherAccessor;
    @Inject
    private IgnoreStationAccessor ignoreStationAccessor;
    @Inject
    private ReportAccessor reportAccessor;
 

    private boolean stop = false;

    public void cleanupObjectsByTime(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if ((!loggedInUser.getRole().equals(UserRole.SYSADMIN)) && (!loggedInUser.getRole().equals(UserRole.SYSTEM))) {
            // no privs
            logger.error("No privileges to allow delete all");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "No privileges to allow delete all");
        }

        try {
            ZonedDateTime before = ZonedDateTime.now().minusMinutes(netConfigServerConfig.getObjectCleanupMinutes());
            User systemUser = sessionAccessor.getSystemUser();
            // delete weather report
            aprsObjectAccessor.deleteWeatherReports(systemUser, before);
            // delete position reports
            aprsObjectAccessor.deletePositionReports(systemUser, before);
            // delete status reports
            aprsObjectAccessor.deleteStatusReports(systemUser, before);
            
            // delete objects and items
            aprsObjectAccessor.deleteObjects(systemUser, before);
            // delete tracked stations
            trackedStationAccessor.deleteTrackedStations(systemUser, before);

            // delete messages
            aprsObjectAccessor.deleteMessages(systemUser, before);

            changePublisherAccessor.publishAllUpdate();
        } catch (Exception e) {
            logger.error("Exception caught cleaning up objects", e);
        }
    }

    public void cleanupAllAPRSData(User loggedInUser) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if ((!loggedInUser.getRole().equals(UserRole.SYSADMIN)) && (!loggedInUser.getRole().equals(UserRole.SYSTEM))) {
            // no privs
            logger.error("No privileges to allow delete all");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "No privileges to allow delete all");
        }

        try {
            // delete weather report
            aprsObjectAccessor.deleteAllData(loggedInUser);
            // delete position reports
            trackedStationAccessor.deleteAllData(loggedInUser);
            // delete position reports
            callsignAccessor.deleteAllData(loggedInUser);
            // delete all ignored stations
            ignoreStationAccessor.deleteAllData(loggedInUser);
            // delete position reports
            callsignAceAccessor.deleteAllData(loggedInUser);
            // delete all reports
            reportAccessor.deleteAllData(loggedInUser);

            changePublisherAccessor.publishAllUpdate();
        } catch (Exception e) {
            logger.error("Exception caught cleaning up objects", e);
        }
    }

    public void shutdown() {
        stop = true;
    }

    public boolean stayRunning() {
        return !stop;
    }
}
