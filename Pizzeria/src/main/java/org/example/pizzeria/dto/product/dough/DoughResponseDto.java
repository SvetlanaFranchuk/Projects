package org.example.pizzeria.dto.product.dough;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.pizzeria.entity.product.ingredient.TypeDough;

@Schema(description = "Form for read information about dough")
public record DoughResponseDto(
        @Schema(description = "Type of dough")
        @NotNull(message = "Field must be filled in") TypeDough typeDough,

        @Schema(description = "Weight for small pizza")
        @NotNull(message = "Field must be filled in") @Positive @Max(200) int smallWeight,

        @Schema(description = "Nutrition for small pizza")
        @NotNull(message = "Field must be filled in") @Positive @Max(600) int smallNutrition,

        @Schema(description = "Price for small pizza")
        @NotNull(message = "Field must be filled in") @Positive double smallPrice){

}
