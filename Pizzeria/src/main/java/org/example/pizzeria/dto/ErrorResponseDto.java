package org.example.pizzeria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record ErrorResponseDto (String message){
    public ErrorResponseDto(String message) {
        this.message = message;
    }
}
