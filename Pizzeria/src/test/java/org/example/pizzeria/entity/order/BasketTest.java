package org.example.pizzeria.entity.order;

import org.example.pizzeria.entity.product.pizza.Pizza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasketTest {

    private Basket basket;
    private Pizza pizza1;
    private Pizza pizza2;

    @BeforeEach
    void setUp() {
        basket = new Basket();
        pizza1 = new Pizza();
        pizza2 = new Pizza();
    }

    @Test
    void testAddPizza() {
        basket.addPizza(pizza1);
        assertEquals(1, basket.getPizzas().size());
        assertEquals(pizza1, basket.getPizzas().get(0));

        basket.addPizza(pizza2);
        assertEquals(2, basket.getPizzas().size());
        assertEquals(pizza2, basket.getPizzas().get(1));
    }

    @Test
    void testRemovePizza() {
        basket.addPizza(pizza1);
        basket.addPizza(pizza2);

        basket.removePizza(pizza1);
        assertEquals(1, basket.getPizzas().size());
        assertEquals(pizza2, basket.getPizzas().get(0));

        basket.removePizza(pizza2);
        assertEquals(0, basket.getPizzas().size());
    }

}