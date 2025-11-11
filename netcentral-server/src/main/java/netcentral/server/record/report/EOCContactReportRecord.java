package netcentral.server.record.report;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("report_eoc_contact")
public record EOCContactReportRecord(@Id @NonNull @NotBlank @Size(max = 36) String report_eoc_contact_id,
                                @NonNull @NotBlank @Size(max = 20) String callsign,
                                @Nullable @Size(max = 25) String director_name,
                                @Nullable @Size(max = 25) String incident_commander_name,
                                @NonNull @NotBlank ZonedDateTime reported_date
) {  
}
