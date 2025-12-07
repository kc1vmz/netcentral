package netcentral.server.record;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("completed_net")
public record CompletedNetRecord(
                @Id @NonNull @NotBlank @Size(max = 36) String completed_net_id,
                @NonNull @NotBlank @Size(max = 20) String callsign,
                @Nullable @Size(max = 20) String vfreq,
                @NonNull @NotBlank @Size(max = 30) String name,
                @Nullable @Size(max = 50) String description,
                @NonNull @NotBlank ZonedDateTime start_time,
                @NonNull @NotBlank ZonedDateTime end_time,
                @NonNull @NotBlank @Size(max = 100) String creator_name,
                @Nullable @Size(max = 50) String checkin_message,
                @NonNull @NotBlank Boolean open, 
                @NonNull @NotBlank Boolean participant_invite_allowed) {  
}
