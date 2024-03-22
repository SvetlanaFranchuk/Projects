package org.example.pizzeria.exception.product;

public class FavoritesExistException extends RuntimeException{
    public FavoritesExistException(String message) {
        super(message);
    }

}
