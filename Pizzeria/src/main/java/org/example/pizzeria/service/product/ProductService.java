package org.example.pizzeria.service.product;

import org.example.pizzeria.dto.benefits.FavoritesResponseDto;
import org.example.pizzeria.dto.product.dough.DoughRequestDto;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.dto.product.ingredient.IngredientRequestDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaRequestDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;

import java.util.List;

public interface ProductService {

    DoughResponseDto addDough(DoughRequestDto newDough);

    DoughResponseDto updateDough(DoughRequestDto dough, Integer id);

    void deleteDough(Integer id);

    List<DoughResponseDto> getAllDoughForAdmin();

    List<DoughResponseClientDto> getAllDoughForClient();

    IngredientResponseDto addIngredient(IngredientRequestDto newIngredient);

    IngredientResponseDto updateIngredient(IngredientRequestDto ingredient, Long id);

    void deleteIngredient(Long id);

    List<IngredientResponseDto> getAllIngredientForAdmin();

    List<IngredientResponseClientDto> getAllIngredientByGroup(GroupIngredient groupIngredient);

    List<IngredientResponseClientDto> getAllIngredientForPizza(Long idPizza);

    PizzaResponseDto addPizza(PizzaRequestDto newPizza, Long userId);

    PizzaResponseDto updatePizza(PizzaRequestDto pizza, Long id);

    void deletePizzaRecipe(Long id);

    List<PizzaResponseDto> getAllPizzaStandardRecipe();

    List<PizzaResponseDto> getAllPizzaStandardRecipeByStyles(Styles styles);

    List<PizzaResponseDto> getAllPizzaStandardRecipeByTopping(ToppingsFillings toppingsFillings);

    List<PizzaResponseDto> getAllPizzaStandardRecipeByToppingByStyles(ToppingsFillings toppingsFillings, Styles styles);

    FavoritesResponseDto addPizzaToUserFavorite(Long userId, Pizza pizza);

    void deletePizzaFromUserFavorite(Long pizzaId, Long userId);

    List<PizzaResponseDto> getAllFavoritePizzaByUser(Long userId);
}


