package org.example.pizzeria.entity.user;

import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAppTest {
    private UserApp userApp;
    private Review review;
    private Order order;

    @BeforeEach
    void setUp() {
        userApp = new UserApp();
        userApp.setId(1L);
        review = new Review();
        review.setId(1L);
        order = new Order();
        order.setId(1L);
    }

    @Test
    void testAddReview() {
        userApp.addReview(review);
        assertEquals(1, userApp.getReviews().size());
        assertTrue(userApp.getReviews().contains(review));
        assertEquals(userApp, review.getUserApp());
    }

    @Test
    void testRemoveReview() {
        userApp.addReview(review);
        userApp.removeReview(review);
        assertEquals(0, userApp.getReviews().size());
        assertFalse(userApp.getReviews().contains(review));
        assertNull(review.getUserApp());
    }

    @Test
    void testAddOrder() {
        userApp.addOrder(order);
        assertEquals(1, userApp.getOrders().size());
        assertTrue(userApp.getOrders().contains(order));
        assertEquals(userApp, order.getUserApp());
    }

    @Test
    void testRemoveOrder() {
        userApp.addOrder(order);
        userApp.removeOrder(order);
        assertEquals(0, userApp.getOrders().size());
        assertFalse(userApp.getOrders().contains(order));
        assertNull(order.getUserApp());
    }
}