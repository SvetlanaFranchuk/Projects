package org.example.pizzeria.exeption;

import org.springframework.security.access.AccessDeniedException;

public class UpdateReviewException extends AccessDeniedException {
    public UpdateReviewException(String message) {
        super(message);
    }
}
