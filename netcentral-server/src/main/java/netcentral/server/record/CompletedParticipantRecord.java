package netcentral.server.record;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("completed_participant")
public record CompletedParticipantRecord(
                @Id @NonNull @NotBlank @Size(max = 36) String completed_participant_id,
                @NonNull @NotBlank @Size(max = 36) String completed_net_id,
                @NonNull @NotBlank @Size(max = 20) String callsign,
                @NonNull @NotBlank ZonedDateTime start_time,
                @NonNull @NotBlank ZonedDateTime end_time,
                @NonNull Integer electrical_power_type,
                @NonNull Integer radio_style,
                @NonNull Integer transmit_power,
                @Nullable @Size(max = 20) String tactical_callsign) {  
}
