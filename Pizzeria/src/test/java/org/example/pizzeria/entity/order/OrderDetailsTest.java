package org.example.pizzeria.entity.order;

import org.example.pizzeria.entity.product.pizza.Pizza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderDetailsTest {

    private OrderDetails orderDetails;
    private Pizza pizza1;
    private Pizza pizza2;

    @BeforeEach
    void setUp() {
        orderDetails = new OrderDetails();
        pizza1 = new Pizza();
        pizza2 = new Pizza();
    }

    @Test
    void testAddPizza() {
        orderDetails.addPizza(pizza1);
        assertEquals(1, orderDetails.getPizzas().size());
        assertEquals(pizza1, orderDetails.getPizzas().get(0));

        orderDetails.addPizza(pizza2);
        assertEquals(2, orderDetails.getPizzas().size());
        assertEquals(pizza2, orderDetails.getPizzas().get(1));
    }

    @Test
    void testRemovePizza() {
        orderDetails.addPizza(pizza1);
        orderDetails.addPizza(pizza2);

        orderDetails.removePizza(pizza1);
        assertEquals(1, orderDetails.getPizzas().size());
        assertEquals(pizza2, orderDetails.getPizzas().get(0));

        orderDetails.removePizza(pizza2);
        assertEquals(0, orderDetails.getPizzas().size());
    }
}