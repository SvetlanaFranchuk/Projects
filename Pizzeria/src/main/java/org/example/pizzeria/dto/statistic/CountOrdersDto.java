package org.example.pizzeria.dto.statistic;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Report about count of orders be period")
public record CountOrdersDto(
        @Schema(description = "Date", example = "2024-01-01")
        LocalDate date,
        @Schema(description = "Count orders")
        Integer countOrders) {
}
