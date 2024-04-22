package org.example.pizzeria.dto;

public record ErrorResponseDto(String message) {
    public ErrorResponseDto(String message) {
        this.message = message;
    }
}
