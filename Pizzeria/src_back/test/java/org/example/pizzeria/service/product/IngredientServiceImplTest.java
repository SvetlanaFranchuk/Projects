package org.example.pizzeria.service.product;

import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.product.ingredient.IngredientRequestDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseDto;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private IngredientMapper ingredientMapper;
    @InjectMocks
    private IngredientServiceImpl ingredientService;

    @BeforeEach
    void setUp() {
        Mockito.reset(ingredientRepository);
        Mockito.reset(pizzaRepository);
    }

    @Test
    void add() {
        Ingredient ingredient = new Ingredient(1L, "Tomato", 100, 120, 0.23, GroupIngredient.EXTRA, null);
        when(ingredientMapper.toIngredient(TestData.INGREDIENT_REQUEST_DTO.name(), TestData.INGREDIENT_REQUEST_DTO.weight(),
                TestData.INGREDIENT_REQUEST_DTO.nutrition(), TestData.INGREDIENT_REQUEST_DTO.price(), TestData.INGREDIENT_REQUEST_DTO.groupIngredient(),
                new HashSet<>())).thenReturn(ingredient);
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);
        when(ingredientMapper.toIngredientResponseDto(ingredient)).thenReturn(TestData.INGREDIENT_RESPONSE_DTO);

        IngredientResponseDto result = ingredientService.add(TestData.INGREDIENT_REQUEST_DTO);
        assertEquals(TestData.INGREDIENT_RESPONSE_DTO, result);
    }

    @Test
    void add_DuplicateDoughTypeAndPrice_ThrowIngredientsCreateException() {
        Ingredient ingredient = new Ingredient(1L, "Tomato", 100, 120, 0.23, GroupIngredient.EXTRA, null);
        when(ingredientRepository.findAllByNameAndPrice("Tomato", 0.23))
                .thenReturn(Collections.singletonList(ingredient));
        assertThrows(IngredientsCreateException.class, () -> ingredientService.add(TestData.INGREDIENT_REQUEST_DTO));
    }


    @Test
    void update() {
        Ingredient existingIngredient = new Ingredient(1L, "Tomato", 100, 120, 0.23, GroupIngredient.EXTRA, null);
        Ingredient newIngredient = new Ingredient(1L, "Tomato", 110, 120, 0.25, GroupIngredient.EXTRA, null);
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(existingIngredient));
        when(ingredientRepository.save(newIngredient)).thenReturn(newIngredient);
        when(ingredientMapper.toIngredientResponseDto(newIngredient)).thenReturn(TestData.INGREDIENT_RESPONSE_DTO_NEW);
        IngredientResponseDto responseDto = ingredientService.update(TestData.INGREDIENT_REQUEST_DTO_NEW, 1L);
        assertEquals(TestData.INGREDIENT_RESPONSE_DTO_NEW, responseDto);
    }

    @Test
    void update_ThrowInvalidID() {
        IngredientRequestDto updatedIngredient = new IngredientRequestDto("Tomato", 110, 120, 0.25, GroupIngredient.BASIC);
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> ingredientService.update(updatedIngredient, null));
    }

    @Test
    void delete() {
        Ingredient newIngredient = new Ingredient(1L, "Tomato", 110, 120, 0.25, GroupIngredient.EXTRA, null);
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(newIngredient));
        when(pizzaRepository.findAllByIngredientsListContaining(newIngredient)).thenReturn(Collections.emptyList());
        ingredientService.delete(1L);
    }

    @Test
    void delete_IngredientNotFound_ThrowInvalidIDException() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(InvalidIDException.class, () -> ingredientService.delete(1L));
    }

    @Test
    void delete_IngredientInUse() {
        Ingredient newIngredient = new Ingredient(1L, "Tomato", 110, 120, 0.25, GroupIngredient.EXTRA, null);
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(newIngredient));
        when(pizzaRepository.findAllByIngredientsListContaining(newIngredient)).thenReturn(Collections.singletonList(new Pizza()));
        assertThrows(DeleteProductException.class, () -> ingredientService.delete(1L));
    }

    @Test
    void getAllForAdmin() {
        List<Ingredient> ingredientList = Arrays.asList(TestData.INGREDIENT_1, TestData.INGREDIENT_2);
        when(ingredientRepository.findAll()).thenReturn(ingredientList);
        List<IngredientResponseDto> response = ingredientService.getAllForAdmin();
        assertFalse(response.isEmpty());
        assertEquals(ingredientList.size(), response.size());
        assertEquals(ingredientList.get(0).getName(), "Tomato");
        assertEquals(ingredientList.get(0).getGroupIngredient(), GroupIngredient.BASIC);
        assertEquals(ingredientList.get(1).getName(), "Cheese");
        assertEquals(ingredientList.get(1).getGroupIngredient(), GroupIngredient.BASIC);
    }

    @Test
    void getAllForAdmin_EmptyList() {
        List<Ingredient> emptyList = Collections.emptyList();
        when(ingredientRepository.findAll()).thenReturn(emptyList);
        List<IngredientResponseDto> response = ingredientService.getAllForAdmin();
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllByGroup() {
        Ingredient ingredient1 = new Ingredient(1L, "Cheese", 30, 70, 0.51, GroupIngredient.SAUCE, null);
        Ingredient ingredient2 = new Ingredient(2L, "Tomato", 90, 110, 0.24, GroupIngredient.SAUCE, null);
        List<Ingredient> ingredientList = List.of(ingredient1, ingredient2);
        IngredientResponseDto ingredientResponseDto1 = new IngredientResponseDto(1L, "Cheese", 30, 70, 0.51, GroupIngredient.SAUCE);
        IngredientResponseDto ingredientResponseDto2 = new IngredientResponseDto(2L, "Tomato", 90, 110, 0.24, GroupIngredient.SAUCE);
        List<IngredientResponseDto> exsitingList = List.of(ingredientResponseDto1, ingredientResponseDto2);
        when(ingredientRepository.findAllByGroupIngredient(GroupIngredient.SAUCE)).thenReturn(ingredientList);
        when(ingredientMapper.toIngredientResponseDto(ingredient1)).thenReturn(ingredientResponseDto1);
        when(ingredientMapper.toIngredientResponseDto(ingredient2)).thenReturn(ingredientResponseDto2);

        List<IngredientResponseDto> response = ingredientService.getAllByGroup(GroupIngredient.SAUCE);
        assertFalse(response.isEmpty());
        assertEquals(exsitingList, response);
    }

    @Test
    void getAllByGroup_EmptyList() {
        List<Ingredient> emptyList = Collections.emptyList();
        when(ingredientRepository.findAllByGroupIngredient(GroupIngredient.SAUCE)).thenReturn(emptyList);
        List<IngredientResponseDto> response = ingredientService.getAllByGroup(GroupIngredient.SAUCE);
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
        List<IngredientResponseClientDto> response = ingredientService.getAllForPizza(1L);

        assertFalse(response.isEmpty());
        assertEquals(exsitingList, response);
    }

    @Test
    void getAllIngredientForPizza_PizzaNotFound_ThrowNullPointerException() {
        when(pizzaRepository.getReferenceById(1L)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> ingredientService.getAllForPizza(1L));
    }
}