package netcentral.server.record.aprs;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("aprs_message")
public record APRSMessageRecord(@Id @NonNull @NotBlank @Size(max = 36) String aprs_message_id, 
                                    @Nullable @Size(max = 36) String completed_net_id,
                                    @NonNull @NotBlank @Size(max = 36) String source,
                                    @NonNull @NotBlank ZonedDateTime heard_time,
                                    @NonNull @NotBlank @Size(max = 20) String callsign_from,
                                    @Nullable @Size(max = 20) String callsign_to,
                                    @Nullable @Size(max = 200) String message,
                                    @Nullable @Size(max = 10) String message_number,
                                    @Nullable Boolean must_ack,
                                    @Nullable Boolean bulletin,
                                    @Nullable Boolean announcement,
                                    @Nullable Boolean group_bulletin,
                                    @Nullable Boolean nws_bulletin,
                                    @Nullable @Size(max = 20) String nws_level,
                                    @Nullable Boolean query,
                                    @Nullable @Size(max = 20) String query_type
                                    ) {  
}


