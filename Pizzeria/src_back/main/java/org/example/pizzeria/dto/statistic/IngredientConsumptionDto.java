package org.example.pizzeria.dto.statistic;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Report about ingredient consumption")
public record IngredientConsumptionDto(
        @Schema(description = "ingredient id")
        Long ingredientId,
        @Schema(description = "ingredient name")
        String ingredientName,
        @Schema(description = "weigh")
        Double weigh) {
}
