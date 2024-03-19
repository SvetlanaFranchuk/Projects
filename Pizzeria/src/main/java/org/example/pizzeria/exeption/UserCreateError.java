package org.example.pizzeria.exeption;

public class UserCreateError extends RuntimeException{
    public UserCreateError(String message) {
        super(message);
    }
}
