package netcentral.server.object.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record ScheduledNetCreateRequest(@NotBlank String callsign, @NotBlank String name, String description, Integer type, 
                                            String voiceFrequency, String lat, String lon, String announce, int dayStart, String timeStartStr, 
                                            int duration, String checkinReminder, String checkinMessage) {
}
