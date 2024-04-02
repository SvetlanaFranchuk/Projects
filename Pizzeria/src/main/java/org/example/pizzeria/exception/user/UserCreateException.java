package org.example.pizzeria.exception.user;

public class UserCreateException extends RuntimeException{
    public UserCreateException(String message) {
        super(message);
    }
}
