package org.example.pizzeria.dto.product.pizza;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.entity.product.pizza.TypeBySize;

import java.util.List;

@Schema(description = "Form for reading recipe of pizza")
public record PizzaResponseDto(
        @Schema(description = "Title")
        @NotNull(message = "Field must be filled in")
        @Size(min = 5, max = 35, message = "Title length of pizza should be from 5 to 35 symbols")
        String title,

        @Schema(description = "description")
        @Size(max = 255, message = "Description length of pizza should be less than 256 symbols")
        String description,

        @Schema(description = "Type cooking")
        @NotNull(message = "Field must be filled in") Styles styles,

        @Schema(description = "Topping and filling")
        ToppingsFillings toppingsFillings,

        @Schema(description = "Size")
        @NotNull(message = "Field must be filled in") TypeBySize size,

        @Schema(description = "Dough")
        @NotNull(message = "Field must be filled in") DoughResponseClientDto dough,

        @Schema(description = "List of sauce")
        @NotNull(message = "Field must be filled in") List<IngredientResponseClientDto> ingredientsList,

        @Schema(description = "Amount of pizza")
        @NotNull(message = "Field must be filled in") @Positive double amount,

        @Schema(description = "Nutrition of pizza")
        @NotNull(message = "Field must be filled in") @Positive int nutrition
) {
}
