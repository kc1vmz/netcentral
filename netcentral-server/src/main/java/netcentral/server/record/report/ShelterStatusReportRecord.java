package netcentral.server.record.report;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("report_shelter_status")
public record ShelterStatusReportRecord(@Id @NonNull @NotBlank @Size(max = 36) String report_shelter_status_id,
                                @NonNull @NotBlank @Size(max = 20) String callsign,
                                @NonNull @NotBlank Integer state,
                                @NonNull @NotBlank Integer status,
                                @NonNull @NotBlank @Size(max = 40) String message,
                                @NonNull @NotBlank ZonedDateTime reported_date
) {  
}
