package org.example.pizzeria.dto.product.ingredient;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;

@Schema(description = "Form for creating/updating ingredient")
public record IngredientRequestDto(
        @Schema(description = "Name of ingredient")
        @NotNull(message = "Field must be filled in")
        @Size(min = 5, max = 75, message = "Name length of ingredient should be from 5 to 75 symbols")
        String name,

        @Schema(description = "Weight of ingredient")
        @NotNull(message = "Field must be filled in") @Positive @Max(200) int weight,

        @Schema(description = "Nutrition of ingredient")
        @NotNull(message = "Field must be filled in") @Positive @Max(600) int nutrition,

        @Schema(description = "Price of ingredient")
        @NotNull(message = "Field must be filled in") @Positive double price,

        @Schema(description = "Group of ingredient")
        @NotNull(message = "Field must be filled in") GroupIngredient groupIngredient

) {
}
