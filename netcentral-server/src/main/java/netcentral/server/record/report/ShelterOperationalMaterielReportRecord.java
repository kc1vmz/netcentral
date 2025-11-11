package netcentral.server.record.report;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("report_shelter_operational_materiel")
public record ShelterOperationalMaterielReportRecord(@Id @NonNull @NotBlank @Size(max = 36) String report_shelter_operational_materiel_id,
                                @NonNull @NotBlank @Size(max = 20) String callsign,
                                @NonNull @NotBlank Integer timeframe,
                                @NonNull @NotBlank Integer cots,
                                @NonNull @NotBlank Integer blankets,
                                @NonNull @NotBlank Integer comfort,
                                @NonNull @NotBlank Integer cleanup,
                                @NonNull @NotBlank Integer signage,
                                @NonNull @NotBlank Integer other,
                                @NonNull @NotBlank ZonedDateTime date,
                                @NonNull @NotBlank ZonedDateTime reported_date
) {  
}