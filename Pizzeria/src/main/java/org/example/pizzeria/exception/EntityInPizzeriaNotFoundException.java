package org.example.pizzeria.exception;

import lombok.Getter;

@Getter
public class EntityInPizzeriaNotFoundException extends RuntimeException {
    private final String entityName;

    public EntityInPizzeriaNotFoundException(String entityName, String message) {
        super(message);
        this.entityName = entityName;
    }

}
