package org.example.pizzeria.mapper.order;

import org.example.pizzeria.dto.order.OrderResponseDto;
import org.example.pizzeria.dto.order.OrderStatusResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.order.DeliveryAddress;
import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.order.OrderDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", source = "order.id")
    @Mapping(target = "deliveryCity", source = "deliveryAddress.city")
    @Mapping(target = "deliveryStreetName", source = "deliveryAddress.streetName")
    @Mapping(target = "deliveryHouseNumber", source = "deliveryAddress.houseNumber")
    @Mapping(target = "deliveryApartmentNumber", source = "deliveryAddress.apartmentNumber")
    @Mapping(target = "userAppId", source = "order.userApp.id")
    OrderResponseDto toOrderResponseDto(Order order, DeliveryAddress deliveryAddress, OrderDetails orderDetails,
                                        Map<PizzaResponseDto, Integer> pizzaToCount);

    OrderStatusResponseDto toOrderStatusResponseDto(Order order);
}
