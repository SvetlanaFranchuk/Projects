package org.example.pizzeria.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Schema(description = "Create new/update record in the basket")
public record BasketRequestDto(
        @Schema(description = "List of pizzas. First parameter is pizza id, second - count pizzas ")
        Map<Long, Integer> pizzaToCount,
        @Schema(description = "user ID")
        @NotNull Long userId) {
}
