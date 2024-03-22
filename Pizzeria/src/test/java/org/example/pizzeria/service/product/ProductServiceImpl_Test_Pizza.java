package org.example.pizzeria.service.product;

import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.pizza.PizzaRequestDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.order.OrderDetails;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.entity.product.pizza.TypeBySize;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpl_Test_Pizza {
    private static final DoughResponseClientDto DOUGH_RESPONSE_CLIENT_DTO = new DoughResponseClientDto(1,
            TypeDough.CLASSICA, 100, 120);
    private static final Dough DOUGH = new Dough(1, TypeDough.CLASSICA, 100, 120, 0.23);
    private static final IngredientResponseClientDto INGREDIENT_RESPONSE_CLIENT_DTO_1 = new IngredientResponseClientDto(1L,
            "Tomato", 100, 90, GroupIngredient.BASIC);
    private static final IngredientResponseClientDto INGREDIENT_RESPONSE_CLIENT_DTO_2 = new IngredientResponseClientDto(2L,
            "Cheese", 20, 80, GroupIngredient.BASIC);
    private static final Ingredient INGREDIENT_1 = new Ingredient(1L, "Tomato", 100, 90, 0.2, GroupIngredient.BASIC, null);
    private static final Ingredient INGREDIENT_2 = new Ingredient(2L, "Cheese", 20, 80, 0.4, GroupIngredient.BASIC, null);

    private static List<IngredientResponseClientDto> ingredientResponseClientBasicDtoList = List.of(INGREDIENT_RESPONSE_CLIENT_DTO_1, INGREDIENT_RESPONSE_CLIENT_DTO_2);
    private static final PizzaRequestDto PIZZA_REQUEST_DTO = new PizzaRequestDto("Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.MEDIUM, DOUGH_RESPONSE_CLIENT_DTO, new ArrayList<>(), ingredientResponseClientBasicDtoList,
            new ArrayList<>());
    private static final PizzaRequestDto PIZZA_REQUEST_DTO_NEW = new PizzaRequestDto("Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, DOUGH_RESPONSE_CLIENT_DTO, new ArrayList<>(), ingredientResponseClientBasicDtoList,
            new ArrayList<>());
    private static final Pizza PIZZA = new Pizza(1L, "Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.MEDIUM, true, (0.2 + 0.4 + 0.23) * 1.3, 377, DOUGH, List.of(INGREDIENT_1, INGREDIENT_2));
    private static final Pizza PIZZA_NEW = new Pizza(1L, "Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, true, (0.2 + 0.4 + 0.23) * 1.7, 493, DOUGH, List.of(INGREDIENT_1, INGREDIENT_2));

    private static final PizzaResponseDto PIZZA_RESPONSE_DTO = new PizzaResponseDto(1L, "Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.MEDIUM, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList, (0.2 + 0.4 + 0.23) * 1.3, 377);
    private static final PizzaResponseDto PIZZA_RESPONSE_DTO_NEW = new PizzaResponseDto(1L, "Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList, (0.2 + 0.4 + 0.23) * 1.7, 493);
    private static final UserApp USER_APP = UserApp.builder()
            .id(1L)
            .userName("IvanAdmin")
            .password("12345")
            .email("iv.admin@pizzeria.com")
            .birthDate(LocalDate.of(2000, 1, 15))
            .dateRegistration(LocalDate.now())
            .isBlocked(false)
            .role(Role.CLIENT)
            .bonus(new Bonus(0, 0.0))
            .build();
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderDetailsRepository orderDetailsRepository;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private DoughRepository doughRepository;
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private IngredientMapper ingredientMapper;
    @Mock
    private DoughMapper doughMapper;
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
        when(userRepository.findById(1L)).thenReturn(Optional.of(USER_APP));
        List<Ingredient> ingredients = List.of(INGREDIENT_1, INGREDIENT_2);
        when(ingredientRepository.getReferenceById(1L)).thenReturn(INGREDIENT_1);
        when(ingredientRepository.getReferenceById(2L)).thenReturn(INGREDIENT_2);
        when(doughRepository.getReferenceById(1)).thenReturn(DOUGH);
        when(pizzaMapper.toPizza(PIZZA_REQUEST_DTO.title(),
                PIZZA_REQUEST_DTO.description(), PIZZA_REQUEST_DTO.styles(), PIZZA_REQUEST_DTO.toppingsFillings(),
                PIZZA_REQUEST_DTO.size(), !USER_APP.getRole().equals(Role.CLIENT),
                (0.2 + 0.4 + 0.23) * 1.3, 377, DOUGH, ingredients)).thenReturn(PIZZA);
        when(pizzaRepository.save(PIZZA)).thenReturn(PIZZA);
        when(doughMapper.toDoughResponseClientDto(DOUGH)).thenReturn(DOUGH_RESPONSE_CLIENT_DTO);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_1)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_1);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_2)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_2);
        when(pizzaMapper.toPizzaResponseDto(PIZZA, DOUGH_RESPONSE_CLIENT_DTO,
                List.of(INGREDIENT_RESPONSE_CLIENT_DTO_1, INGREDIENT_RESPONSE_CLIENT_DTO_2)))
                .thenReturn(PIZZA_RESPONSE_DTO);

        PizzaResponseDto result = productService.addPizza(PIZZA_REQUEST_DTO, userId);
        assertEquals(PIZZA_RESPONSE_DTO, result);
    }

    @Test
    void addPizza_InvalidUserId() {
        assertThrows(NullPointerException.class, () -> productService.addPizza(PIZZA_REQUEST_DTO, null));
    }

    @Test
    void updatePizza() {
        Long pizzaId = 1L;
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.of(PIZZA));
        when(ingredientRepository.getReferenceById(1L)).thenReturn(INGREDIENT_1);
        when(ingredientRepository.getReferenceById(2L)).thenReturn(INGREDIENT_2);
        when(doughRepository.getReferenceById(1)).thenReturn(DOUGH);
        when(pizzaRepository.save(PIZZA_NEW)).thenReturn(PIZZA_NEW);
        when(doughMapper.toDoughResponseClientDto(DOUGH)).thenReturn(DOUGH_RESPONSE_CLIENT_DTO);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_1)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_1);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_2)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_2);
        when(pizzaMapper.toPizzaResponseDto(PIZZA_NEW, DOUGH_RESPONSE_CLIENT_DTO,
                List.of(INGREDIENT_RESPONSE_CLIENT_DTO_1, INGREDIENT_RESPONSE_CLIENT_DTO_2)))
                .thenReturn(PIZZA_RESPONSE_DTO_NEW);

        PizzaResponseDto result = productService.updatePizza(PIZZA_REQUEST_DTO_NEW, pizzaId);
        assertEquals(PIZZA_RESPONSE_DTO_NEW, result);
    }

    @Test
    void updatePizza_PizzaNotFound() {
        Long pizzaId = 123L;
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.empty());
        assertThrows(InvalidIDException.class, () -> productService.updatePizza(PIZZA_REQUEST_DTO, pizzaId));
    }

    @Test
    void deletePizzaRecipe() {
        Long pizzaId = 1L;
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.of(PIZZA));
        when(orderDetailsRepository.findAllByPizzasContaining(PIZZA)).thenReturn(Collections.emptyList());
        productService.deletePizzaRecipe(pizzaId);
        verify(pizzaRepository, times(1)).delete(PIZZA);
    }

    @Test
    void deletePizzaRecipe_RecipeAlreadyOrdered() {
        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(PIZZA));
        List<OrderDetails> detailsList = List.of(new OrderDetails(1L,
                LocalDateTime.of(2024, 4, 18, 6, 45), null,
                new Order(), List.of(PIZZA)));
        when(orderDetailsRepository.findAllByPizzasContaining(PIZZA)).thenReturn(detailsList);

        DeleteProductException exception = assertThrows(DeleteProductException.class, () -> {
            productService.deletePizzaRecipe(1L);
        });
        assertEquals(ErrorMessage.RECIPE_ALREADY_ORDERED, exception.getMessage());
        verify(pizzaRepository, never()).delete(PIZZA);
    }

    @Test
    void deletePizzaRecipe_InvalidID() {
        Long invalidId = 100L;
        when(pizzaRepository.findById(invalidId)).thenReturn(Optional.empty());
        InvalidIDException exception = assertThrows(InvalidIDException.class, () -> {
            productService.deletePizzaRecipe(invalidId);
        });
        assertEquals(ErrorMessage.INVALID_ID, exception.getMessage());
        verify(pizzaRepository, never()).delete(any());
    }

    @Test
    void getAllPizzaStandardRecipe() {
        List<Pizza> pizzas = List.of(PIZZA).stream()
                .filter(Pizza::isStandardRecipe)
                .toList();
        List<PizzaResponseDto> expectedResponse = List.of(PIZZA_RESPONSE_DTO);

        when(pizzaRepository.findAll()).thenReturn(pizzas);
        when(doughMapper.toDoughResponseClientDto(PIZZA.getDough())).thenReturn(DOUGH_RESPONSE_CLIENT_DTO);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_1)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_1);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_2)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_2);
        when(pizzaMapper.toPizzaResponseDto(PIZZA, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList))
                .thenReturn(PIZZA_RESPONSE_DTO);

        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipe();
        assertEquals(expectedResponse, result);
    }

    @Test
    void getAllPizzaStandard_EmptyList() {
        when(pizzaRepository.findAll()).thenReturn(Collections.emptyList());
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipe();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllPizzaStandardRecipeByStyles() {
        List<PizzaResponseDto> expectedResponse = List.of(PIZZA_RESPONSE_DTO);

        List<Pizza> pizzas = List.of(PIZZA).stream()
                .filter(pizza -> pizza.isStandardRecipe() &&
                        pizza.getStyles().equals(Styles.CLASSIC_ITALIAN))
                .toList();

        when(pizzaRepository.findAll()).thenReturn(pizzas);
        when(doughMapper.toDoughResponseClientDto(PIZZA.getDough())).thenReturn(DOUGH_RESPONSE_CLIENT_DTO);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_1)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_1);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_2)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_2);
        when(pizzaMapper.toPizzaResponseDto(PIZZA, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList))
                .thenReturn(PIZZA_RESPONSE_DTO);

        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByStyles(Styles.CLASSIC_ITALIAN);
        assertEquals(expectedResponse, result);
    }

    @Test
    void getAllPizzaStandardRecipeByStyles_EmptyList() {
        when(pizzaRepository.findAll()).thenReturn(Collections.emptyList());
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByStyles(Styles.CLASSIC_ITALIAN);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllPizzaStandardRecipeByTopping() {
        List<PizzaResponseDto> expectedResponse = List.of(PIZZA_RESPONSE_DTO);

        List<Pizza> pizzas = List.of(PIZZA).stream()
                .filter(pizza -> pizza.isStandardRecipe() &&
                        pizza.getToppingsFillings().equals(ToppingsFillings.CHEESE))
                .toList();

        when(pizzaRepository.findAll()).thenReturn(pizzas);
        when(doughMapper.toDoughResponseClientDto(PIZZA.getDough())).thenReturn(DOUGH_RESPONSE_CLIENT_DTO);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_1)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_1);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_2)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_2);
        when(pizzaMapper.toPizzaResponseDto(PIZZA, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList))
                .thenReturn(PIZZA_RESPONSE_DTO);

        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByTopping(ToppingsFillings.CHEESE);
        assertEquals(expectedResponse, result);
    }

    @Test
    void getAllPizzaStandardRecipeByTopping_EmptyList() {
        when(pizzaRepository.findAll()).thenReturn(Collections.emptyList());
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByTopping(ToppingsFillings.CHEESE);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllPizzaStandardRecipeByToppingByStyles() {
        List<PizzaResponseDto> expectedResponse = List.of(PIZZA_RESPONSE_DTO);

        List<Pizza> pizzas = List.of(PIZZA).stream()
                .filter(pizza -> pizza.isStandardRecipe() &&
                        pizza.getToppingsFillings().equals(ToppingsFillings.CHEESE) &&
                        pizza.getStyles().equals(Styles.CLASSIC_ITALIAN))
                .toList();

        when(pizzaRepository.findAll()).thenReturn(pizzas);
        when(doughMapper.toDoughResponseClientDto(PIZZA.getDough())).thenReturn(DOUGH_RESPONSE_CLIENT_DTO);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_1)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_1);
        when(ingredientMapper.toIngredientResponseClientDto(INGREDIENT_2)).thenReturn(INGREDIENT_RESPONSE_CLIENT_DTO_2);
        when(pizzaMapper.toPizzaResponseDto(PIZZA, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList))
                .thenReturn(PIZZA_RESPONSE_DTO);

        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByToppingByStyles(ToppingsFillings.CHEESE, Styles.CLASSIC_ITALIAN);
        assertEquals(expectedResponse, result);
    }

    @Test
    void getAllPizzaStandardRecipeByToppingByStyles_EmptyList() {
        when(pizzaRepository.findAll()).thenReturn(Collections.emptyList());
        List<PizzaResponseDto> result = productService.getAllPizzaStandardRecipeByToppingByStyles(ToppingsFillings.CHEESE, Styles.CLASSIC_ITALIAN);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}