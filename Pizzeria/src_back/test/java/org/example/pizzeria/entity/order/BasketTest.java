package org.example.pizzeria.entity.order;

import org.example.pizzeria.TestData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasketTest {

    @Test
    void clear() {
        Basket basket = TestData.BASKET;
        basket.setPizzas(new ArrayList<>(List.of(TestData.PIZZA, TestData.PIZZA)));
        basket.clear();
        assertEquals(0, basket.getPizzas().size());
    }

}