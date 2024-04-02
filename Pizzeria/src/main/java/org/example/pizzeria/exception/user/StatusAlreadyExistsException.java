package org.example.pizzeria.exception.user;

public class StatusAlreadyExistsException extends IllegalStateException{
    public StatusAlreadyExistsException(String message) {
        super(message);
    }
}
