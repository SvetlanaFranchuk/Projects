package org.example.pizzeria.service.order;

import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.order.*;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.order.Basket;
import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.order.OrderDetails;
import org.example.pizzeria.entity.order.StatusOrder;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.NotCorrectArgumentException;
import org.example.pizzeria.mapper.order.BasketMapper;
import org.example.pizzeria.mapper.order.OrderDetailsMapper;
import org.example.pizzeria.mapper.order.OrderMapper;
import org.example.pizzeria.mapper.product.PizzaMapper;
import org.example.pizzeria.repository.order.BasketRepository;
import org.example.pizzeria.repository.order.OrderDetailsRepository;
import org.example.pizzeria.repository.order.OrderRepository;
import org.example.pizzeria.repository.product.PizzaRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private BasketRepository basketRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailsRepository orderDetailsRepository;
    @Mock
    private PizzaMapper pizzaMapper;
    @Mock
    private BasketMapper basketMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderDetailsMapper orderDetailsMapper;
    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        Mockito.reset(basketRepository);
        Mockito.reset(pizzaRepository);
        Mockito.reset(userRepository);
        Mockito.reset(orderDetailsRepository);
        Mockito.reset(orderRepository);
    }

    @Test
    void addPizzaToBasket() {
        Long userId = 1L;
        Long pizzaId = 1L;
        int countPizza = 2;
        List<Pizza> pizzas = List.of(TestData.PIZZA, TestData.PIZZA);
        TestData.BASKET.setPizzas(pizzas);
        Map<PizzaResponseDto, Integer> pizzaCountMap = new HashMap<>();
        pizzaCountMap.put(TestData.PIZZA_RESPONSE_DTO, countPizza);

        BasketResponseDto expectedBasketResponseDto = new BasketResponseDto(pizzaCountMap, userId);
        when(pizzaRepository.getReferenceById(pizzaId)).thenReturn(TestData.PIZZA);
        when(basketRepository.findByUserApp_Id(userId)).thenReturn(Optional.of(TestData.BASKET));
        when(basketRepository.save(TestData.BASKET)).thenReturn(TestData.BASKET);
        when(pizzaMapper.mapPizzasToPizzaResponseDtos(anyList())).thenReturn(Collections.emptyList());
        when(basketMapper.toBasketResponseDto(anyMap(), eq(userId))).thenReturn(expectedBasketResponseDto);

        BasketResponseDto result = orderService.addPizzaToBasket(userId, pizzaId, countPizza);
        assertEquals(expectedBasketResponseDto, result);
    }


    @Test
    void addPizzaToBasket_BadArguments_ThrowNotCorrectArgumentException() {
        Long userId = 123L;
        Long pizzaId = 12L;
        int countPizza = -1;
        assertThrows(NotCorrectArgumentException.class, () -> orderService.addPizzaToBasket(userId, pizzaId, countPizza));
    }

    @Test
    void getBasketByUser() {
        Long userId = 1L;
        List<Pizza> pizzas = List.of(TestData.PIZZA);
        TestData.BASKET.setPizzas(pizzas);
        Map<PizzaResponseDto, Integer> pizzaCountMap = new HashMap<>();
        pizzaCountMap.put(TestData.PIZZA_RESPONSE_DTO, 1);
        BasketResponseDto basketResponseDto = new BasketResponseDto(pizzaCountMap, userId);

        when(basketRepository.findByUserApp_Id(userId)).thenReturn(Optional.of(TestData.BASKET));
        when(pizzaMapper.mapPizzasToPizzaResponseDtos(pizzas))
                .thenReturn(Collections.singletonList(TestData.PIZZA_RESPONSE_DTO));
        when(basketMapper.toBasketResponseDto(pizzaCountMap, userId)).thenReturn(basketResponseDto);
        BasketResponseDto result = orderService.getBasketByUser(userId);
        assertEquals(basketResponseDto, result);
    }

    @Test
    void getBasketByUser_WhenBasketNotFound_TrowEntityInPizzeriaNotFoundException() {
        Long userId = 1L;
        when(basketRepository.findByUserApp_Id(userId)).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.getBasketByUser(userId));
        verifyNoMoreInteractions(pizzaMapper);
        verifyNoMoreInteractions(basketMapper);
    }

    @Test
    void changePizzasInBasket() {
        Long userId = 1L;
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 2);
        BasketRequestDto request = new BasketRequestDto(pizzaToCount, userId);
        List<Pizza> oldPizzas = List.of(TestData.PIZZA, TestData.PIZZA, TestData.PIZZA);
        TestData.BASKET.setPizzas(oldPizzas);
        BasketResponseDto expected = new BasketResponseDto(pizzaToCount, userId);

        when(basketRepository.findByUserApp_Id(userId)).thenReturn(Optional.of(TestData.BASKET));
        when(pizzaRepository.getReferenceById(TestData.PIZZA.getId())).thenReturn(TestData.PIZZA);
        when(basketRepository.save(TestData.BASKET)).thenReturn(TestData.BASKET);
        when(basketMapper.toBasketResponseDto(pizzaToCount, userId)).thenReturn(expected);
        BasketResponseDto result = orderService.changePizzasInBasket(request);
        verify(basketRepository, times(1)).save(TestData.BASKET);
        assertEquals(expected, result);
    }

    @Test
    void changePizzasInBasket_BasketNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long userId = 1L;
        BasketRequestDto request = new BasketRequestDto(Collections.emptyMap(), userId);
        when(basketRepository.findByUserApp_Id(userId)).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.changePizzasInBasket(request));
    }

    @Test
    void changePizzasInBasket_NegativePizzaCount_ThrowNotCorrectArgumentException() {
        Long userId = 1L;
        when(basketRepository.findByUserApp_Id(userId)).thenReturn(Optional.of(TestData.BASKET));

        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, -1);
        BasketRequestDto request = new BasketRequestDto(pizzaToCount, userId);
        assertThrows(NotCorrectArgumentException.class, () -> orderService.changePizzasInBasket(request));
    }

    @Test
    void moveDetailsBasketToOrder() {
        Long userId = 1L;
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        when(userRepository.findById(userId)).thenReturn(Optional.of(TestData.USER_APP));
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 2);
        BasketRequestDto basketRequestDto = new BasketRequestDto(pizzaToCount, userId);
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        TestData.ORDER_DETAILS.setDeliveryDateTime(expectedDateTime);
        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(TestData.PIZZA));
        when(orderDetailsMapper.toOrderDetails(any(LocalDateTime.class), anyList()))
                .thenReturn(TestData.ORDER_DETAILS);
        when(orderDetailsRepository.save(TestData.ORDER_DETAILS)).thenReturn(TestData.ORDER_DETAILS);
        TestData.ORDER.setOrderDetail(TestData.ORDER_DETAILS);
        OrderResponseDto orderResponseDto = new OrderResponseDto(
                1L,
                TestData.DELIVERY_ADDRESS.getCity(),
                TestData.DELIVERY_ADDRESS.getStreetName(),
                TestData.DELIVERY_ADDRESS.getHouseNumber(),
                TestData.DELIVERY_ADDRESS.getApartmentNumber(),
                expectedDateTime,
                (0.2 + 0.4 + 0.23) * 1.3 * 2,
                StatusOrder.NEW,
                LocalDateTime.now(),
                pizzaToCount,
                userId
        );
        when(orderRepository.save(any(Order.class))).thenReturn(TestData.ORDER);
        when(basketRepository.findByUserApp_Id(userId)).thenReturn(Optional.of(TestData.BASKET));
        when(orderMapper.toOrderResponseDto(eq(TestData.ORDER), eq(TestData.DELIVERY_ADDRESS), eq(TestData.ORDER_DETAILS), eq(pizzaToCount)))
                .thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.moveDetailsBasketToOrder(basketRequestDto);

        assertNotNull(result);
        assertEquals(StatusOrder.NEW, result.statusOrder());
        assertNotNull(result.orderDateTime());
        assertNotNull(result.pizzaToCount());
        assertEquals(expectedSum, result.sum());
        verify(basketRepository, times(1)).save(any(Basket.class));
    }

    @Test
    void moveDetailsBasketToOrder_UserNotFound_ThrowEntityInPizzeriaNotFoundException() {
        BasketRequestDto basketRequestDto = new BasketRequestDto(new HashMap<>(), 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.moveDetailsBasketToOrder(basketRequestDto));
    }

    @Test
    void moveDetailsBasketToOrder_PizzaNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long userId = 1L;
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 2);
        BasketRequestDto basketRequestDto = new BasketRequestDto(pizzaToCount, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(TestData.USER_APP));
        when(pizzaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.moveDetailsBasketToOrder(basketRequestDto));
    }

    @Test
    void updateOrderDetails() {
        Long orderId = 1L;
        Long userID = 1L;
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 2);
        OrderRequestDto orderRequestDto = new OrderRequestDto(orderId,
                "", "test", "10b", "",
                expectedDateTime, pizzaToCount, userID);
        TestData.ORDER.setDeliveryAddress(TestData.DELIVERY_ADDRESS_NEW);
        TestData.ORDER.setOrderDetail(TestData.ORDER_DETAILS);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(TestData.ORDER));
        TestData.ORDER_DETAILS.setDeliveryDateTime(expectedDateTime);
        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(TestData.PIZZA));
        when(userRepository.findById(1L)).thenReturn(Optional.of(TestData.USER_APP));
        when(orderDetailsRepository.save(any(OrderDetails.class))).thenReturn(TestData.ORDER_DETAILS);
        when(orderRepository.save(any(Order.class))).thenReturn(TestData.ORDER);
        OrderResponseDto orderResponseDto = new OrderResponseDto(
                1L,
                TestData.DELIVERY_ADDRESS_NEW.getCity(),
                TestData.DELIVERY_ADDRESS_NEW.getStreetName(),
                TestData.DELIVERY_ADDRESS_NEW.getHouseNumber(),
                TestData.DELIVERY_ADDRESS_NEW.getApartmentNumber(),
                expectedDateTime,
                (0.2 + 0.4 + 0.23) * 1.3 * 2,
                StatusOrder.NEW,
                LocalDateTime.now(),
                pizzaToCount,
                1L
        );
        when(orderMapper.toOrderResponseDto(TestData.ORDER, TestData.DELIVERY_ADDRESS_NEW, TestData.ORDER_DETAILS, pizzaToCount)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.updateOrderDetails(orderRequestDto);
        assertNotNull(result);
        assertEquals(StatusOrder.NEW, result.statusOrder());
        assertNotNull(result.orderDateTime());
        assertNotNull(result.pizzaToCount());
        assertEquals(expectedSum, result.sum());
    }

    @Test
    void updateOrderDetails_OrderNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long userID = 1L;
        Long orderId = 999L;
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 2);
        OrderRequestDto orderRequestDto = new OrderRequestDto(orderId,
                "", "test", "10b", "",
                expectedDateTime, pizzaToCount, userID);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.updateOrderDetails(orderRequestDto));
    }

    @Test
    void deleteOrder() {
        Long orderId = 2L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatusOrder(StatusOrder.NEW);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        assertDoesNotThrow(() -> orderService.deleteOrder(orderId));
        verify(orderRepository).findById(orderId);
        verify(orderDetailsRepository).delete(order.getOrderDetail());
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_OrderNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.deleteOrder(orderId));
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void updateStatusOrder() {
        Long orderId = 1L;
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(TestData.ORDER));
        StatusOrder newStatus = StatusOrder.PAID;
        TestData.ORDER.setStatusOrder(newStatus);
        when(orderRepository.save(TestData.ORDER)).thenReturn(TestData.ORDER);
        OrderStatusResponseDto orderStatusResponseDto = new OrderStatusResponseDto(1L, expectedSum,
                newStatus, LocalDateTime.now(), 1L);
        when(orderMapper.toOrderStatusResponseDto(TestData.ORDER)).thenReturn(orderStatusResponseDto);
        OrderStatusResponseDto updatedOrderStatus = orderService.updateStatusOrder(orderId, newStatus);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(TestData.ORDER);
        assertEquals(newStatus, updatedOrderStatus.statusOrder());
    }

    @Test
    void updateStatusOrder_OrderNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.updateStatusOrder(orderId, StatusOrder.PAID));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getAllOrdersByUser() {
        Long userId = 1L;
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(TestData.USER_APP));
        when(orderRepository.findAllByUserApp(TestData.USER_APP)).thenReturn(List.of(TestData.ORDER));
        when(orderRepository.getReferenceById(TestData.ORDER.getId())).thenReturn(TestData.ORDER);
        TestData.ORDER.setOrderDetail(TestData.ORDER_DETAILS);
        when(pizzaMapper.mapPizzasToPizzaResponseDtos(List.of(TestData.PIZZA, TestData.PIZZA)))
                .thenReturn(List.of(TestData.PIZZA_RESPONSE_DTO, TestData.PIZZA_RESPONSE_DTO));
        OrderResponseDto expected = new OrderResponseDto(
                1L,
                TestData.DELIVERY_ADDRESS.getCity(),
                TestData.DELIVERY_ADDRESS.getStreetName(),
                TestData.DELIVERY_ADDRESS.getHouseNumber(),
                TestData.DELIVERY_ADDRESS.getApartmentNumber(),
                expectedDateTime,
                (0.2 + 0.4 + 0.23) * 1.3 * 2,
                StatusOrder.NEW,
                LocalDateTime.now(),
                pizzaToCount,
                userId
        );
        when(orderMapper.toOrderResponseDto(TestData.ORDER, TestData.DELIVERY_ADDRESS, TestData.ORDER_DETAILS, pizzaToCount)).thenReturn(expected);
        List<OrderResponseDto> orderResponseDtoList = orderService.getAllOrdersByUser(userId);
        assertEquals(List.of(expected), orderResponseDtoList);
    }

    @Test
    void getAllOrdersByUser_UserNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.getAllOrdersByUser(userId));
        verify(orderRepository, never()).findAllByUserApp(any());
    }

    @Test
    void getOrderByUser() {
        Long userId = 1L;
        Long orderId = 1L;
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 2);
        when(orderRepository.getReferenceById(orderId)).thenReturn(TestData.ORDER);
        TestData.ORDER.setOrderDetail(TestData.ORDER_DETAILS);
        when(pizzaMapper.mapPizzasToPizzaResponseDtos(TestData.ORDER.getOrderDetail().getPizzas()))
                .thenReturn(List.of(TestData.PIZZA_RESPONSE_DTO, TestData.PIZZA_RESPONSE_DTO));
        OrderResponseDto expectedOrderResponseDto = new OrderResponseDto(
                1L,
                TestData.DELIVERY_ADDRESS.getCity(),
                TestData.DELIVERY_ADDRESS.getStreetName(),
                TestData.DELIVERY_ADDRESS.getHouseNumber(),
                TestData.DELIVERY_ADDRESS.getApartmentNumber(),
                expectedDateTime,
                (0.2 + 0.4 + 0.23) * 1.3 * 2,
                StatusOrder.NEW,
                LocalDateTime.now(),
                pizzaToCount,
                userId
        );
        when(orderMapper.toOrderResponseDto(any(), any(), any(), any())).thenReturn(expectedOrderResponseDto);
        OrderResponseDto result = orderService.getOrderByUser(orderId);
        verify(orderRepository).getReferenceById(orderId);
        verify(orderMapper).toOrderResponseDto(TestData.ORDER, TestData.DELIVERY_ADDRESS, TestData.ORDER_DETAILS, pizzaToCount);
        assertNotNull(result);
        assertEquals(expectedOrderResponseDto, result);
    }

    @Test
    void getOrderByUser_OrderNotFound_ThrowNullPointerException() {
        Long orderId = 1L;
        when(orderRepository.getReferenceById(orderId)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> orderService.getOrderByUser(orderId));
        verify(orderRepository).getReferenceById(orderId);
        verifyNoMoreInteractions(orderMapper);
    }

    @Test
    void getOrderByStatus() {
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        StatusOrder statusOrder = StatusOrder.NEW;
        List<Order> orders = List.of(TestData.ORDER);
        when(orderRepository.findAllByStatusOrder(statusOrder)).thenReturn(orders);
        OrderStatusResponseDto orderStatusResponseDto = new OrderStatusResponseDto(1L, expectedSum,
                statusOrder, LocalDateTime.now(), 1L);
        when(orderMapper.toOrderStatusResponseDto(TestData.ORDER)).thenReturn(orderStatusResponseDto);
        List<OrderStatusResponseDto> actualResponse = orderService.getOrderByStatus(statusOrder);
        assertEquals(List.of(orderStatusResponseDto), actualResponse);
    }

    @Test
    void getOrderByStatus_NoOrdersFound_ThrowEntityInPizzeriaNotFoundException() {
        StatusOrder statusOrder = StatusOrder.CANCELED;
        when(orderRepository.findAllByStatusOrder(statusOrder)).thenReturn(Collections.emptyList());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.getOrderByStatus(statusOrder));
        verifyNoMoreInteractions(orderMapper);
    }

    @Test
    void getAllOrdersByPeriod() {
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<Order> orders = List.of(TestData.ORDER);
        OrderStatusResponseDto orderStatusResponseDto = new OrderStatusResponseDto(1L, expectedSum,
                StatusOrder.NEW, LocalDateTime.now(), 1L);

        when(orderRepository.findAllByOrderDateTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(orders);
        when(orderMapper.toOrderStatusResponseDto(TestData.ORDER)).thenReturn(orderStatusResponseDto);
        List<OrderStatusResponseDto> result = orderService.getAllOrdersByPeriod(startDate, endDate);
        assertEquals(List.of(orderStatusResponseDto), result);
    }

    @Test
    void getAllOrdersByPeriod_DateMissed_ThrowNotCorrectArgumentException() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(7);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        assertThrows(NotCorrectArgumentException.class, () -> orderService.getAllOrdersByPeriod(startDate, endDate));
        verify(orderRepository, never()).findAllByOrderDateTimeBetween(startDateTime, endDateTime);
    }
}