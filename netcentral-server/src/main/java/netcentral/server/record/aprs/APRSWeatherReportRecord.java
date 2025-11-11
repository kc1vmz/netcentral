package netcentral.server.record.aprs;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("aprs_weather_report")
public record APRSWeatherReportRecord(@Id @NonNull @NotBlank @Size(max = 36) String aprs_weather_report_id, 
                                    @NonNull @NotBlank @Size(max = 36) String source,
                                    @NonNull @NotBlank ZonedDateTime heard_time,
                                    @NonNull @NotBlank @Size(max = 20) String callsign_from,
                                    @Nullable @Size(max = 20) String lat,
                                    @Nullable @Size(max = 20) String lon,
                                    @Nullable @Size(max = 10) String time,
                                    @Nullable Integer wind_direction,
                                    @Nullable Integer wind_speed,
                                    @Nullable Integer gust,
                                    @Nullable Integer temperature,
                                    @Nullable Integer rainfall_last_1hr,
                                    @Nullable Integer rainfall_last_24hr,
                                    @Nullable Integer rainfall_since_midnight,
                                    @Nullable Integer raw_rain_counter,
                                    @Nullable Integer humidity,
                                    @Nullable Integer barometric_pressure,
                                    @Nullable Integer luminosity,
                                    @Nullable Integer snowfall_last_24hr
                                    ) {  
}

