package org.example.pizzeria.dto.benefits;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;

import java.util.List;

@Schema(description = "Form for create/update favorites")
public record FavoritesRequestDto(
        @Schema(description = "List of pizzas")
        List<PizzaResponseDto> pizzas,
        @Schema(description = "user ID")
        Long userId) {
}
