package org.example.pizzeria.dto.product.dough;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Form for update dough")
public record DoughUpdateRequestDto(
        @Schema(description = "ID dough")
        Integer id,
        @Schema(description = "Weight for small pizza")
        @NotNull(message = "Field must be filled in") @Positive @Max(200) int smallWeight,

        @Schema(description = "Nutrition for small pizza")
        @NotNull(message = "Field must be filled in") @Positive @Max(600) int smallNutrition
) {
}
