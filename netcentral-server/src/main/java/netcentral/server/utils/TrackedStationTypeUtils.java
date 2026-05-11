package netcentral.server.utils;

import java.util.ArrayList;
import java.util.List;

import netcentral.server.enums.TrackedStationType;

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

public class TrackedStationTypeUtils {

    public static List<TrackedStationType> convertTrackedStationTypeStringToList(String value) {
        List<TrackedStationType> ret = new ArrayList<>();

        if ((value == null) || (value.isBlank())) {
            ret.add(TrackedStationType.UNKNOWN);
        } else {
            int len = value.length();
            for (int i = 0 ; i < len; i++) {
                int ord = value.charAt(i) - 'A';
                TrackedStationType tst = TrackedStationType.values()[ord];
                ret.add(tst);
            }
        }
        if (ret.size() > 1) {
            ret.remove(TrackedStationType.UNKNOWN);
        }
        return ret;
    }

    public static String convertTrackedStationTypesToString(List<TrackedStationType> values) {
        String ret = "";
        if ((values == null) || (values.isEmpty())) {
            return convertTrackedStationTypeToString(TrackedStationType.UNKNOWN);
        }
        boolean skipUnknown = false;
        if (values.size() > 1) {
            skipUnknown = true;
        }
        for (TrackedStationType value : values) {
            if (value.equals(TrackedStationType.UNKNOWN) && skipUnknown) {
                continue;
            }
            ret += convertTrackedStationTypeToString(value);
        }
        return ret;
    }

    private static String convertTrackedStationTypeToString(TrackedStationType value) {
        char base = 'A';
        base += value.ordinal();
        return ""+base;
    }

    public static String convertTrackedStationTypeEnumStringToString(String value) {
        char base = 'A';
        base += TrackedStationType.valueOf(value).ordinal();
        return ""+base;
    }

    public static List<TrackedStationType> addTrackedStationType(List<TrackedStationType> types, TrackedStationType value) {
        if ((types.size() > 0) && (value.equals(TrackedStationType.UNKNOWN))) {
            // has values so do not make it unknown too
            return types;
        }
        types.add(value);
        if (types.size() > 1) {
            // more than one type - remove UNKNOWN
            types.remove(TrackedStationType.UNKNOWN);
        }
        return types;
    }

    public static List<TrackedStationType> removeTrackedStationType(List<TrackedStationType> types, TrackedStationType value) {
        if (types == null) {
            types = new ArrayList<>();
        }

        types.remove(value);
        if (types.isEmpty()) {
            // removed last - make it unknown
            types.add(TrackedStationType.UNKNOWN);
        }
        return types;
    }

    public static boolean isType(List<TrackedStationType> types, TrackedStationType value) {
        if ((types == null) || (types.isEmpty())) {
            return false;
        }
        return types.contains(value);
    }

    public static String getPrettyTypes(List<TrackedStationType> types) {
        String ret = "";
        if ((types == null) || (types.isEmpty())) {
            return ret;
        }

        boolean bAddComma = false;
        for (TrackedStationType type : types) {
            if (bAddComma) {
                ret += ", ";
            }
            ret += type.name();
            bAddComma = true;
        }
        
        return ret;
    }

}
