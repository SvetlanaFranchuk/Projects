package org.example.pizzeria.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "User bonus form")
public final class UserBonusDto {
    @Schema(description = "Count of pizzas ordered")
    private @Min(0) Integer countOrders;
    @Schema(description = "Sum of pizzas ordered")
    private @Min(0) Double sumOrders;
}
