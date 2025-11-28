package netcentral.server.record;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("net_question")
public record NetQuestionRecord(@Id @NonNull @NotBlank @Size(max = 36) String net_question_id,
                                @NonNull @NotBlank @Size(max = 36) String completed_net_id,
                                @NonNull @NotBlank Integer number,
                                @NonNull @NotBlank ZonedDateTime asked_time,
                                @NonNull @NotBlank Boolean active,
                                @NonNull @NotBlank Integer reminder_minutes,
                                @NonNull @NotBlank @Size(max = 50) String question_text,
                                @NonNull @NotBlank ZonedDateTime next_reminder_time
) {  
}
