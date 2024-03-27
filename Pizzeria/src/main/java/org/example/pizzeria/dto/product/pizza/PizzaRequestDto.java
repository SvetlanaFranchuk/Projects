package org.example.pizzeria.dto.product.pizza;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.entity.product.pizza.TypeBySize;

import java.util.List;

@Schema(description = "Form for creating/updating pizza")
public record PizzaRequestDto(
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

        @Schema(description = "Dough id")
        @NotNull(message = "Field must be filled in") int doughId,

        @Schema(description = "List of sauce")
        List<Long> ingredientsSauceListId,
        @Schema(description = "List of basic topping")
        List<Long> ingredientsBasicListId,
        @Schema(description = "List of extra topping")
        List<Long> ingredientsExtraListId
) {
}
