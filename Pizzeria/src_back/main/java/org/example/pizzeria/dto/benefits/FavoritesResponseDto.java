package org.example.pizzeria.dto.benefits;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;

import java.util.List;

@Schema(description = "Form reading favorite pizzas")
public record FavoritesResponseDto(
        @Schema(description = "Pizzas list")
        List<PizzaResponseDto> pizzas) {
}
