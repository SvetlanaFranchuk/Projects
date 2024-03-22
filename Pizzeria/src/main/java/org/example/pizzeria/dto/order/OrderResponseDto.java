package org.example.pizzeria.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.order.StatusOrder;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Form for reading order")
public record OrderResponseDto(
        @NotNull
        Long id,
        @Schema(description = "City")
        @Size(max = 75, message = "City length could be less than 76 symbols")
        String deliveryCity,
        @Schema(description = "Street name")
        @Size(max = 75, message = "Street name length could be less than 76 symbols")
        String deliveryStreetName,
        @Schema(description = "House number")
        @Size(max = 5, message = "House number length could be less than 6 symbols")
        String deliveryHouseNumber,
        @Schema(description = "Apartment number")
        @Size(max = 5, message = "Apartment number length could be less than 6 symbols")
        String deliveryApartmentNumber,
        @Schema(description = "Delivery date and time")
        @Future
        LocalDateTime deliveryDateTime,
        @Schema(description = "Total sum of order")
        @PositiveOrZero
        double sum,
        @Schema(description = "Status order")
        @NotNull
        StatusOrder statusOrder,
        @Schema(description = "Order date and time")
        @PastOrPresent
        LocalDateTime orderDateTime,
        @Schema(description = "List pizzas")
        @NotNull
        Map<PizzaResponseDto, Integer> pizzaToCount,
        @Schema(description = "User id")
        @NotNull
        Long userAppId
) {
}
