package netcentral.server.record.aprs;

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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("aprs_message")
public record APRSMessageRecord(@Id @NonNull @NotBlank @Size(max = 36) String aprs_message_id, 
                                    @Nullable @Size(max = 36) String completed_net_id,
                                    @NonNull @NotBlank @Size(max = 36) String source,
                                    @NonNull @NotBlank ZonedDateTime heard_time,
                                    @NonNull @NotBlank @Size(max = 20) String callsign_from,
                                    @Nullable @Size(max = 20) String callsign_to,
                                    @Nullable @Size(max = 200) String message,
                                    @Nullable @Size(max = 10) String message_number,
                                    @Nullable Boolean must_ack,
                                    @Nullable Boolean bulletin,
                                    @Nullable Boolean announcement,
                                    @Nullable Boolean group_bulletin,
                                    @Nullable Boolean nws_bulletin,
                                    @Nullable @Size(max = 20) String nws_level,
                                    @Nullable Boolean query,
                                    @Nullable @Size(max = 20) String query_type
                                    ) {  
}


