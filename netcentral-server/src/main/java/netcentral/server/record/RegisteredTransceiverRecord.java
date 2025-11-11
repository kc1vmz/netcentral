package netcentral.server.record;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("registered_transceiver")
public record RegisteredTransceiverRecord(
                @Id @NonNull @NotBlank @Size(max = 36) String registered_transceiver_id,
                @NonNull @NotBlank @Size(max = 30) String name,
                @Nullable @Size(max = 50) String description,
                @NonNull @NotBlank @Size(max = 200) String fqd_name,
                @NonNull @NotBlank @Size(max = 20) String type,
                @NonNull @NotBlank Integer port,
                @NonNull @NotBlank Boolean enabled_receive,
                @NonNull @NotBlank Boolean enabled_transmit
                ) {  
}

