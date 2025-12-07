package netcentral.server.object.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record NetCreateRequest(@NotBlank String callsign, @NotBlank String name, String description, String voiceFrequency, 
                                    String lat, String lon, String announce, String checkinReminder, String checkinMessage, 
                                    boolean open,  boolean participantInviteAllowed, String expectedCallsigns, String creatorName) {
}
