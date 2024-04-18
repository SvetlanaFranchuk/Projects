package org.example.pizzeria.dto.product.pizza;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Form for adding pizza to basket")
public class PizzaToBasketRequestDto {
    @Schema(description = "pizza id")
    @NotNull
    private Long pizzaId;
    @Schema(description = "count pizza")
    @Positive
    int countPizza;
}
