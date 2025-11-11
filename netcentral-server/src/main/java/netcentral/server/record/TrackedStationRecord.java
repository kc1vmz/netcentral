package netcentral.server.record;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("tracked_station")
public record TrackedStationRecord(
                @Id @NonNull @NotBlank @Size(max = 36) String tracked_station_id,
                @Nullable @Size(max = 20) String callsign,
                @Nullable @Size(max = 20) String lat,
                @Nullable @Size(max = 20) String lon,
                @NonNull @NotBlank @Size(max = 50) String name,
                @Nullable @Size(max = 250) String description,
                @Nullable @Size(max = 20) String frequency_tx,
                @Nullable @Size(max = 20) String frequency_rx,
                @Nullable @Size(max = 10) String tone,
                @Nullable ZonedDateTime last_heard_time,
                @NonNull Boolean tracking_active,
                @NonNull Integer status,
                @Nullable @Size(max = 40) String ip_address,
                @NonNull Integer type,
                @NonNull Integer electrical_power_type,
                @NonNull Integer backup_electrical_power_type,
                @NonNull Integer radio_style,
                @NonNull Integer transmit_power) {  
}
