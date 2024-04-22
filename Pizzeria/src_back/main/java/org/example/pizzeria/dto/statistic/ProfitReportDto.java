package org.example.pizzeria.dto.statistic;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Report about profit")
public record ProfitReportDto(
        @Schema(description = "sales amount") Double salesAmount,
        @Schema(description = "food amount") Double foodAmount) {
}
