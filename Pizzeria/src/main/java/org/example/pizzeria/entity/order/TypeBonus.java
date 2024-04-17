package org.example.pizzeria.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeBonus {
    DISCOUNT_30(null, 30),
    DISCOUNT_50(100.0, null),
    DISCOUNT_100(100.0, 10);
    private final Double sumConditions;
    private final Integer countConditions;
}
