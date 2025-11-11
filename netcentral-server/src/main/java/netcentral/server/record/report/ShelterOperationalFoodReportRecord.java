package netcentral.server.record.report;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("report_shelter_operational_food")
public record ShelterOperationalFoodReportRecord(@Id @NonNull @NotBlank @Size(max = 36) String report_shelter_operational_food_id,
                                @NonNull @NotBlank @Size(max = 20) String callsign,
                                @NonNull @NotBlank Integer timeframe,
                                @NonNull @NotBlank Integer breakfast,
                                @NonNull @NotBlank Integer lunch,
                                @NonNull @NotBlank Integer dinner,
                                @NonNull @NotBlank Integer snack,
                                @NonNull @NotBlank ZonedDateTime date,
                                @NonNull @NotBlank ZonedDateTime reported_date
) {  
}
