package netcentral.server.record;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("callsign_ace")
public record CallsignAceRecord(
                @Id @NonNull @NotBlank @Size(max = 36) String callsign_ace_id,
                @NonNull @NotBlank @Size(max = 20) String callsign_target,
                @NonNull @NotBlank @Size(max = 20) String callsign_checked,
                @NonNull @NotBlank Integer callsign_checked_type,
                @NonNull @NotBlank boolean allowed,
                @Nullable Integer proximity) {  
}
