package org.example.pizzeria.mapper.product;

import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseDto;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface IngredientMapper {

    IngredientResponseDto toIngredientResponseDto(Ingredient ingredient);
    IngredientResponseClientDto toIngredientResponseClientDto(Ingredient ingredient);
    Ingredient toIngredient(String name, int weight,int nutrition, double price, GroupIngredient groupIngredient, Set<Pizza> pizzaSet);

}
