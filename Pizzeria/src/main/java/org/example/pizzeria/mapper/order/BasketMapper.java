package org.example.pizzeria.mapper.order;

import org.example.pizzeria.dto.order.BasketRequestDto;
import org.example.pizzeria.dto.order.BasketResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.order.Basket;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.user.UserApp;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface BasketMapper {
    Basket toBasket(UserApp userApp, List<Pizza> pizzas);

    BasketResponseDto toBasketResponseDto(Map<PizzaResponseDto, Integer> pizzaCountMap, Long userId);
}
