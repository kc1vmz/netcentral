package netcentral.server.record;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("net")
public record NetRecord(
                @Id @NonNull @NotBlank @Size(max = 20) String callsign,
                @Nullable @Size(max = 20) String vfreq,
                @NonNull @NotBlank @Size(max = 30) String name,
                @Nullable @Size(max = 50) String description,
                @NonNull @NotBlank ZonedDateTime start_time,
                @NonNull @NotBlank String completed_net_id,
                @Nullable @Size(max = 20) String lat,
                @Nullable @Size(max = 20) String lon,
                @NonNull @NotBlank Boolean announce,
                @NonNull @NotBlank @Size(max = 100) String creator_name,
                @NonNull @NotBlank Boolean checkin_reminder
                ) {  
}
