package org.example.pizzeria.mapper.product;

import jakarta.annotation.Nullable;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.entity.product.pizza.TypeBySize;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PizzaMapper {

    @Mapping(target = "id", source = "pizza.id")
    PizzaResponseDto toPizzaResponseDto(Pizza pizza, DoughResponseClientDto dough, List<IngredientResponseClientDto> ingredientsList);

    Pizza toPizza(String title, String description, Styles styles, ToppingsFillings toppingsFillings,
                  TypeBySize size, boolean isStandardRecipe, double amount, int nutrition, Dough dough,
                  List<Ingredient> ingredientsList);
    default List<PizzaResponseDto> mapPizzasToPizzaResponseDtos(List<Pizza> pizzas, DoughMapper doughMapper, IngredientMapper ingredientMapper) {
        return pizzas.stream()
                .map(p -> toPizzaResponseDto(
                        p,
                        doughMapper.toDoughResponseClientDto(p.getDough()),
                        p.getIngredientsList().stream()
                                .map(ingredientMapper::toIngredientResponseClientDto)
                                .toList()))
                .toList();
    }
}
