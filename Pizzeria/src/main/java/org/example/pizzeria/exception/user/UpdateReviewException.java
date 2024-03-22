package org.example.pizzeria.exception.user;

import org.springframework.security.access.AccessDeniedException;

public class UpdateReviewException extends AccessDeniedException {
    public UpdateReviewException(String message) {
        super(message);
    }
}
