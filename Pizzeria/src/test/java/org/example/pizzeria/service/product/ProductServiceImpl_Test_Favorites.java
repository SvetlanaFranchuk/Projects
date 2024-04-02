package org.example.pizzeria.service.product;

import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.benefits.FavoritesResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.product.FavoritesExistException;
import org.example.pizzeria.exception.product.PizzaAlreadyInFavoritesException;
import org.example.pizzeria.mapper.benefits.FavoritesMapper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpl_Test_Favorites {
    @Mock
    private UserRepository userRepository;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private DoughRepository doughRepository;
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private PizzaMapper pizzaMapper;
    @Mock
    private FavoritesRepository favoritesRepository;
    @Mock
    private FavoritesMapper favoritesMapper;
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
        FavoritesResponseDto expected = (new FavoritesResponseDto(List.of(
                TestData.PIZZA_RESPONSE_DTO, TestData.PIZZA_RESPONSE_DTO_2, TestData.PIZZA_RESPONSE_DTO_3)));
        when(userRepository.getReferenceById(userId)).thenReturn(TestData.USER_APP);
        when(favoritesRepository.findByUserApp_Id(userId)).thenReturn(Optional.of(TestData.FAVORITES));
        when(pizzaRepository.getReferenceById(TestData.PIZZA_3.getId())).thenReturn(TestData.PIZZA_3);
        when(favoritesRepository.save(TestData.FAVORITES)).thenReturn(TestData.FAVORITES);
        when(favoritesMapper.toFavoriteResponseDto(TestData.FAVORITES)).thenReturn(expected);
        FavoritesResponseDto result = productService.addPizzaToUserFavorite(userId, TestData.PIZZA_3.getId());
        assertEquals(expected, result);
    }

    @Test
    void addPizzaToUserFavorite_PizzaAlreadyInFavorites() {
        Long userId = 1L;
        Long pizzaId = 2L;

        when(userRepository.getReferenceById(userId)).thenReturn(TestData.USER_APP);
        when(favoritesRepository.findByUserApp_Id(userId)).thenReturn(Optional.of(TestData.FAVORITES));
        assertThrows(PizzaAlreadyInFavoritesException.class,
                () -> productService.addPizzaToUserFavorite(userId, pizzaId));
    }

    @Test
    void deletePizzaFromUserFavorite() {
        Long userId = 1L;
        Long pizzaIdToRemove = 2L;
        List<Pizza> pizzas = new ArrayList<>(TestData.FAVORITES.getPizzas());
        pizzas.removeIf(pizza -> pizza.getId().equals(pizzaIdToRemove));
        TestData.FAVORITES.setPizzas(pizzas);

        when(userRepository.findById(userId)).thenReturn(Optional.of(TestData.USER_APP));
        when(favoritesRepository.findByUserApp(TestData.USER_APP)).thenReturn(Optional.of(TestData.FAVORITES));
        when(favoritesRepository.save(TestData.FAVORITES)).thenReturn(TestData.FAVORITES);
        productService.deletePizzaFromUserFavorite(pizzaIdToRemove, userId);

        verify(favoritesRepository, times(1)).save(TestData.FAVORITES);
        assertFalse(TestData.FAVORITES.getPizzas().contains(TestData.PIZZA_2));
    }

    @Test
    void deletePizzaFromUserFavorite_UserNotFound_EntityInPizzeriaNotFoundException() {
        Long userId = 1L;
        Long pizzaIdToRemove = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> productService.deletePizzaFromUserFavorite(pizzaIdToRemove, userId));
    }

    @Test
    void deletePizzaFromUserFavorite_PizzaNotFoundInFavorites() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(TestData.USER_APP));
        when(favoritesRepository.findByUserApp(TestData.USER_APP)).thenReturn(Optional.of(TestData.FAVORITES));
        assertDoesNotThrow(() -> productService.deletePizzaFromUserFavorite(1L, userId));
    }

    @Test
    void getAllFavoritePizzaByUser() {
        when(favoritesRepository.findByUserApp_Id(TestData.USER_APP.getId())).thenReturn(Optional.of(TestData.FAVORITES));
        when(pizzaMapper.toPizzaResponseDto(TestData.FAVORITES.getPizzas().getFirst())).thenReturn(TestData.PIZZA_RESPONSE_DTO);
        List<PizzaResponseDto> result = productService.getAllFavoritePizzaByUser(1L);
        assertNotNull(result);
        assertEquals(TestData.FAVORITES.getPizzas().size(), result.size());
    }

    @Test
    void getAllFavoritePizzaByUser_FavoritesNotFound_FavoritesExistException() {
        Long userId = 1L;
        when(favoritesRepository.findByUserApp_Id(TestData.USER_APP.getId())).thenReturn(Optional.empty());
        assertThrows(FavoritesExistException.class, () -> productService.getAllFavoritePizzaByUser(userId));
    }

}