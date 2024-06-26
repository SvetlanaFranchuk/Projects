package org.example.pizzeria.controller;

import org.example.pizzeria.dto.ErrorResponseDto;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.NotCorrectArgumentException;
import org.example.pizzeria.exception.order.InvalidOrderStatusException;
import org.example.pizzeria.exception.product.*;
import org.example.pizzeria.exception.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidOrderStatusException(InvalidOrderStatusException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(DeleteProductException.class)
    public ResponseEntity<ErrorResponseDto> handleDeleteProductException(DeleteProductException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(DoughCreateException.class)
    protected ResponseEntity<ErrorResponseDto> handleDoughCreateException(DoughCreateException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(FavoritesExistException.class)
    public ResponseEntity<ErrorResponseDto> handleFavoritesExistException(FavoritesExistException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(IngredientsCreateException.class)
    public ResponseEntity<ErrorResponseDto> handleIngredientCreateException(IngredientsCreateException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(UpdateReviewException.class)
    public ResponseEntity<ErrorResponseDto> handleUpdateReviewException(UpdateReviewException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ErrorResponseDto> handleUserBlockedException(UserBlockedException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(UserCreateException.class)
    public ResponseEntity<ErrorResponseDto> handleUserCreateException(UserCreateException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(InvalidIDException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidIdException(InvalidIDException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(NotCorrectArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleNotCorrectArgumentException(NotCorrectArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponseDto> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(EntityInPizzeriaNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityInPizzeriaNotFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ex.getEntityName() + " " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    @ExceptionHandler(PizzaAlreadyInFavoritesException.class)
    public ResponseEntity<ErrorResponseDto> handlePizzaAlreadyInFavoritesException(PizzaAlreadyInFavoritesException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(StatusAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleStatusAlreadyExistsException(StatusAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        BindingResult result = ex.getBindingResult();
        for (FieldError fieldError : result.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

}
