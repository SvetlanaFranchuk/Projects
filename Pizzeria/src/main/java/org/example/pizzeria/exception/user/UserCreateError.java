package org.example.pizzeria.exception.user;

public class UserCreateError extends RuntimeException{
    public UserCreateError(String message) {
        super(message);
    }
}
