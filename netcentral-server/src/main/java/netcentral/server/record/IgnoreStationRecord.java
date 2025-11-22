package netcentral.server.record;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("ignore_station")
public record IgnoreStationRecord(@Id @NonNull @NotBlank @Size(max = 20) String callsign,
                                @NonNull @NotBlank ZonedDateTime ignore_start_time,
                                @NonNull Integer type) {  
}

