package org.example.pizzeria.exception.product;

public class PizzaAlreadyInFavoritesException extends RuntimeException {
    public PizzaAlreadyInFavoritesException(String message) {
        super(message);
    }
}
