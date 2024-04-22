package org.example.pizzeria.mapper.order;

import org.example.pizzeria.dto.order.OrderDetailsResponseDto;
import org.example.pizzeria.entity.order.OrderDetails;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderDetailsMapper {
    @Mapping(target = "count", constant = "1")
    OrderDetailsResponseDto pizzaToOrderDetailsDto(Pizza pizza);

    List<OrderDetailsResponseDto> pizzasToOrderDetailsDtoList(List<Pizza> pizzas);

    @Mapping(target = "pizzaId", source = "pizza.id")
    @Mapping(target = "count", source = "quantity")
    OrderDetailsResponseDto orderDetailsToOrderDetailsResponseDto(OrderDetails orderDetails);

    default List<OrderDetailsResponseDto> orderDetailsListToOrderDetailsResponseDtoList(List<OrderDetails> orderDetailsList) {
        return orderDetailsList.stream()
                .map(this::orderDetailsToOrderDetailsResponseDto)
                .collect(Collectors.toList());
    }
}
