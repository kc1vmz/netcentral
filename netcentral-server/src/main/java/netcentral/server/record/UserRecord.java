package netcentral.server.record;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("user")
public record UserRecord(@Id @NonNull @NotBlank @Size(max = 36) String user_id, 
                @NonNull @NotBlank @Size(max = 100) String username,
                @NonNull @NotBlank @Size(max = 100) String password,
                @Nullable @Size(max = 20) String callsign,
                @Nullable @Size(max = 50) String firstName,
                @Nullable @Size(max = 50) String lastName,
                @NonNull @NotBlank Integer role) {  
}

