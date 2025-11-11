package netcentral.server.record;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("activity")
public record ActivityRecord(
                @Id @NonNull @NotBlank @Size(max = 36) String activity_id,
                @NonNull @NotBlank ZonedDateTime time,
                @NonNull @NotBlank @Size(max = 100) String text,
                @Nullable @Size(max = 36) String user_id) {  
}
