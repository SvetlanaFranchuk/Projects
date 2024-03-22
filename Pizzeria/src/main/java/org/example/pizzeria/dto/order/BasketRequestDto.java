package org.example.pizzeria.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;

import java.util.Map;

@Schema(description = "Create new/update record in the basket")
public record BasketRequestDto (
    @Schema(description = "List of pizzas")
    Map<PizzaResponseDto, Integer> pizzaToCount,
    @Schema(description = "user ID")
    @NotNull Long userId){
}
