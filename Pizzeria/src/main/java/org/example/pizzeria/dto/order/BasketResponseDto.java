package org.example.pizzeria.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;

import java.util.Map;

@Schema(description = "Reading records in the basket")
public record BasketResponseDto(
    @Schema(description = "List of pizzas")
    Map<PizzaResponseDto, Integer> pizzaCountMap,
    @Schema(description = "user ID")
    @NotNull
    Long userId){
}
