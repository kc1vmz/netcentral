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
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("aprs_object_resource_kv")
public record ResourceObjectKVRecord(@Id @NonNull @NotBlank @Size(max = 36) String aprs_object_resource_kv_id,
                                @NonNull @NotBlank @Size(max = 36) String aprs_object_id,
                                @NonNull @NotBlank @Size(max = 20) String key,
                                @NonNull @NotBlank @Size(max = 40) String value,
                                @NonNull @NotBlank Boolean broadcast
) {  
}
