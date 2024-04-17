package org.example.pizzeria.dto.benefits;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Form for viewing reviews")
public record ReviewResponseDto(
        @Schema(description = "Comment")
        @Size(max = 255, message = "Comment length could be less then 256 symbols")
        String comment,
        @Schema(description = "Grade")
        @Min(0)
        Integer grade,
        @Schema(description = "user name")
        @Size(min = 5, max = 25, message = "User name length could be from 5 to 25 symbols")
        String userName,
        @Schema(description = "Data reviews", example = "2004-08-29")
        LocalDateTime reviewDate
) {
}
