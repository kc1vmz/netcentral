package netcentral.server.object.request;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record TrackedStationEditRequest(@NotBlank String name, String description, Integer electricalPowerType, Integer backupElectricalPowerType, 
                                            Integer radioStyle, Integer transmitPower, 
                                            String transmitFrequency, String receiveFrequency, String tone) {
}
