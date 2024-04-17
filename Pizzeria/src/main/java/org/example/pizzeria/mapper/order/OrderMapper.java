package org.example.pizzeria.mapper.order;

import org.example.pizzeria.dto.order.OrderDetailsResponseDto;
import org.example.pizzeria.dto.order.OrderResponseDto;
import org.example.pizzeria.dto.order.OrderStatusResponseDto;
import org.example.pizzeria.entity.order.DeliveryAddress;
import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.user.UserApp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", source = "order.id")
    @Mapping(target = "deliveryCity", source = "deliveryAddress.city")
    @Mapping(target = "deliveryStreetName", source = "deliveryAddress.streetName")
    @Mapping(target = "deliveryHouseNumber", source = "deliveryAddress.houseNumber")
    @Mapping(target = "deliveryApartmentNumber", source = "deliveryAddress.apartmentNumber")
    @Mapping(target = "userAppId", source = "order.userApp.id")
    @Mapping(target = "pizzaIdToCount", source = "pizzaIdToCount")
    OrderResponseDto toOrderResponseDto(Order order, DeliveryAddress deliveryAddress,
                                        List<OrderDetailsResponseDto> pizzaIdToCount);

    @Mapping(target = "id", source = "order.id")
    @Mapping(target = "sum", source = "order.sum")
    @Mapping(target = "statusOrder", source = "order.statusOrder")
    @Mapping(target = "orderDateTime", source = "order.orderDateTime")
    @Mapping(target = "userAppId", source = "userAppId")
    OrderStatusResponseDto toOrderStatusResponseDto(Order order, Long userAppId);

    @Mapping(target = "orderDateTime", expression = "java(LocalDateTime.now())")
    @Mapping(target = "deliveryDateTime", expression = "java(LocalDateTime.now().plusHours(1))")
    @Mapping(target = "statusOrder", constant = "NEW")
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "typeBonus", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sum", ignore = true)
    Order toOrder(UserApp userApp, DeliveryAddress deliveryAddress);

}
