package netcentral.server.record;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("net_participant")
public record NetParticipantRecord(@Id @NonNull @NotBlank @Size(max = 36) String net_participant_id,
                @NonNull @NotBlank @Size(max = 36) String net_callsign, 
                @NonNull @NotBlank @Size(max = 36) String participant_callsign,
                @NonNull @NotBlank ZonedDateTime start_time,
                @Nullable @Size(max = 36) String tactical_callsign) {  
}

