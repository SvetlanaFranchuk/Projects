package org.example.pizzeria.exeption;

public class InvalidIDException extends NullPointerException{
    public InvalidIDException(String message) {
        super(message);
    }
}
