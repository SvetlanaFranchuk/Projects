package org.example.pizzeria.exception;

public class NotCorrectArgumentException extends RuntimeException{
    public NotCorrectArgumentException(String message) {
        super(message);
    }
}
