package netcentral.server.record;

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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("config_settings")
public record NetCentralServerConfigRecord(
                @Id @NonNull @NotBlank Integer config_set,
                @Nullable Integer object_beacon_minutes,

                @Nullable Integer object_cleanup_minutes,
                @Nullable Integer report_cleanup_minutes,
                @Nullable Integer scheduled_net_check_minutes,
                @Nullable Integer net_participant_reminder_minutes,
                @Nullable Integer net_report_minutes,
                @Nullable @Size(max = 20) String bulletin_announce,

                @Nullable Integer map_default_latitude_min,
                @Nullable Integer map_default_longitude_min,
                @Nullable Integer map_default_latitude_max,
                @Nullable Integer map_default_longitude_max,

                @Nullable Boolean federated,
                @Nullable Boolean federated_push_udp,
                @Nullable Boolean federated_push_message,
                @Nullable Boolean federated_interrogate,
                @Nullable Boolean log_raw_packets
                ) {  
}
