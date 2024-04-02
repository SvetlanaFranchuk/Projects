package org.example.pizzeria.entity.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest {

    @Test
    void equals_SameObject_ReturnsTrue() {
        Order order = new Order();
        assertThat(order.equals(order)).isTrue();
    }

    @Test
    void equals_NullObject_ReturnsFalse() {
        Order order = new Order();
        assertThat(order.equals(null)).isFalse();
    }

    @Test
    void equals_DifferentClass_ReturnsFalse() {
        Order order = new Order();
        Object object = new Object();
        assertThat(order.equals(object)).isFalse();
    }

    @Test
    void equals_SameId_ReturnsTrue() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(1L);
        assertThat(order1.equals(order2)).isTrue();
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        assertThat(order1.equals(order2)).isFalse();
    }

    @Test
    void hashCode_SameId_ReturnsSameHashCode() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(1L);
        assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
    }

    @Test
    void hashCode_DifferentId_ReturnsDifferentHashCode() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        assertThat(order1.hashCode()).isNotEqualTo(order2.hashCode());
    }
}