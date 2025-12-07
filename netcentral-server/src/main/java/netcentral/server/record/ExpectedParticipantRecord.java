package netcentral.server.record;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("net_expected_participant")
public record ExpectedParticipantRecord(
                @Id @NonNull @NotBlank @Size(max = 36) String net_expected_participant_id,
                @NonNull @NotBlank @Size(max = 20) String net_callsign,
                @Nullable @Size(max = 20) String completed_net_id,
                @NonNull @NotBlank @Size(max = 20) String callsign) {  
}
