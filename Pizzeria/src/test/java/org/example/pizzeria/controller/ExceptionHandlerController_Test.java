package org.example.pizzeria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.controller.user.UserController;
import org.example.pizzeria.dto.ErrorResponseDto;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.NotCorrectArgumentException;
import org.example.pizzeria.exception.order.InvalidOrderStatusException;
import org.example.pizzeria.exception.product.*;
import org.example.pizzeria.exception.user.*;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.service.auth.JwtService;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(ExceptionHandlerController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class ExceptionHandlerController_Test {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private ExceptionHandlerController exceptionHandlerController;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    void handleUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Authentication failed");
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleUnauthorizedException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Authentication failed", Objects.requireNonNull(responseEntity.getBody()).message());
    }
    @Test
    void handleInvalidOrderStatusException() {
        InvalidOrderStatusException ex = new InvalidOrderStatusException(ErrorMessage.INVALID_STATUS_ORDER_FOR_DELETE);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleInvalidOrderStatusException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(ErrorMessage.INVALID_STATUS_ORDER_FOR_DELETE, Objects.requireNonNull(responseEntity.getBody()).message());
    }

    @Test
    void handleDeleteProductException() {
        DeleteProductException ex = new DeleteProductException(ErrorMessage.DOUGH_ALREADY_USE_IN_PIZZA);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleDeleteProductException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleDoughCreateException() {
        DoughCreateException ex = new DoughCreateException(ErrorMessage.DOUGH_ALREADY_EXIST);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleDoughCreateException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleFavoritesExistException() {
        FavoritesExistException ex = new FavoritesExistException(ErrorMessage.FAVORITES_IS_EMPTY);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleFavoritesExistException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleIngredientCreateException() {
        IngredientsCreateException ex = new IngredientsCreateException(ErrorMessage.INGREDIENT_ALREADY_EXIST);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleIngredientCreateException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleUpdateReviewException() {
        UpdateReviewException ex = new UpdateReviewException(ErrorMessage.CANT_REVIEW_UPDATED);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleUpdateReviewException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleUserBlockedException() {
        UserBlockedException ex = new UserBlockedException(ErrorMessage.USER_BLOCKED);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleUserBlockedException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleUserCreateException() {
        UserCreateException ex = new UserCreateException(ErrorMessage.USER_ALREADY_EXIST);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleUserCreateException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleInvalidIdException() {
        InvalidIDException ex = new InvalidIDException(ErrorMessage.INVALID_ID);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleInvalidIdException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleNotCorrectArgumentException() {
        NotCorrectArgumentException ex = new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleNotCorrectArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleNullPointerException() {
        NullPointerException ex = new NullPointerException(ErrorMessage.INVALID_ID);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleNullPointerException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    void handleEntityNotFoundException() {
        EntityInPizzeriaNotFoundException ex = new EntityInPizzeriaNotFoundException("Entity",ErrorMessage.ENTITY_NOT_FOUND);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleEntityNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Entity not found", Objects.requireNonNull(responseEntity.getBody()).message());
    }


    @Test
    void handlePizzaAlreadyInFavoritesException() {
        PizzaAlreadyInFavoritesException ex = new PizzaAlreadyInFavoritesException(ErrorMessage.PIZZA_ALREADY_IN_FAVORITES);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handlePizzaAlreadyInFavoritesException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleStatusAlreadyExistsException() {
        StatusAlreadyExistsException ex = new StatusAlreadyExistsException(ErrorMessage.PIZZA_ALREADY_IN_FAVORITES);
        ExceptionHandlerController controller = new ExceptionHandlerController();
        ResponseEntity<ErrorResponseDto> responseEntity = controller.handleStatusAlreadyExistsException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleValidationExceptions() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        ExceptionHandlerController controller = new ExceptionHandlerController();

        BindingResult result = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("objectName", "field1", "Error message 1");
        FieldError fieldError2 = new FieldError("objectName", "field2", "Error message 2");
        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("field1", "Error message 1");
        expectedErrors.put("field2", "Error message 2");
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(expectedErrors);

        when(ex.getBindingResult()).thenReturn(result);
        when(result.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<Object> actualResponse = controller.handleValidationExceptions(ex);
        assertEquals(expectedResponse, actualResponse);
    }
}