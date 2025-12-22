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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.Anomaly;
import netcentral.server.object.User;
import netcentral.server.record.AnomalyRecord;
import netcentral.server.repository.AnomalyRepository;

@Singleton
public class AnomalyAccessor {
    private static final Logger logger = LogManager.getLogger(AnomalyAccessor.class);

    @Inject
    private AnomalyRepository anomalyRepository;

    public List<Anomaly> getAll(User loggedInUser, String root) {
        logger.debug("getAll() called");
        List<AnomalyRecord> recs = anomalyRepository.findAll();
        List<Anomaly> ret = new ArrayList<>();

        if ((recs != null) && (!recs.isEmpty())) {
            for (AnomalyRecord rec : recs) {
                if ((root != null) && (!rec.callsign().startsWith(root))) {
                    // only take those with the optional root
                    continue;
                }
                ret.add(new Anomaly(rec.anomaly_id(), rec.callsign(), rec.packet_text(), rec.description(), rec.time()));
            }
        }

        return ret;
    }

    public Anomaly get(User loggedInUser, String id) {
        logger.debug(String.format("getId(%s) called", id));
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Anomaly id not provided");
        }
        Optional<AnomalyRecord> recOpt = anomalyRepository.findById(id);
        if ((recOpt == null) || !recOpt.isPresent()) {
            logger.debug("Anomaly not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Anomaly not found");
        }
        AnomalyRecord rec = recOpt.get();
        return new Anomaly(rec.anomaly_id(), rec.callsign(), rec.packet_text(), rec.description(), rec.time());
    }

    public Anomaly create(User loggedInUser, String callsign, String packetText, String description) {
        return new Anomaly(UUID.randomUUID().toString(), callsign, packetText, description, ZonedDateTime.now());
    }

    public Anomaly create(User loggedInUser, Anomaly obj) {
        if (obj == null) {
            logger.debug("Anomaly is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Anomaly not provided");
        }
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        AnomalyRecord src = new AnomalyRecord(obj.getId(), obj.getCallsign(), obj.getPacketText(), obj.getDescription(), obj.getTime());
        AnomalyRecord rec = anomalyRepository.save(src);
        if (rec != null) {
            return obj;
        }
        logger.debug("Anomaly not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Anomaly not created");
    }
}
