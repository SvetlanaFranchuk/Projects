package org.example.pizzeria.mapper.product;

import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.pizza.PizzaRequestDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PizzaMapper {
    @Autowired
    DoughMapper doughMapper = null;
    @Mapping(target = "id", source = "pizza.id")
    @Mapping(target = "dough", expression = "java(doughMapper.toDoughResponseClientDto(pizza.getDough()))")
    @Mapping(target = "ingredientsList", source = "pizza")
    PizzaResponseDto toPizzaResponseDto(Pizza pizza);


    @Mapping(target = "dough", expression = "java(doughMapper.toDough(newPizza.dough()))")
    Pizza toPizza(PizzaRequestDto newPizza);

    default List<IngredientResponseClientDto> mapIngredientsList(Pizza pizza) {
        IngredientMapper ingredientMapper = null;
        return pizza.getIngredientsList().stream()
                .map(ingredientMapper::toIngredientResponseClientDto)
                .toList();
    }

    default List<PizzaResponseDto> mapPizzasToPizzaResponseDtos(List<Pizza> pizzas) {
        return pizzas.stream()
                .map(this::toPizzaResponseDto)
                .toList();
    }
}