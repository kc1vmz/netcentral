package netcentral.server.record.report;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("report_shelter_population")
public record ShelterCensusReportRecord(@Id @NonNull @NotBlank @Size(max = 36) String report_shelter_population_id,
                                @NonNull @NotBlank @Size(max = 20) String callsign,
                                @NonNull @NotBlank Integer p03,
                                @NonNull @NotBlank Integer p47,
                                @NonNull @NotBlank Integer p812,
                                @NonNull @NotBlank Integer p1318,
                                @NonNull @NotBlank Integer p1965,
                                @NonNull @NotBlank Integer p66,
                                @NonNull @NotBlank ZonedDateTime reported_date
) {  
}
