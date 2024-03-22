package org.example.pizzeria.mapper.order;

import org.example.pizzeria.entity.order.OrderDetails;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderDetailsMapper {

    @Mapping(target = "typeBonus", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderDetails toOrderDetails(LocalDateTime deliveryDateTime, List<Pizza> pizzas);
}
