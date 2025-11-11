package netcentral.server.record.report;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("report_shelter_worker")
public record ShelterWorkerReportRecord(@Id @NonNull @NotBlank @Size(max = 36) String report_shelter_worker_id,
                                @NonNull @NotBlank @Size(max = 20) String callsign,
                                @NonNull @NotBlank Integer shift,
                                @NonNull @NotBlank Integer health,
                                @NonNull @NotBlank Integer mental,
                                @NonNull @NotBlank Integer spiritual,
                                @NonNull @NotBlank Integer caseworker,
                                @NonNull @NotBlank Integer feeding,
                                @NonNull @NotBlank Integer other,
                                @NonNull @NotBlank ZonedDateTime date,
                                @NonNull @NotBlank ZonedDateTime reported_date
) {  
}
