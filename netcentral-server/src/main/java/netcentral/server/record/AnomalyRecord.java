package netcentral.server.record;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("anomaly")
public record AnomalyRecord(@Id @NonNull @NotBlank @Size(max = 36) String anomaly_id,
                @Nullable @Size(max = 20) String callsign,
                @NonNull @Size(max = 100) String packet_text,
                @Nullable @Size(max = 100) String description,
                @NonNull @NotBlank ZonedDateTime time) {  
}
