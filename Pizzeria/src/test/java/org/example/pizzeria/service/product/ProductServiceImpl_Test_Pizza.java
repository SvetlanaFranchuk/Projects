package org.example.pizzeria.service.product;

import jakarta.persistence.EntityManager;
import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.order.OrderDetails;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.product.DeleteProductException;
import org.example.pizzeria.mapper.product.DoughMapper;
import org.example.pizzeria.mapper.product.IngredientMapper;
import org.example.pizzeria.mapper.product.PizzaMapper;
import org.example.pizzeria.repository.order.OrderDetailsRepository;
import org.example.pizzeria.repository.product.DoughRepository;
import org.example.pizzeria.repository.product.IngredientRepository;
import org.example.pizzeria.repository.product.PizzaRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpl_Test_Pizza {
    @Mock
    private EntityManager entityManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderDetailsRepository orderDetailsRepository;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private IngredientMapper ingredientMapper;
    @Mock
    private DoughRepository doughRepository;
    @Mock
    private DoughMapper doughMapper;
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private PizzaMapper pizzaMapper;
    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        Mockito.reset(ingredientRepository);
        Mockito.reset(doughRepository);
        Mockito.reset(pizzaRepository);
        Mockito.reset(orderDetailsRepository);
        Mockito.reset(userRepository);
    }

    @Test
    void addPizza() {
        Long userId = 1L;
        when(pizzaMapper.toPizza(TestData.PIZZA_REQUEST_DTO)).thenReturn(TestData.PIZZA);
        when(ingredientRepository.getReferenceById(1L)).thenReturn(TestData.INGREDIENT_1);
        when(ingredientRepository.getReferenceById(2L)).thenReturn(TestData.INGREDIENT_2);
        when(doughRepository.getReferenceById(1)).thenReturn(TestData.DOUGH_1);
        when(userRepository.getReferenceById(1L)).thenReturn(TestData.USER_APP);
        when(pizzaRepository.save(TestData.PIZZA)).thenReturn(TestData.PIZZA);
        when(pizzaMapper.toPizzaResponseDto(TestData.PIZZA)).thenReturn(TestData.PIZZA_RESPONSE_DTO);
        when(doughMapper.toDoughResponseClientDto(TestData.PIZZA.getDough())).thenReturn(TestData.DOUGH_RESPONSE_CLIENT_DTO);
        when(ingredientMapper.toIngredientResponseClientDto(TestData.INGREDIENT_2)).thenReturn(TestData.INGREDIENT_RESPONSE_CLIENT_DTO_2);
        when(ingredientMapper.toIngredientResponseClientDto(TestData.INGREDIENT_1)).thenReturn(TestData.INGREDIENT_RESPONSE_CLIENT_DTO_1);


        PizzaResponseDto result = productService.addPizza(TestData.PIZZA_REQUEST_DTO, userId);
        assertEquals(TestData.PIZZA_RESPONSE_DTO, result);
    }

    @Test
    void addPizza_InvalidUserId_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> productService.addPizza(TestData.PIZZA_REQUEST_DTO, null));
    }

    @Test
    void updatePizza() {
        Long pizzaId = 1L;
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.of(TestData.PIZZA_NEW));
        when(ingredientRepository.getReferenceById(1L)).thenReturn(TestData.INGREDIENT_1);
        when(ingredientRepository.getReferenceById(2L)).thenReturn(TestData.INGREDIENT_2);
        when(doughRepository.getReferenceById(1)).thenReturn(TestData.DOUGH_1);
        entityManager.flush();
        when(pizzaRepository.save(TestData.PIZZA_NEW)).thenReturn(TestData.PIZZA_NEW);
        when(pizzaMapper.toPizzaResponseDto(TestData.PIZZA_NEW)).thenReturn(TestData.PIZZA_RESPONSE_DTO_NEW);
        when(doughMapper.toDoughResponseClientDto(TestData.PIZZA_NEW.getDough())).thenReturn(TestData.DOUGH_RESPONSE_CLIENT_DTO);
        when(ingredientMapper.toIngredientResponseClientDto(TestData.INGREDIENT_1)).thenReturn(TestData.INGREDIENT_RESPONSE_CLIENT_DTO_1);
        when(ingredientMapper.toIngredientResponseClientDto(TestData.INGREDIENT_2)).thenReturn(TestData.INGREDIENT_RESPONSE_CLIENT_DTO_2);

        PizzaResponseDto result = productService.updatePizza(TestData.PIZZA_REQUEST_DTO_NEW, pizzaId);
        assertEquals(TestData.PIZZA_RESPONSE_DTO_NEW, result);
    }

    @Test
    void updatePizza_PizzaNotFound_ThrowInvalidIDException() {
        Long pizzaId = 123L;
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.empty());
        assertThrows(InvalidIDException.class, () -> productService.updatePizza(TestData.PIZZA_REQUEST_DTO, pizzaId));
    }

    @Test
    void deletePizzaRecipe() {
        Long pizzaId = 1L;
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.of(TestData.PIZZA));
        when(orderDetailsRepository.findAllByPizza(TestData.PIZZA)).thenReturn(Collections.emptyList());
        productService.deletePizzaRecipe(pizzaId);
        verify(pizzaRepository, times(1)).delete(TestData.PIZZA);
    }

    @Test
    void deletePizzaRecipe_RecipeAlreadyOrdered_ThrowDeleteProductException() {
        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(TestData.PIZZA));
        List<OrderDetails> detailsList = List.of(new OrderDetails(1L, TestData.PIZZA, 1,  new Order()));
        when(orderDetailsRepository.findAllByPizza(TestData.PIZZA)).thenReturn(detailsList);

        DeleteProductException exception = assertThrows(DeleteProductException.class, () -> productService.deletePizzaRecipe(1L));
        assertEquals(ErrorMessage.RECIPE_ALREADY_ORDERED, exception.getMessage());
        verify(pizzaRepository, never()).delete(TestData.PIZZA);
    }

    @Test
    void deletePizzaRecipe_InvalidID_ThrowInvalidIDException() {
        Long invalidId = 100L;
        when(pizzaRepository.findById(invalidId)).thenReturn(Optional.empty());
        InvalidIDException exception = assertThrows(InvalidIDException.class, () -> productService.deletePizzaRecipe(invalidId));
        assertEquals(ErrorMessage.INVALID_ID, exception.getMessage());
        verify(pizzaRepository, never()).delete(any());
    }

    @Test
    void getAllPizzaStandardRecipe() {
        List<Pizza> pizzas = List.of(TestData.PIZZA);
        List<PizzaResponseDto> expectedResponse = List.of(TestData.PIZZA_RESPONSE_DTO);

        when(pizzaRepository.findAllByStandardRecipe(true)).thenReturn(pizzas);
        when(pizzaMapper.toPizzaResponseDto(TestData.PIZZA)).thenReturn(TestData.PIZZA_RESPONSE_DTO);
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipe();
        assertEquals(expectedResponse, result);
    }

    @Test
    void getAllPizzaStandard_EmptyList() {
        when(pizzaRepository.findAllByStandardRecipe(true)).thenReturn(Collections.emptyList());
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipe();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllPizzaStandardRecipeByStyles() {
        List<PizzaResponseDto> expectedResponse = List.of(TestData.PIZZA_RESPONSE_DTO);
        List<Pizza> pizzas = List.of(TestData.PIZZA);

        when(pizzaRepository.findAllByStandardRecipeAndStyles(true, Styles.CLASSIC_ITALIAN)).thenReturn(pizzas);
        when(pizzaMapper.toPizzaResponseDto(TestData.PIZZA)).thenReturn(TestData.PIZZA_RESPONSE_DTO);
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByStyles(Styles.CLASSIC_ITALIAN);
        assertEquals(expectedResponse, result);
    }

    @Test
    void getAllPizzaStandardRecipeByStyles_EmptyList() {
        when(pizzaRepository.findAllByStandardRecipeAndStyles(true, Styles.CLASSIC_ITALIAN)).thenReturn(Collections.emptyList());
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByStyles(Styles.CLASSIC_ITALIAN);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllPizzaStandardRecipeByTopping() {
        List<PizzaResponseDto> expectedResponse = List.of(TestData.PIZZA_RESPONSE_DTO);
        List<Pizza> pizzas = Stream.of(TestData.PIZZA)
                .filter(pizza -> pizza.isStandardRecipe() &&
                        pizza.getToppingsFillings().equals(ToppingsFillings.CHEESE))
                .toList();

        when(pizzaRepository.findAllByStandardRecipeAndToppingsFillings(true, ToppingsFillings.CHEESE)).thenReturn(pizzas);
        when(pizzaMapper.toPizzaResponseDto(TestData.PIZZA)).thenReturn(TestData.PIZZA_RESPONSE_DTO);
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByTopping(ToppingsFillings.CHEESE);
        assertEquals(expectedResponse, result);
    }

    @Test
    void getAllPizzaStandardRecipeByTopping_EmptyList() {
        when(pizzaRepository.findAllByStandardRecipeAndToppingsFillings(true, ToppingsFillings.CHEESE)).thenReturn(Collections.emptyList());
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByTopping(ToppingsFillings.CHEESE);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllPizzaStandardRecipeByToppingByStyles() {
        List<PizzaResponseDto> expectedResponse = List.of(TestData.PIZZA_RESPONSE_DTO);
        List<Pizza> pizzas = List.of(TestData.PIZZA);

        when(pizzaRepository.findAllByStandardRecipeAndToppingsFillingsAndStyles(true, ToppingsFillings.CHEESE, Styles.CLASSIC_ITALIAN)).thenReturn(pizzas);
        when(pizzaMapper.toPizzaResponseDto(TestData.PIZZA)).thenReturn(TestData.PIZZA_RESPONSE_DTO);
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByToppingByStyles(ToppingsFillings.CHEESE, Styles.CLASSIC_ITALIAN);
        assertEquals(expectedResponse, result);
    }

    @Test
    void getAllPizzaStandardRecipeByToppingByStyles_EmptyList() {
        when(pizzaRepository.findAllByStandardRecipeAndToppingsFillingsAndStyles(true, ToppingsFillings.CHEESE, Styles.CLASSIC_ITALIAN)).thenReturn(Collections.emptyList());
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByToppingByStyles(ToppingsFillings.CHEESE, Styles.CLASSIC_ITALIAN);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPizza() {
        Long pizzaId = 1L;
        when(pizzaRepository.getReferenceById(pizzaId)).thenReturn(TestData.PIZZA);
        when(pizzaMapper.toPizzaResponseDto(TestData.PIZZA)).thenReturn(TestData.PIZZA_RESPONSE_DTO);
        PizzaResponseDto result = productService.getPizza(pizzaId);
        assertEquals(TestData.PIZZA_RESPONSE_DTO, result);
    }

    @Test
    public void testGetPizza_NonExistingId_ReturnsNull() {
        Long pizzaId = 999L;
        when(pizzaRepository.getReferenceById(pizzaId)).thenReturn(null);
        PizzaResponseDto result = productService.getPizza(pizzaId);
        assertNull(result);
    }
}