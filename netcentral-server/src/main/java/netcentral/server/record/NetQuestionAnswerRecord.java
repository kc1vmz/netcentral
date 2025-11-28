package netcentral.server.record;

import java.time.ZonedDateTime;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
@MappedEntity("net_question_answer")
public record NetQuestionAnswerRecord(@Id @NonNull @NotBlank @Size(max = 36) String net_question_answer_id,
                                @NonNull @NotBlank @Size(max = 36) String net_question_id,
                                @NonNull @NotBlank @Size(max = 36) String completed_net_id,
                                @NonNull @NotBlank ZonedDateTime answered_time,
                                @NonNull @NotBlank @Size(max = 20) String callsign,
                                @NonNull @NotBlank @Size(max = 50) String answer_text
) {  
}
