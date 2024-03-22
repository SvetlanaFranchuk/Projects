package org.example.pizzeria.exception;

public class InvalidIDException extends NullPointerException{
    public InvalidIDException(String message) {
        super(message);
    }
}
