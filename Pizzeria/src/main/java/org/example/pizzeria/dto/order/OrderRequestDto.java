package org.example.pizzeria.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;

import java.time.LocalDateTime;
import java.util.Map;

@Schema (description = "Form for creating/updating order")
public record OrderRequestDto(
        @Schema(description = "Delivery date and time")
        @Future
        LocalDateTime deliveryDateTime,
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
        @Schema(description = "List pizzas ID and count")
        @NotNull
        Map<Long, Integer> pizzaToCount
) {
}
