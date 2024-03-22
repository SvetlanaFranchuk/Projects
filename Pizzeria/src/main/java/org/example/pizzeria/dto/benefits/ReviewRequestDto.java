package org.example.pizzeria.dto.benefits;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Form for reviews")
public record ReviewRequestDto(
        @Schema(description = "User Id")
        @PositiveOrZero
        Long UserId,
        @Schema(description = "Comment")
        @Size(max = 255, message = "Comment length could be less then 256 symbols")
        String comment,
        @Schema(description = "Grade")
        @Min(0)
        @Max(value = 10, message = "Grade could be from 0 to 10")
        Integer grade) {
}
