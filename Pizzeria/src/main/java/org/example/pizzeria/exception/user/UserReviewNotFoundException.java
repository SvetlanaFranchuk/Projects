package org.example.pizzeria.exception.user;

public class UserReviewNotFoundException extends RuntimeException{
    public UserReviewNotFoundException(String message) {
        super(message);
    }
}
