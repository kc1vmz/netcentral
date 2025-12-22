package netcentral.server.record;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

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
