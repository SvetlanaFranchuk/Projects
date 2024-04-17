package org.example.pizzeria.mapper.product;

import org.example.pizzeria.dto.product.pizza.PizzaRequestDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PizzaMapper {
    @Mapping(target = "id", source = "pizza.id")
    PizzaResponseDto toPizzaResponseDto(Pizza pizza);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "standardRecipe", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "nutrition", ignore = true)
    @Mapping(target = "dough", ignore = true)
    @Mapping(target = "ingredientsList", ignore = true)
    Pizza toPizza(PizzaRequestDto newPizza);

    default List<PizzaResponseDto> mapPizzasToPizzaResponseDtos(List<Pizza> pizzas) {
        return pizzas.stream()
                .map(this::toPizzaResponseDto)
                .toList();
    }
}
