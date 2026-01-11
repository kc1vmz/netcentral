package netcentral.server.accessor;

/*
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.common.object.InternetServer;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.UserRole;
import netcentral.server.object.User;
import netcentral.server.record.InternetServerRecord;
import netcentral.server.repository.InternetServerRepository;

@Singleton
public class InternetServerAccessor {
    private static final Logger logger = LogManager.getLogger(InternetServerAccessor.class);

    @Inject
    private InternetServerRepository internetServerRepository;

    public List<InternetServer> getAll(User loggedInUser) {
        List<InternetServerRecord> recs = internetServerRepository.findAll();

        List<InternetServer> ret = new ArrayList<>();

        if (!recs.isEmpty()) {
            for (InternetServerRecord rec : recs) {
                // (String id, String ipAddress, String name, String description, String loginCallsign, String query)
                ret.add(new InternetServer(rec.internet_server_id(), rec.ip_address(), rec.name(), rec.description(), rec.login_callsign(), rec.query()));
            }
        }

        Collections.sort(ret, new Comparator<InternetServer>() {
            @Override
            public int compare(InternetServer obj1, InternetServer obj2) {
                return obj1.getIpAddress().compareTo(obj2.getIpAddress());
            }
        });

        return ret;
    }

    public InternetServer get(User loggedInUser, String id) {
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Internet server id not provided");
        }
        Optional<InternetServerRecord> recOpt = internetServerRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Internet server not found");
        }
        InternetServerRecord rec = recOpt.get();
        return new InternetServer(rec.internet_server_id(), rec.ip_address(), rec.name(), rec.description(), rec.login_callsign(), rec.query());
    }

    public InternetServer create(User loggedInUser, InternetServer obj) {
        String err = "Internet server not created";
        if (obj == null) {
            logger.debug("Internet server is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Internet server not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        try {
            List<InternetServerRecord> existingList = internetServerRepository.findByip_address(obj.getIpAddress());
            if ((existingList != null) && (!existingList.isEmpty())) {
                err = "Internet server IP address already exists";
            } else {
                String id = UUID.randomUUID().toString();
                InternetServerRecord src = new InternetServerRecord(id, obj.getLoginCallsign(), obj.getName(), obj.getDescription(), obj.getIpAddress(), obj.getQuery());
                InternetServerRecord rec = internetServerRepository.save(src);
                if (rec != null) {
                    InternetServer internetServerFinal = get(loggedInUser, id);
                    return internetServerFinal;
                }
            }
        } catch (Exception e) {
        }

        logger.debug(err);
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, err);
    }

    public InternetServer update(User loggedInUser, String id, InternetServer obj) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Internet server id not provided");
        }
        if (obj == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Internet server not provided");
        }
        if ((obj.getName() == null) || (obj.getName().isEmpty()) || (obj.getName().isBlank())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid name");
        }

        Optional<InternetServerRecord> recOpt = internetServerRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Internet server not found");
        }
        InternetServerRecord rec = recOpt.get();
        InternetServerRecord updatedRec = new InternetServerRecord(rec.internet_server_id(), obj.getLoginCallsign(), obj.getName(), obj.getDescription(), obj.getIpAddress(), obj.getQuery());
        internetServerRepository.update(updatedRec);

        InternetServer internetServerFinal = get(loggedInUser, id);
        return internetServerFinal;
    }

    public InternetServer delete(User loggedInUser, String id) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Internet server id not provided");
        }

        Optional<InternetServerRecord> recOpt = internetServerRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Internet server not found");
        }
   
        get(loggedInUser, id);
        internetServerRepository.delete(recOpt.get());
        
        return null;
    }

    public void deleteAllData(User loggedInUser) {
        if ((loggedInUser == null) || (!loggedInUser.getRole().equals(UserRole.SYSADMIN))) {
            // no privs
            logger.error("No privileges to allow delete all");
            return;
        }

        internetServerRepository.deleteAll();
    }
}
