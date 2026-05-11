package netcentral.server.accessor;

import java.util.ArrayList;
import java.util.List;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import netcentral.server.enums.TrackedStationType;
import netcentral.server.enums.TrackedStationTypeRuleTarget;
import netcentral.server.enums.TrackedStationTypeRuleType;
import netcentral.server.object.TrackedStation;
import netcentral.server.object.TrackedStationTypeRule;
import netcentral.server.utils.TrackedStationTypeUtils;

@Singleton
public class TrackedStationTypeRulesProcessor {
    @Inject
    private TrackedStationTypeRuleAccessor trackedStationTypeRuleAccessor;

    private static final Logger logger = LogManager.getLogger(TrackedStationTypeRulesProcessor.class);
    private List<TrackedStationTypeRule> rules = null;

    private synchronized void initialize() {
        if (rules != null) {
            return;
        }
        rules = new ArrayList<>();
        logger.info("Initializing TrackedStationTypeRulesProcessor");

        rules.addAll(trackedStationTypeRuleAccessor.getAll(null));
    }

    public List<TrackedStationType> determineTypesFromStatus(String value) {
        return determineTypes(value, TrackedStationTypeRuleTarget.STATUS);
    }

    public List<TrackedStationType> determineTypesFromComment(String value) {
        return determineTypes(value, TrackedStationTypeRuleTarget.COMMENT);
    }

    private List<TrackedStationType> determineTypes(String value, TrackedStationTypeRuleTarget target) {
        List<TrackedStationType> ret = new ArrayList<>();

        if ((value == null) || (value.isEmpty())) {
            ret.add(TrackedStationType.UNKNOWN);
            return ret;
        }

        initialize();
        String valueUpper = value.toUpperCase();

        for (TrackedStationTypeRule rule : rules) {
            if (!rule.getRuleTarget().equals(target)) {
                continue;
            }

            if (rule.getRuleType().equals(TrackedStationTypeRuleType.CONTAINS)) {
                if (valueUpper.contains(rule.getValue())) {
                    ret = TrackedStationTypeUtils.addTrackedStationType(ret, rule.getTrackedStationType());
                }
            }
        }

        return ret;
    }

    public TrackedStation processObjectComment(String comment, TrackedStationType type) {
        TrackedStation ret = new TrackedStation();
        ret.setTypes(TrackedStationTypeUtils.addTrackedStationType(ret.getTypes(), type));
        if (comment == null) {
            return ret;
        }

        int index = 0; 
        String remain = "";
        while (index < comment.length()) {
            remain = comment.substring(index);
            if (remain.startsWith("/A")) {
                index += "/A=xxxxxx".length();
            } else if (remain.startsWith(" ")) {
                index++;
            } else if (remain.startsWith("RNG")) {
                index += "RNGxxxx".length();
            } else if (remain.startsWith("PHG")) {
                index += "PHGxxxx".length();
            } else {
                break;
            }
        }

        String info = remain;
        String description = null;
        // remain has more info
        if (remain.contains(",")) {
            // 70cm Voice (D-Star) 433.60000MHz +0.0000MHz, APRS for ircDDBGateway
            String [] parts = remain.split(",");
            if (parts.length == 2) {
                description = parts[1].substring(1); // skip space
                info = parts[0];
            }
        }

        ret.setTypes(determineTypesFromComment(comment));

        if (description != null) {
            ret.setDescription(description);
        } else {
            ret.setDescription(info);
        }

        return ret;
    }

}
