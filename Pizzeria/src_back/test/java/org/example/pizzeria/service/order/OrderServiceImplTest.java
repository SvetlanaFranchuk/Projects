package org.example.pizzeria.service.order;

import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.order.*;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaToBasketRequestDto;
import org.example.pizzeria.entity.order.Basket;
import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.order.OrderDetails;
import org.example.pizzeria.entity.order.StatusOrder;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.NotCorrectArgumentException;
import org.example.pizzeria.exception.order.InvalidOrderStatusException;
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
        List<Pizza> pizzas = new ArrayList<>(List.of(TestData.PIZZA, TestData.PIZZA));
        TestData.BASKET.setPizzas(pizzas);
        Map<PizzaResponseDto, Integer> pizzaCountMap = new HashMap<>();
        pizzaCountMap.put(TestData.PIZZA_RESPONSE_DTO, countPizza);

        BasketResponseDto expectedBasketResponseDto = new BasketResponseDto(pizzaCountMap, userId);
        when(pizzaRepository.getReferenceById(pizzaId)).thenReturn(TestData.PIZZA);
        when(basketRepository.findByUserApp_Id(userId)).thenReturn(Optional.of(TestData.BASKET));
        when(basketRepository.save(TestData.BASKET)).thenReturn(TestData.BASKET);
        when(pizzaMapper.mapPizzasToPizzaResponseDtos(anyList())).thenReturn(Collections.emptyList());
        when(basketMapper.toBasketResponseDto(anyMap(), eq(userId))).thenReturn(expectedBasketResponseDto);

        BasketResponseDto result = orderService.addPizzaToBasket(userId, new PizzaToBasketRequestDto(pizzaId, countPizza));
        assertEquals(expectedBasketResponseDto, result);
    }

    @Test
    void addPizzaToBasket_BadArguments_ThrowNotCorrectArgumentException() {
        Long userId = 123L;
        Long pizzaId = 12L;
        int countPizza = -1;
        assertThrows(NotCorrectArgumentException.class, () -> orderService.addPizzaToBasket(userId, new PizzaToBasketRequestDto(pizzaId, countPizza)));
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
        Map<Long, Integer> pizzaRequestToCount = new HashMap<>();
        pizzaRequestToCount.put(1L, 3);
        BasketRequestDto request = new BasketRequestDto(pizzaRequestToCount, userId);
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 3);
        BasketResponseDto expected = new BasketResponseDto(pizzaToCount, userId);

        when(basketRepository.findByUserApp_Id(userId)).thenReturn(Optional.of(TestData.BASKET));
        when(pizzaRepository.getReferenceById(TestData.PIZZA.getId())).thenReturn(TestData.PIZZA);
        when(basketRepository.save(TestData.BASKET)).thenReturn(TestData.BASKET);
        when(pizzaMapper.toPizzaResponseDto(TestData.PIZZA)).thenReturn(TestData.PIZZA_RESPONSE_DTO);
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

        Map<Long, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO.getId(), -1);
        BasketRequestDto request = new BasketRequestDto(pizzaToCount, userId);
        assertThrows(NotCorrectArgumentException.class, () -> orderService.changePizzasInBasket(request));
    }

    @Test
    void moveDetailsBasketToOrder() {
        Long basketId = 1L;
        Order newOrder = new Order();
        newOrder.setOrderDateTime(TestData.NOW);
        newOrder.setDeliveryDateTime(TestData.EXPECTED_DATE_TIME);
        Basket basket = TestData.BASKET;
        List<Pizza> pizzas = new ArrayList<>();
        pizzas.add(TestData.PIZZA);
        pizzas.add(TestData.PIZZA);
        basket.setPizzas(pizzas);
        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basket));
        when(userRepository.findById(any())).thenReturn(Optional.of(TestData.USER_APP));
        when(orderMapper.toOrder(any(), any())).thenReturn(newOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(newOrder);

        OrderDetails orderDetails =new OrderDetails();
        orderDetails.setOrder(newOrder);
        orderDetails.setPizza(TestData.PIZZA);
        orderDetails.setQuantity(2);
        when(orderDetailsRepository.save(any(OrderDetails.class))).thenReturn(orderDetails);
        newOrder.addOrderDetails(orderDetails);

        OrderResponseDto expectedOrderResponseDto = TestData.ORDER_RESPONSE_DTO;

        UserApp userApp = TestData.USER_APP;
        when(basketRepository.save(TestData.BASKET)).thenReturn(TestData.BASKET);
        userApp.addOrder(newOrder);
        when(userRepository.save(any(UserApp.class))).thenReturn(userApp);
        when(orderDetailsMapper.orderDetailsListToOrderDetailsResponseDtoList(anyList())).thenReturn(List.of(TestData.ORDER_DETAILS_RESPONSE_DTO));
        when(orderMapper.toOrderResponseDto(any(Order.class), any(), any())).thenReturn(expectedOrderResponseDto);

        OrderResponseDto result = orderService.moveDetailsBasketToOrder(basketId);
        assertNotNull(result);
        assertEquals(expectedOrderResponseDto, result);
    }

    @Test
    void moveDetailsBasketToOrder_UserNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long basketId = 1L;
        Basket basket = TestData.BASKET;
        basket.setPizzas(List.of(new Pizza()));
        basket.setUserApp(new UserApp());
        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basket));
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.moveDetailsBasketToOrder(basketId));
    }

    @Test
    void moveDetailsBasketToOrder_InvalidBasketId_ThrowEntityInPizzeriaNotFoundException() {
        Long invalidBasketId = 999L;
        when(basketRepository.findById(invalidBasketId)).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.moveDetailsBasketToOrder(invalidBasketId));
    }

    @Test
    void moveDetailsBasketToOrder_BasketIsEmpty_ThrowEntityInPizzeriaNotFoundException() {
        Long basketId = 1L;
        Basket emptyBasket = new Basket();
        emptyBasket.setPizzas(new ArrayList<>());
        when(basketRepository.findById(basketId)).thenReturn(Optional.of(emptyBasket));
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.moveDetailsBasketToOrder(basketId));
    }

    @Test
    void updateOrderAndOrderDetails() {
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        Map<Long, Integer> pizzaIdToCount = new HashMap<>();
        pizzaIdToCount.put(1L, 2);
        OrderDetails orderDetails = TestData.ORDER_DETAILS;
        Set<OrderDetails> orderDetailsSet = new HashSet<>();
        orderDetailsSet.add(orderDetails);
        Order oldOrder = new Order(1L, now, expectedDateTime, null, expectedSum, StatusOrder.NEW,
                TestData.DELIVERY_ADDRESS, orderDetailsSet, TestData.USER_APP);
        orderDetails.setOrder(oldOrder);
        Order newOrder = oldOrder;
        newOrder.setDeliveryAddress(TestData.DELIVERY_ADDRESS_NEW);
        OrderResponseDto orderResponseDto = TestData.ORDER_RESPONSE_DTO;
        OrderRequestDto orderRequestDto = new OrderRequestDto(expectedDateTime,
                TestData.DELIVERY_ADDRESS_NEW.getCity(),
                TestData.DELIVERY_ADDRESS_NEW.getStreetName(),
                TestData.DELIVERY_ADDRESS_NEW.getHouseNumber(),
                TestData.DELIVERY_ADDRESS_NEW.getApartmentNumber(), pizzaIdToCount);
        when(orderRepository.findById(any())).thenReturn(Optional.of(oldOrder));
        when(orderDetailsRepository.findAllByOrder(oldOrder)).thenReturn(List.of(orderDetails));
        when(pizzaRepository.findById(any())).thenReturn(Optional.of(TestData.PIZZA));

        lenient().when(orderDetailsRepository.save(orderDetails)).thenReturn(orderDetails);
        lenient().when(orderDetailsRepository.findAllByOrder(newOrder)).thenReturn(List.of(TestData.ORDER_DETAILS));
        lenient().when(orderRepository.save(newOrder)).thenReturn(newOrder);
        lenient().when(userRepository.save(TestData.USER_APP)).thenReturn(TestData.USER_APP);
        when(orderDetailsMapper.orderDetailsListToOrderDetailsResponseDtoList(anyList())).thenReturn(List.of(TestData.ORDER_DETAILS_RESPONSE_DTO));
        when(orderMapper.toOrderResponseDto(newOrder, TestData.DELIVERY_ADDRESS_NEW, List.of(TestData.ORDER_DETAILS_RESPONSE_DTO))).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.updateOrderAndOrderDetails(1L, orderRequestDto);
        assertNotNull(result);
        assertEquals(StatusOrder.NEW, result.statusOrder());
        assertNotNull(result.orderDateTime());
        assertNotNull(result.pizzaIdToCount());
        assertEquals(expectedSum, result.sum());
    }

    @Test
    void updateOrderDetails_OrderNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long invalidOrderId = 999L;
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        Map<Long, Integer> pizzaIdToCount = new HashMap<>();
        pizzaIdToCount.put(1L, 2);
        OrderRequestDto orderRequestDto = new OrderRequestDto(expectedDateTime,
                TestData.DELIVERY_ADDRESS_NEW.getCity(),
                TestData.DELIVERY_ADDRESS_NEW.getStreetName(),
                TestData.DELIVERY_ADDRESS_NEW.getHouseNumber(),
                TestData.DELIVERY_ADDRESS_NEW.getApartmentNumber(), pizzaIdToCount);

        when(orderRepository.findById(invalidOrderId)).thenReturn(Optional.empty());

        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.updateOrderAndOrderDetails(invalidOrderId, orderRequestDto));
    }

    @Test
    void updateOrderAndOrderDetails_InvalidOrderStatus_ThrowInvalidOrderStatusException() {
        Long validOrderId = 1L;
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        LocalDateTime now = LocalDateTime.now();
        Map<Long, Integer> pizzaIdToCount = new HashMap<>();
        pizzaIdToCount.put(1L, 2);
        Set<OrderDetails> orderDetailsSet = new HashSet<>();
        orderDetailsSet.add(TestData.ORDER_DETAILS);
        Order order = new Order(1L, now, expectedDateTime, null, expectedSum, StatusOrder.PAID,
                TestData.DELIVERY_ADDRESS, orderDetailsSet, TestData.USER_APP);
        OrderRequestDto orderRequestDto = new OrderRequestDto(expectedDateTime,
                TestData.DELIVERY_ADDRESS_NEW.getCity(),
                TestData.DELIVERY_ADDRESS_NEW.getStreetName(),
                TestData.DELIVERY_ADDRESS_NEW.getHouseNumber(),
                TestData.DELIVERY_ADDRESS_NEW.getApartmentNumber(), pizzaIdToCount);
        when(orderRepository.findById(validOrderId)).thenReturn(Optional.of(order));
        assertThrows(InvalidOrderStatusException.class, () -> orderService.updateOrderAndOrderDetails(validOrderId, orderRequestDto));
    }

    @Test
    void deleteOrder() {
        Long orderId = 1L;
        Order order = new Order();
        order.setStatusOrder(StatusOrder.NEW);
        UserApp userApp = TestData.USER_APP;
        userApp.setOrders(new HashSet<>());
        userApp.addOrder(order);
        order.setUserApp(userApp);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userRepository.save(userApp)).thenReturn(userApp);
        orderService.deleteOrder(orderId);
        verify(userRepository).save(any());
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
    void deleteOrder_WhenOrderStatusNotNew_ThrowsInvalidOrderStatusException() {
        Long orderId = 1L;
        Order orderWithNonNewStatus = new Order();
        orderWithNonNewStatus.setStatusOrder(StatusOrder.CANCELED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderWithNonNewStatus));
        assertThrows(InvalidOrderStatusException.class, () -> orderService.deleteOrder(orderId));

        verify(userRepository, never()).save(any());
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void updateStatusOrder() {
        Long orderId = 1L;
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        Order order = new Order();
        order.setId(orderId);
        order.setUserApp(TestData.USER_APP);
        order.setStatusOrder(StatusOrder.NEW);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        StatusOrder newStatus = StatusOrder.PAID;
        order.setStatusOrder(newStatus);
        when(orderRepository.save(order)).thenReturn(order);
        OrderStatusResponseDto orderStatusResponseDto = new OrderStatusResponseDto(1L, expectedSum,
                newStatus, LocalDateTime.now(), 1L);
        when(orderMapper.toOrderStatusResponseDto(order, 1L)).thenReturn(orderStatusResponseDto);

        OrderStatusResponseDto updatedOrderStatus = orderService.updateStatusOrder(orderId, newStatus);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
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
        Order order = TestData.ORDER;
        OrderDetails orderDetails = TestData.ORDER_DETAILS;
        orderDetails.setOrder(order);

        OrderResponseDto orderResponseDto = TestData.ORDER_RESPONSE_DTO;
        when(orderRepository.findAllByUserApp_Id(userId)).thenReturn(List.of(order));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderDetailsRepository.findAllByOrder(order)).thenReturn(List.of(orderDetails));
        when(orderDetailsMapper.orderDetailsListToOrderDetailsResponseDtoList(anyList())).thenReturn(List.of(TestData.ORDER_DETAILS_RESPONSE_DTO));
        when(orderMapper.toOrderResponseDto(any(), any(), any())).thenReturn(orderResponseDto);

        List<OrderResponseDto> orderResponseDtoList = orderService.getAllOrdersByUser(userId);
        assertEquals(List.of(orderResponseDto), orderResponseDtoList);
    }

    @Test
    void testGetAllOrdersByUser_EmptyList() {
        Long userId = 1L;
        when(orderRepository.findAllByUserApp_Id(userId)).thenReturn(new ArrayList<>());
        List<OrderResponseDto> result = orderService.getAllOrdersByUser(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOrderByUser() {
        Order order = TestData.ORDER;
        OrderDetails orderDetails = TestData.ORDER_DETAILS;
        orderDetails.setOrder(order);

        OrderResponseDto orderResponseDto = TestData.ORDER_RESPONSE_DTO;
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderDetailsRepository.findAllByOrder(order)).thenReturn(List.of(orderDetails));
        when(orderDetailsMapper.orderDetailsListToOrderDetailsResponseDtoList(anyList())).thenReturn(List.of(TestData.ORDER_DETAILS_RESPONSE_DTO));
        when(orderMapper.toOrderResponseDto(any(), any(), any())).thenReturn(orderResponseDto);
        OrderResponseDto result = orderService.getOrderByUser(1L);
        assertEquals(orderResponseDto, result);
    }

    @Test
    void getOrderByUser_NonExistentOrderId_EntityNotFoundException() {
        Long nonExistentOrderId = 100L;
        when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> orderService.getOrderByUser(nonExistentOrderId));
    }

    @Test
    void getOrderByStatus() {
        List<Order> orders = List.of(TestData.ORDER);
        when(orderRepository.findAllByStatusOrder(StatusOrder.NEW)).thenReturn(orders);
        OrderStatusResponseDto orderStatusResponseDto = new OrderStatusResponseDto(1L, TestData.EXPECTED_SUM,
                StatusOrder.NEW, LocalDateTime.now(), 1L);
        when(orderMapper.toOrderStatusResponseDto(TestData.ORDER, 1L)).thenReturn(orderStatusResponseDto);
        List<OrderStatusResponseDto> actualResponse = orderService.getOrderByStatus(StatusOrder.NEW);
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
        when(orderMapper.toOrderStatusResponseDto(TestData.ORDER, 1L)).thenReturn(orderStatusResponseDto);
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