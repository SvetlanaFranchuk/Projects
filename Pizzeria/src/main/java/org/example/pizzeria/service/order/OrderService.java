package org.example.pizzeria.service.order;

import org.example.pizzeria.dto.order.*;
import org.example.pizzeria.entity.order.StatusOrder;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    BasketResponseDto addPizzaToBasket(Long userId, Long pizzaId, int countPizza);
    BasketResponseDto changePizzasInBasket(BasketRequestDto request);
    BasketResponseDto getBasketByUser(Long userId);
    OrderResponseDto moveDetailsBasketToOrder(BasketRequestDto basketRequestDto);
    OrderResponseDto updateOrderDetails(OrderRequestDto orderRequestDto);
    void deleteOrder(Long id);
    OrderStatusResponseDto updateStatusOrder(Long orderId, StatusOrder statusOrder);
    List<OrderResponseDto> getAllOrdersByUser(Long userId);
    OrderResponseDto getOrderByUser(Long orderId);
    List<OrderStatusResponseDto> getOrderByStatus(StatusOrder statusOrder);
    List<OrderStatusResponseDto> getAllOrdersByPeriod(LocalDate startDate, LocalDate endDate);

}
