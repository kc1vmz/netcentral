package netcentral.server.record.aprs;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("aprs_object")
public record APRSObjectRecord(@Id @NonNull @NotBlank @Size(max = 36) String aprs_object_id, 
                                    @NonNull @NotBlank @Size(max = 36) String source,
                                    @NonNull @NotBlank @Size(max = 20) String callsign_from,
                                    @Nullable @Size(max = 20) String callsign_to,
                                    @NonNull @NotBlank ZonedDateTime heard_time,
                                    @NonNull Boolean alive,
                                    @Nullable @Size(max = 20) String lat,
                                    @Nullable @Size(max = 20) String lon,
                                    @Nullable @Size(max = 10) String time,
                                    @Nullable @Size(max = 200) String comment,
                                    @NonNull @NotBlank int type
                                    ) {  
}


