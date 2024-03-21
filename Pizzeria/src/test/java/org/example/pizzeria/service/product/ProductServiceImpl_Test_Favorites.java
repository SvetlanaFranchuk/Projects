package org.example.pizzeria.service.product;

import org.example.pizzeria.dto.benefits.FavoritesResponseDto;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.benefits.Favorites;
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
import org.example.pizzeria.exception.product.FavoritesExistException;
import org.example.pizzeria.exception.user.UserNotFoundException;
import org.example.pizzeria.mapper.benefits.FavoritesMapper;
import org.example.pizzeria.mapper.product.DoughMapper;
import org.example.pizzeria.mapper.product.IngredientMapper;
import org.example.pizzeria.mapper.product.PizzaMapper;
import org.example.pizzeria.repository.benefits.FavoritesRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpl_Test_Favorites {
    private static final UserApp USER_APP = UserApp.builder()
            .id(1L)
            .userName("IvanAdmin")
            .password("12345")
            .email("iv.admin@pizzeria.com")
            .birthDate(LocalDate.of(2000, 1, 15))
            .dateRegistration(LocalDate.now())
            .isBlocked(false)
            .role(Role.CLIENT)
            .bonus(new Bonus(0,0.0))
            .build();
    private static final UserApp USER_APP_2 = UserApp.builder()
            .id(2L)
            .userName("TestClient")
            .password("12345")
            .email("clientTest@pizzeria.com")
            .birthDate(LocalDate.of(2001, 10, 15))
            .dateRegistration(LocalDate.now())
            .isBlocked(false)
            .role(Role.CLIENT)
            .bonus(new Bonus(0,0.0))
            .build();
    private static final Dough DOUGH = new Dough(1, TypeDough.CLASSICA, 100, 120, 0.23);
    private static final IngredientResponseClientDto INGREDIENT_RESPONSE_CLIENT_DTO_1 = new IngredientResponseClientDto(1L,
            "Tomato", 100, 90, GroupIngredient.BASIC);
    private static final IngredientResponseClientDto INGREDIENT_RESPONSE_CLIENT_DTO_2 = new IngredientResponseClientDto(2L,
            "Cheese", 20, 80, GroupIngredient.BASIC);
    private static final Ingredient INGREDIENT_1 = new Ingredient(1L, "Tomato", 100, 90, 0.2, GroupIngredient.BASIC, null);
    private static final Ingredient INGREDIENT_2 = new Ingredient(2L, "Cheese", 20, 80, 0.4, GroupIngredient.BASIC, null);

    private static List<IngredientResponseClientDto> ingredientResponseClientBasicDtoList = List.of(INGREDIENT_RESPONSE_CLIENT_DTO_1, INGREDIENT_RESPONSE_CLIENT_DTO_2);
    private static final DoughResponseClientDto DOUGH_RESPONSE_CLIENT_DTO = new DoughResponseClientDto(1,
            TypeDough.CLASSICA, 100, 120);

    private static final Pizza PIZZA = new Pizza(1L, "Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.MEDIUM, true, (0.2 + 0.4 + 0.23) * 1.3, 377, DOUGH, List.of(INGREDIENT_1, INGREDIENT_2));
    private static final Pizza PIZZA_2 = new Pizza(2L, "Carbonara",
            "Description for pizza Carbonara", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, true, (0.2 + 0.4 + 0.23) * 1.7, 493, DOUGH, List.of(INGREDIENT_1, INGREDIENT_2));
    private static final Pizza PIZZA_3 = new Pizza(3L, "Test3Carbonara",
            "Description for pizza Carbonara", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, true, (0.2 + 0.4 + 0.23) * 1.7, 493, DOUGH, List.of(INGREDIENT_1, INGREDIENT_2));
    private static final PizzaResponseDto PIZZA_RESPONSE_DTO = new PizzaResponseDto("Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.MEDIUM, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList, (0.2 + 0.4 + 0.23) * 1.3, 377);
    private static final PizzaResponseDto PIZZA_RESPONSE_DTO_2 = new PizzaResponseDto("Carbonara",
            "Description for pizza Carbonara", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList, (0.2 + 0.4 + 0.23) * 1.7, 493);
    private static final PizzaResponseDto PIZZA_RESPONSE_DTO_3 = new PizzaResponseDto("Test3Carbonara",
            "Description for pizza Carbonara", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList, (0.2 + 0.4 + 0.23) * 1.7, 493);

    private static final Favorites FAVORITES = new Favorites(1L, List.of(PIZZA, PIZZA_2), USER_APP);

    @Mock
    private UserRepository userRepository;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private DoughRepository doughRepository;
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private FavoritesRepository favoritesRepository;
    @Mock
    private DoughMapper doughMapper;
    @Mock
    private PizzaMapper pizzaMapper;
@Mock
private FavoritesMapper favoritesMapper;
@Mock
private IngredientMapper ingredientMapper;
    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        Mockito.reset(doughRepository);
        Mockito.reset(pizzaRepository);
        Mockito.reset(ingredientRepository);
        Mockito.reset(userRepository);
    }

    @Test
    void addPizzaToUserFavorite() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(USER_APP));
        when(favoritesRepository.findByUserApp(USER_APP)).thenReturn(Optional.of(FAVORITES));
        List<Pizza> pizzas = new ArrayList<>(FAVORITES.getPizzas());
        pizzas.add(PIZZA_3);
        FAVORITES.setPizzas(pizzas);
        when(favoritesRepository.save(FAVORITES)).thenReturn(FAVORITES);
        when(favoritesMapper.toFavoriteResponseDto(FAVORITES)).thenReturn(new FavoritesResponseDto(List.of(
                PIZZA_RESPONSE_DTO, PIZZA_RESPONSE_DTO_2, PIZZA_RESPONSE_DTO_3)));
        FavoritesResponseDto result = productService.addPizzaToUserFavorite(userId, PIZZA_3);
        assertEquals(new FavoritesResponseDto(List.of(PIZZA_RESPONSE_DTO, PIZZA_RESPONSE_DTO_2, PIZZA_RESPONSE_DTO_3)), result);
    }

    @Test
    void addPizzaToUserFavorite_NewFavoritesCreated_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(USER_APP_2));
        when(favoritesRepository.findByUserApp(USER_APP_2)).thenReturn(Optional.empty());
        when(favoritesRepository.save(any(Favorites.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(favoritesMapper.toFavoriteResponseDto(any(Favorites.class))).thenAnswer(invocation -> {
            Favorites favorites = invocation.getArgument(0);
            return new FavoritesResponseDto(List.of(PIZZA_RESPONSE_DTO));
        });
        FavoritesResponseDto result = productService.addPizzaToUserFavorite(2L, PIZZA);
        assertNotNull(result);
        assertEquals(1, result.pizzas().size());
        assertTrue(result.pizzas().contains(PIZZA_RESPONSE_DTO));
        verify(favoritesRepository, times(1)).save(any(Favorites.class));
    }
    @Test
    void addPizzaToUserFavorite_UserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> productService.addPizzaToUserFavorite(userId, new Pizza()));
    }

    @Test
    void deletePizzaFromUserFavorite() {
        Long userId = 1L;
        Long pizzaIdToRemove = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(USER_APP));
        when(favoritesRepository.findByUserApp(USER_APP)).thenReturn(Optional.of(FAVORITES));
        List<Pizza> pizzas = new ArrayList<>(FAVORITES.getPizzas());
        pizzas.removeIf(pizza -> pizza.getId().equals(pizzaIdToRemove));
        FAVORITES.setPizzas(pizzas);
        when(favoritesRepository.save(FAVORITES)).thenReturn(FAVORITES);
        productService.deletePizzaFromUserFavorite(pizzaIdToRemove, userId);

        verify(favoritesRepository, times(1)).save(FAVORITES);
        assertFalse(FAVORITES.getPizzas().contains(PIZZA_2));
    }

    @Test
    void deletePizzaFromUserFavorite_UserNotFound() {
        Long userId = 1L;
        Long pizzaIdToRemove = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> productService.deletePizzaFromUserFavorite(pizzaIdToRemove, userId));
    }
    @Test
    void deletePizzaFromUserFavorite_PizzaNotFoundInFavorites() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(USER_APP));
        when(favoritesRepository.findByUserApp(USER_APP)).thenReturn(Optional.of(FAVORITES));

        assertDoesNotThrow(() -> productService.deletePizzaFromUserFavorite(1L, userId));
    }
    @Test
    void getAllFavoritePizzaByUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(USER_APP));
        when(favoritesRepository.findByUserApp(USER_APP)).thenReturn(Optional.of(FAVORITES));
        List<PizzaResponseDto> result = productService.getAllFavoritePizzaByUser(1L);

        assertNotNull(result);
        assertEquals(3, result.size());
    }
    @Test
    void getAllFavoritePizzaByUser_FavoritesNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(USER_APP));
        when(favoritesRepository.findByUserApp(USER_APP)).thenReturn(Optional.empty());

        assertThrows(FavoritesExistException.class, () -> productService.getAllFavoritePizzaByUser(userId));
    }

    @Test
    void getAllFavoritePizzaByUser_UserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> productService.getAllFavoritePizzaByUser(userId));
    }
}