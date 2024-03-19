package org.example.pizzeria.exeption;

public class UserReviewNotFoundException extends RuntimeException{
    public UserReviewNotFoundException(String message) {
        super(message);
    }
}
