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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.TrackedStationType;
import netcentral.server.enums.TrackedStationTypeRuleTarget;
import netcentral.server.enums.TrackedStationTypeRuleType;
import netcentral.server.object.Net;
import netcentral.server.object.TrackedStationTypeRule;
import netcentral.server.object.User;
import netcentral.server.record.NetRecord;
import netcentral.server.record.TrackedStationTypeRuleRecord;
import netcentral.server.repository.TrackedStationTypeRuleRepository;
import netcentral.server.utils.LatLonConverter;

@Singleton
public class TrackedStationTypeRuleAccessor {
    private static final Logger logger = LogManager.getLogger(TrackedStationTypeRuleAccessor.class);

    @Inject
    private TrackedStationTypeRuleRepository trackedStationTypeRuleRepository;

    public List<TrackedStationTypeRule> getAll(User loggedInUser) {
        List<TrackedStationTypeRuleRecord> recs = trackedStationTypeRuleRepository.findAll();
        List<TrackedStationTypeRule> ret = new ArrayList<>();

        if ((recs != null) && (!recs.isEmpty())) {
            for (TrackedStationTypeRuleRecord rec : recs) {
                ret.add(new TrackedStationTypeRule(rec.tracked_station_type_rule_id(), TrackedStationTypeRuleTarget.values()[rec.rule_target()], 
                                TrackedStationTypeRuleType.values()[rec.rule_type()], 
                                rec.value(), TrackedStationType.values()[rec.tracked_station_type()]));
            }
        }

        return ret;
    }

    public TrackedStationTypeRule get(User loggedInUser, String id) {
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Tracked station type rule id not provided");
        }
        Optional<TrackedStationTypeRuleRecord> recOpt = trackedStationTypeRuleRepository.findById(id);
        if ((recOpt == null) || !recOpt.isPresent()) {
            logger.debug("Tracked station type rule not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Tracked station type rule not found");
        }
        TrackedStationTypeRuleRecord rec = recOpt.get();
        return new TrackedStationTypeRule(rec.tracked_station_type_rule_id(), TrackedStationTypeRuleTarget.values()[rec.rule_target()], 
                                TrackedStationTypeRuleType.values()[rec.rule_type()], 
                                rec.value(), TrackedStationType.values()[rec.tracked_station_type()]);
    }


    public TrackedStationTypeRule create(User loggedInUser, TrackedStationTypeRule rule) {
        String rule_id = UUID.randomUUID().toString();
        TrackedStationTypeRuleRecord src = new TrackedStationTypeRuleRecord(rule_id, rule.getRuleTarget().ordinal(),
                                                    rule.getRuleType().ordinal(), rule.getTrackedStationType().ordinal(),
                                                    rule.getValue());
        TrackedStationTypeRuleRecord rec = trackedStationTypeRuleRepository.save(src);
        if (rec != null) {
            return new TrackedStationTypeRule(rec.tracked_station_type_rule_id(), TrackedStationTypeRuleTarget.values()[rec.rule_target()], 
                                TrackedStationTypeRuleType.values()[rec.rule_type()], 
                                rec.value(), TrackedStationType.values()[rec.tracked_station_type()]);
        }

        logger.debug("Tracked station type rule not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Tracked station type rule not created");
    }


    public TrackedStationTypeRule delete(User loggedInUser, String id) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Tracked station type rule id not provided");
        }

        Optional<TrackedStationTypeRuleRecord> recOpt = trackedStationTypeRuleRepository.findById(id);
        if ((recOpt == null) || !recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Tracked station type rule not found");
        }
    
        trackedStationTypeRuleRepository.delete(recOpt.get());
        
        return null;
    }

    public TrackedStationTypeRule update(User loggedInUser, String id, TrackedStationTypeRule obj) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Id not provided");
        }
        if (obj == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "TrackedStationTypeRule not provided");
        }

        Optional<TrackedStationTypeRuleRecord> recOpt = trackedStationTypeRuleRepository.findById(id);
        if (!recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "TrackedStationTypeRule not found");
        }
        TrackedStationTypeRuleRecord rec = recOpt.get();
        TrackedStationTypeRuleRecord updatedRec = new TrackedStationTypeRuleRecord(rec.tracked_station_type_rule_id(), obj.getRuleTarget().ordinal(), 
                                                    obj.getRuleType().ordinal(), obj.getTrackedStationType().ordinal(),
                                                    obj.getValue());
        trackedStationTypeRuleRepository.update(updatedRec);
        obj = get(loggedInUser, id);

        return obj;

    }
}
