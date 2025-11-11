package netcentral.server.record.aprs;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("aprs_status")
public record APRSStatusRecord(@Id @NonNull @NotBlank @Size(max = 36) String aprs_status_id, 
                                    @NonNull @NotBlank @Size(max = 36) String source,
                                    @NonNull @NotBlank ZonedDateTime heard_time,
                                    @NonNull @NotBlank @Size(max = 20) String callsign_from,
                                    @Nullable String time,
                                    @Nullable ZonedDateTime ldtime,
                                    @Nullable @Size(max = 200) String status) {  
}

