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
import netcentral.server.object.Activity;
import netcentral.server.object.User;
import netcentral.server.record.ActivityRecord;
import netcentral.server.repository.ActivityRepository;

@Singleton
public class ActivityAccessor {
    private static final Logger logger = LogManager.getLogger(ActivityAccessor.class);

    @Inject
    private ActivityRepository activityRepository;

    public List<Activity> getAll(User loggedInUser) {
        List<ActivityRecord> recs = activityRepository.findAll();
        List<Activity> ret = new ArrayList<>();

        if ((recs != null) && (!recs.isEmpty())) {
            for (ActivityRecord rec : recs) {
                User user = null;
                if (rec.user_id() != null) {
                    user = new User();
                    user.setId(rec.user_id());
                }
                ret.add(new Activity(rec.activity_id(), rec.time(), rec.text(), user));
            }
        }

        return ret;
    }

    public Activity get(User loggedInUser, String id) {
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Activity id not provided");
        }
        Optional<ActivityRecord> recOpt = activityRepository.findById(id);
        if ((recOpt == null) || !recOpt.isPresent()) {
            logger.debug("Activity not found");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Activity not found");
        }
        ActivityRecord rec = recOpt.get();
        User user = null;
        if (rec.user_id() != null) {
            user = new User();
            user.setId(rec.user_id());
        }
        return new Activity(rec.activity_id(), rec.time(), rec.text(), user);
    }


    public Activity create(User user, String text) {
        if (text == null) {
            logger.debug("Text is null");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Text not provided");
        }

        String activity_id = UUID.randomUUID().toString();
        ActivityRecord src = new ActivityRecord(activity_id, ZonedDateTime.now(), text, (user == null) ? null : user.getId());
        ActivityRecord rec = activityRepository.save(src);
        if (rec != null) {
            return new Activity(rec.activity_id(), rec.time(), rec.text(), user);
        }

        logger.debug("Activity not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Activity not created");
    }


    public Activity delete(User loggedInUser, String id) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Activity id not provided");
        }

        Optional<ActivityRecord> recOpt = activityRepository.findById(id);
        if ((recOpt == null) || !recOpt.isPresent()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Activity not found");
        }
    
        activityRepository.delete(recOpt.get());
        
        return null;
    }
}
