package org.example.pizzeria.mapper.order;

import org.example.pizzeria.dto.order.BasketResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.mapstruct.Mapper;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface BasketMapper {
    BasketResponseDto toBasketResponseDto(Map<PizzaResponseDto, Integer> pizzaCountMap, Long userId);
}
