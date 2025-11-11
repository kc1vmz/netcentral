package netcentral.server.record;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("callsign_ack_counter")
public record CallsignAckCounterRecord(
                @Id @NonNull @NotBlank @Size(max = 20) String callsign_ack_counter_id,
                @NonNull @NotBlank @Size(max = 10) String callsign_from,
                @NonNull @NotBlank @Size(max = 10) String callsign_to,
                @NonNull @NotBlank long counter
                ) {  
}
