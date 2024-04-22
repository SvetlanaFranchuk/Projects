package org.example.pizzeria.service.product;

import org.example.pizzeria.dto.product.ingredient.IngredientRequestDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseDto;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;

import java.util.List;

public interface IngredientService {

    IngredientResponseDto add(IngredientRequestDto newIngredient);

    IngredientResponseDto update(IngredientRequestDto ingredient, Long id);

    void delete(Long id);

    List<IngredientResponseDto> getAllForAdmin();

    List<IngredientResponseDto> getAllByGroup(GroupIngredient groupIngredient);

    List<IngredientResponseClientDto> getAllForPizza(Long idPizza);

}


