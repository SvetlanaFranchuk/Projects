package org.example.pizzeria.dto.statistic;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Report about popularity pizzas")
public record PopularityPizzasDto(
        @Schema(description = "pizza id") Long pizzaId,
        @Schema(description = "pizza title") String pizzaTitle,
        @Schema(description = "count orders") Integer countOrders) {
}
