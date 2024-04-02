package org.example.pizzeria.exception;

import jakarta.persistence.EntityNotFoundException;

public class EntityInPizzeriaNotFoundException extends RuntimeException {
    private final String entityName;
    public EntityInPizzeriaNotFoundException(String entityName, String message) {
        super(message);
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }
}
