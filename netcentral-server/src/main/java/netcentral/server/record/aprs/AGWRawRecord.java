package netcentral.server.record.aprs;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("agw_raw")
public record AGWRawRecord(@Id @NonNull @NotBlank @Size(max = 36) String agw_raw_id, 
                                    @NonNull @NotBlank @Size(max = 36) String source,
                                    @NonNull @NotBlank ZonedDateTime heard_time,
                                    @NonNull @NotBlank @Size(max = 128) String header,
                                    @Nullable @Size(max = 512) String data) {  
}

