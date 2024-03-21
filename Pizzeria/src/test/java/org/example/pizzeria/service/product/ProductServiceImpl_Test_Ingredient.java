package org.example.pizzeria.service.product;

import org.example.pizzeria.dto.product.ingredient.IngredientRequestDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseDto;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.product.DeleteProductException;
import org.example.pizzeria.exception.product.IngredientsCreateException;
import org.example.pizzeria.mapper.product.IngredientMapper;
import org.example.pizzeria.repository.product.IngredientRepository;
import org.example.pizzeria.repository.product.PizzaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpl_Test_Ingredient {
    private static final IngredientRequestDto INGREDIENT_REQUEST_DTO = new IngredientRequestDto("Tomato", 100, 120, 0.23, GroupIngredient.EXTRA);
    private static final IngredientResponseDto INGREDIENT_RESPONSE_DTO = new IngredientResponseDto("Tomato", 100, 120, 0.23, GroupIngredient.EXTRA);
    private static final IngredientRequestDto INGREDIENT_REQUEST_DTO_NEW = new IngredientRequestDto("Tomato", 110, 120, 0.25, GroupIngredient.EXTRA);
    private static final IngredientResponseDto INGREDIENT_RESPONSE_DTO_NEW = new IngredientResponseDto("Tomato", 110, 120, 0.25, GroupIngredient.EXTRA);
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private IngredientMapper ingredientMapper;
    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        Mockito.reset(ingredientRepository);
        Mockito.reset(pizzaRepository);
    }

    @Test
    void addIngredient() {
        Ingredient ingredient = new Ingredient(1L, "Tomato", 100, 120, 0.23, GroupIngredient.EXTRA, null);
        when(ingredientMapper.toIngredient(INGREDIENT_REQUEST_DTO.name(), INGREDIENT_REQUEST_DTO.weight(),
                INGREDIENT_REQUEST_DTO.nutrition(), INGREDIENT_REQUEST_DTO.price(), INGREDIENT_REQUEST_DTO.groupIngredient(),
                null)).thenReturn(ingredient);
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);
        when(ingredientMapper.toIngredientResponseDto(ingredient)).thenReturn(INGREDIENT_RESPONSE_DTO);

        IngredientResponseDto result = productService.addIngredient(INGREDIENT_REQUEST_DTO);
        assertEquals(INGREDIENT_RESPONSE_DTO, result);
    }

    @Test
    void addIngredient_DuplicateDoughTypeAndPrice() {
        Ingredient ingredient = new Ingredient(1L, "Tomato", 100, 120, 0.23, GroupIngredient.EXTRA, null);
        when(ingredientRepository.findAllByNameAndPrice("Tomato", 0.23))
                .thenReturn(Collections.singletonList(ingredient));
        assertThrows(IngredientsCreateException.class, () -> productService.addIngredient(INGREDIENT_REQUEST_DTO));
    }


    @Test
    void updateIngredient() {
        Ingredient existingIngredient = new Ingredient(1L, "Tomato", 100, 120, 0.23, GroupIngredient.EXTRA, null);
        Ingredient newIngredient = new Ingredient(1L, "Tomato", 110, 120, 0.25, GroupIngredient.EXTRA, null);
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(existingIngredient));
        when(ingredientRepository.save(newIngredient)).thenReturn(newIngredient);
        when(ingredientMapper.toIngredientResponseDto(newIngredient)).thenReturn(INGREDIENT_RESPONSE_DTO_NEW);
        IngredientResponseDto responseDto = productService.updateIngredient(INGREDIENT_REQUEST_DTO_NEW, 1L);
        assertEquals(INGREDIENT_RESPONSE_DTO_NEW, responseDto);
    }

    @Test
    void updateIngredient_InvalidID() {
        IngredientRequestDto updatedIngredient = new IngredientRequestDto("Tomato", 110, 120, 0.25, GroupIngredient.BASIC);
        assertThrows(NullPointerException.class, () -> productService.updateIngredient(updatedIngredient, null));
    }

    @Test
    void deleteIngredient() {
        Ingredient newIngredient = new Ingredient(1L, "Tomato", 110, 120, 0.25, GroupIngredient.EXTRA, null);
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(newIngredient));
        when(pizzaRepository.findAllByIngredientsListIsContaining(newIngredient)).thenReturn(Collections.emptyList());
        productService.deleteIngredient(1L);
    }

    @Test
    void deleteIngredient_IngredientNotFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(InvalidIDException.class, () -> productService.deleteIngredient(1L));
    }

    @Test
    void deleteIngredient_IngredientInUse() {
        Ingredient newIngredient = new Ingredient(1L, "Tomato", 110, 120, 0.25, GroupIngredient.EXTRA, null);
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(newIngredient));
        when(pizzaRepository.findAllByIngredientsListIsContaining(newIngredient)).thenReturn(Collections.singletonList(new Pizza()));
        assertThrows(DeleteProductException.class, () -> productService.deleteIngredient(1L));
    }

    @Test
    void getAllIngredientForAdmin() {
        List<Ingredient> ingredientList = Arrays.asList(
                new Ingredient(1L, "Tomato", 110, 120, 0.25, GroupIngredient.EXTRA, null),
                new Ingredient(2L, "Tomato", 90, 110, 0.24, GroupIngredient.SAUCE, null)
        );
        when(ingredientRepository.findAll()).thenReturn(ingredientList);
        List<IngredientResponseDto> response = productService.getAllIngredientForAdmin();
        assertFalse(response.isEmpty());
        assertEquals(ingredientList.size(), response.size());
        assertEquals(ingredientList.get(0).getName(), "Tomato");
        assertEquals(ingredientList.get(0).getGroupIngredient(), GroupIngredient.EXTRA);
        assertEquals(ingredientList.get(1).getName(), "Tomato");
        assertEquals(ingredientList.get(1).getGroupIngredient(), GroupIngredient.SAUCE);
    }

    @Test
    void getAllIngredientForAdmin_EmptyList() {
        List<Ingredient> emptyList = Collections.emptyList();
        when(ingredientRepository.findAll()).thenReturn(emptyList);
        List<IngredientResponseDto> response = productService.getAllIngredientForAdmin();
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllIngredientByGroup() {
        Ingredient ingredient1 = new Ingredient(1L, "Cheese", 30, 70, 0.51, GroupIngredient.SAUCE, null);
        Ingredient ingredient2 = new Ingredient(2L, "Tomato", 90, 110, 0.24, GroupIngredient.SAUCE, null);
        List<Ingredient> ingredientList = List.of(ingredient1, ingredient2);
        IngredientResponseClientDto ingredientResponseClientDto1 = new IngredientResponseClientDto(1L,"Cheese", 30, 70, GroupIngredient.SAUCE);
        IngredientResponseClientDto ingredientResponseClientDto2 = new IngredientResponseClientDto(2L, "Tomato", 90, 110, GroupIngredient.SAUCE);
        List<IngredientResponseClientDto> exsitingList = List.of(ingredientResponseClientDto1, ingredientResponseClientDto2);
        when(ingredientRepository.findAllByGroupIngredient(GroupIngredient.SAUCE)).thenReturn(ingredientList);
        when(ingredientMapper.toIngredientResponseClientDto(ingredient1)).thenReturn(ingredientResponseClientDto1);
        when(ingredientMapper.toIngredientResponseClientDto(ingredient2)).thenReturn(ingredientResponseClientDto2);

        List<IngredientResponseClientDto> response = productService.getAllIngredientByGroup(GroupIngredient.SAUCE);
        assertFalse(response.isEmpty());
        assertEquals(exsitingList, response);
    }

    @Test
    void getAllIngredientByGroup_EmptyList() {
        List<Ingredient> emptyList = Collections.emptyList();
        when(ingredientRepository.findAllByGroupIngredient(GroupIngredient.SAUCE)).thenReturn(emptyList);
        List<IngredientResponseClientDto> response = productService.getAllIngredientByGroup(GroupIngredient.SAUCE);
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllIngredientForPizza() {
        Pizza pizza = new Pizza();
        pizza.setId(1L);
        pizza.setTitle("Pepperoni");
        Ingredient ingredient1 = new Ingredient(1L, "Cheese", 30, 70, 0.51, GroupIngredient.SAUCE, null);
        Ingredient ingredient2 = new Ingredient(2L, "Tomato", 90, 110, 0.24, GroupIngredient.SAUCE, null);
        List<Ingredient> ingredientList = List.of(ingredient1, ingredient2);
        IngredientResponseClientDto ingredientResponseClientDto1 = new IngredientResponseClientDto(1L, "Cheese", 30, 70, GroupIngredient.SAUCE);
        IngredientResponseClientDto ingredientResponseClientDto2 = new IngredientResponseClientDto(2L, "Tomato", 90, 110, GroupIngredient.SAUCE);

        List<IngredientResponseClientDto> exsitingList = List.of(ingredientResponseClientDto1, ingredientResponseClientDto2);
        pizza.setIngredientsList(ingredientList);
        when(pizzaRepository.getReferenceById(1L)).thenReturn(pizza);
        when(ingredientMapper.toIngredientResponseClientDto(ingredient1)).thenReturn(ingredientResponseClientDto1);
        when(ingredientMapper.toIngredientResponseClientDto(ingredient2)).thenReturn(ingredientResponseClientDto2);
        List<IngredientResponseClientDto> response = productService.getAllIngredientForPizza(1L);

        assertFalse(response.isEmpty());
        assertEquals(exsitingList, response);
    }

    @Test
    void getAllIngredientForPizza_PizzaNotFound() {
        when(pizzaRepository.getReferenceById(1L)).thenReturn(null);
        assertThrows(InvalidIDException.class, () -> productService.getAllIngredientForPizza(1L));
    }
}