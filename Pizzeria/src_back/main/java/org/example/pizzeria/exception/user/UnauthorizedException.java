package org.example.pizzeria.exception.user;

import org.springframework.security.core.AuthenticationException;

public class UnauthorizedException extends AuthenticationException {
    public UnauthorizedException(String msg) {
        super(msg);
    }
}
