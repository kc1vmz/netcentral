package netcentral.server.record.report;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("report_eoc_mobilization")
public record EOCMobilizationReportRecord(@Id @NonNull @NotBlank @Size(max = 36) String report_eoc_mobilization_id,
                                @NonNull @NotBlank @Size(max = 36) String eoc_name,
                                @NonNull @NotBlank @Size(max = 20) String callsign,
                                @NonNull @NotBlank Integer status,
                                @NonNull @NotBlank Integer level,
                                @NonNull @NotBlank ZonedDateTime reported_date
) {  
}
