package org.example.pizzeria.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Information about pizzas in order")
public record OrderDetailsResponseDto(Long pizzaId, Integer count) {
}
