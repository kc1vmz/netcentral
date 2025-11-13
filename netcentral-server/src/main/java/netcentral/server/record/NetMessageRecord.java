package netcentral.server.record;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("net_message")
public record NetMessageRecord(
                @Id @NonNull @NotBlank @Size(max = 36) String net_message_id,
                @NonNull @NotBlank @Size(max = 36) String completed_net_id,
                @NonNull @NotBlank @Size(max = 30) String callsign_from, 
                @NonNull @NotBlank @Size(max = 63) String message,
                @NonNull @NotBlank ZonedDateTime received_time,
                @NonNull @NotBlank @Size(max = 20) String recipient) {  
}
