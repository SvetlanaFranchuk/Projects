package org.example.pizzeria.service.product;

import org.example.pizzeria.dto.product.pizza.PizzaRequestDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;

import java.util.List;

public interface PizzaService {

    PizzaResponseDto add(PizzaRequestDto newPizza, Long userId);

    PizzaResponseDto update(PizzaRequestDto pizza, Long id);

    void deletePizzaRecipe(Long id);

    PizzaResponseDto getPizza(Long id);

    List<PizzaResponseDto> getAllPizzaStandardRecipe();

    List<PizzaResponseDto> getAllPizzaStandardRecipeByStyles(Styles styles);

    List<PizzaResponseDto> getAllPizzaStandardRecipeByTopping(ToppingsFillings toppingsFillings);

    List<PizzaResponseDto> getAllPizzaStandardRecipeByToppingByStyles(ToppingsFillings toppingsFillings, Styles styles);

}


