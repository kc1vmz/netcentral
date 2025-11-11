package netcentral.server.record;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("callsign")
public record CallsignRecord(@Id @NonNull @NotBlank @Size(max = 20) String callsign,
                                @Nullable @Size(max = 75) String name,
                                @Nullable @Size(max = 30) String country,
                                @Nullable @Size(max = 50) String state,
                                @Nullable @Size(max = 30) String license
) {  
}

