package netcentral.server.accessor;

import java.time.ZonedDateTime;

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
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.object.ResourceObjectKeyValuePair;
import netcentral.server.object.User;
import netcentral.server.record.ResourceObjectKVHistoryRecord;
import netcentral.server.record.ResourceObjectKVRecord;
import netcentral.server.repository.ResourceObjectKVHistoryRepository;
import netcentral.server.repository.ResourceObjectKVRepository;

@Singleton
public class ResourceObjectKVAccessor {
    private static final Logger logger = LogManager.getLogger(ResourceObjectKVAccessor.class);

    @Inject
    private ResourceObjectKVRepository resourceObjectKVRepository;
    @Inject
    private ResourceObjectKVHistoryRepository resourceObjectKVHistoryRepository;

    public List<ResourceObjectKeyValuePair> get(User loggedInUser, String id) {
        if (id == null) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Object id not provided");
        }
        List<ResourceObjectKVRecord> recs = new ArrayList<>();
        try {
            recs = resourceObjectKVRepository.findByaprs_object_id(id);
        } catch (Exception e) {
        }
        if (recs == null) {
            recs = new ArrayList<>();
        }

        List<ResourceObjectKeyValuePair> ret = new ArrayList<>();
        for (ResourceObjectKVRecord rec : recs) {
            ResourceObjectKeyValuePair item = new ResourceObjectKeyValuePair(rec.key(), rec.value(), rec.broadcast());
            ret.add(item);
        }
        return ret;
    }

    public String get(User loggedInUser, String id, String key) {
        String ret = null;
        if ((id == null) || (key == null)) {
            logger.debug("null id");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Null data provided");
        }
        List<ResourceObjectKVRecord> recs = new ArrayList<>();
        try {
            recs = resourceObjectKVRepository.findByaprs_object_id(id);
        } catch (Exception e) {
        }
        if (recs == null) {
            recs = new ArrayList<>();
        }

        for (ResourceObjectKVRecord rec : recs) {
            if (rec.key().equalsIgnoreCase(key)) {
                ret = rec.value();
                break;
            }
        }
        return ret;
    }

    public ResourceObjectKeyValuePair create(User loggedInUser, String id, String key, String value, Boolean broadcast) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        if ((id == null) || (key == null) || (value == null) || (broadcast == null)) {
            logger.debug("Null value provided");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Null value provided");
        }

        String recordid = UUID.randomUUID().toString();
        ResourceObjectKVRecord src = new ResourceObjectKVRecord(recordid, id, key, value, broadcast);
        ResourceObjectKVRecord rec = resourceObjectKVRepository.save(src);

        ResourceObjectKVHistoryRecord srcHistory = new ResourceObjectKVHistoryRecord(recordid, id, key, value, broadcast, ZonedDateTime.now());
        resourceObjectKVHistoryRepository.save(srcHistory);

        if (rec != null) {
            return new ResourceObjectKeyValuePair(key, value, broadcast);
        }

        logger.debug("Key value pair not created");
        throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Key value pair not created");
    }


    public ResourceObjectKeyValuePair delete(User loggedInUser, String id, String key) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if (id == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Object id not provided");
        }

        try {
            List<ResourceObjectKVRecord> recs = resourceObjectKVRepository.findByaprs_object_id(id);
            if (recs == null) {
                recs = new ArrayList<>();
            }

            for (ResourceObjectKVRecord rec : recs) {
                if (rec.key().equalsIgnoreCase(key)) {
                    // it is here
                    resourceObjectKVRepository.delete(rec);
                    break;
                }
            }
        } catch (Exception e) {
        }
        
        return null;
    }

    public ResourceObjectKeyValuePair update(User loggedInUser, String id, String key, String value, Boolean broadcast) {
        if (loggedInUser == null) {
            logger.debug("User not logged in");
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        if ((id == null) || (key == null) || (value == null)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Null value provided");
        }

        try {
            List<ResourceObjectKVRecord> recs = resourceObjectKVRepository.findByaprs_object_id(id);
            if (recs == null) {
                recs = new ArrayList<>();
            }
            boolean bcast = false;
            boolean found = false;
            for (ResourceObjectKVRecord rec : recs) {
                if (rec.key().equalsIgnoreCase(key)) {
                    bcast = rec.broadcast();
                    if (broadcast != null) {
                        bcast = broadcast;
                    }
                    // it is here
                    ResourceObjectKVRecord src = new ResourceObjectKVRecord(rec.aprs_object_resource_kv_id(), id, rec.key(), value, bcast);
                    resourceObjectKVRepository.update(src);

                    String recordid = UUID.randomUUID().toString();
                    ResourceObjectKVHistoryRecord srcHistory = new ResourceObjectKVHistoryRecord(recordid, id, key, value, bcast, ZonedDateTime.now());
                    resourceObjectKVHistoryRepository.save(srcHistory);
                    found = true;
                    break;
                }
            }
            if (!found) {
                create(loggedInUser, id, key, value, false);
            }
            return new ResourceObjectKeyValuePair(key, value, (broadcast == null) ? false : broadcast);
        } catch (Exception e) {
        }

        return null;
    }

}
