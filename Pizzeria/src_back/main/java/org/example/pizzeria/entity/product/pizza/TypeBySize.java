package org.example.pizzeria.entity.product.pizza;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeBySize {
    SMALL(1),
    MEDIUM(1.3),
    LARGE(1.7);
    private final double coefficient;
}
