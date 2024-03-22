package org.example.pizzeria.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.order.StatusOrder;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Form for reading status order")
public record OrderStatusResponseDto(
        @NotNull
        Long id,
        @Schema(description = "Total sum of order")
        @PositiveOrZero
        double sum,
        @Schema(description = "Status order")
        @NotNull
        StatusOrder statusOrder,
        @Schema(description = "Order date and time")
        @PastOrPresent
        LocalDateTime orderDateTime,
        @Schema(description = "User id")
        @NotNull
        Long userAppId
) {
}
