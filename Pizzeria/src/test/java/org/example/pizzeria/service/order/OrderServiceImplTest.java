package org.example.pizzeria.service.order;

import org.example.pizzeria.dto.order.*;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.order.*;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.entity.product.pizza.TypeBySize;
import org.example.pizzeria.entity.user.Address;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.NotCorrectArgumentException;
import org.example.pizzeria.exception.order.BasketNotFoundException;
import org.example.pizzeria.exception.order.OrderNotFoundException;
import org.example.pizzeria.exception.product.PizzaNotFoundException;
import org.example.pizzeria.exception.user.UserNotFoundException;
import org.example.pizzeria.mapper.order.BasketMapper;
import org.example.pizzeria.mapper.order.OrderDetailsMapper;
import org.example.pizzeria.mapper.order.OrderMapper;
import org.example.pizzeria.mapper.product.DoughMapper;
import org.example.pizzeria.mapper.product.IngredientMapper;
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
    private static final Bonus BONUS = new Bonus(10, 125.0);
    private static final Address ADDRESS = new Address("", "", "", "");
    private static final DeliveryAddress DELIVERY_ADDRESS = new DeliveryAddress("", "", "", "");
    private static final DeliveryAddress DELIVERY_ADDRESS_NEW = new DeliveryAddress("", "test", "10b", "");
    private static final UserApp USER_APP = UserApp.builder()
            .id(1L)
            .userName("IvanAdmin")
            .password("12345")
            .email("iv.admin@pizzeria.com")
            .address(ADDRESS)
            .isBlocked(false)
            .bonus(BONUS)
            .build();
    private static final Basket BASKET = new Basket(1L, USER_APP, null);
    private static final Dough DOUGH = new Dough(1, TypeDough.CLASSICA, 100, 120, 0.23);
    private static final DoughResponseClientDto DOUGH_RESPONSE_CLIENT_DTO = new DoughResponseClientDto(1,
            TypeDough.CLASSICA, 100, 120);
    private static final IngredientResponseClientDto INGREDIENT_RESPONSE_CLIENT_DTO_1 = new IngredientResponseClientDto(1L,
            "Tomato", 100, 90, GroupIngredient.BASIC);
    private static final IngredientResponseClientDto INGREDIENT_RESPONSE_CLIENT_DTO_2 = new IngredientResponseClientDto(2L,
            "Cheese", 20, 80, GroupIngredient.BASIC);
    private static final Ingredient INGREDIENT_1 = new Ingredient(1L, "Tomato", 100, 90, 0.2, GroupIngredient.BASIC, null);
    private static final Ingredient INGREDIENT_2 = new Ingredient(2L, "Cheese", 20, 80, 0.4, GroupIngredient.BASIC, null);
    private static final List<IngredientResponseClientDto> ingredientResponseClientBasicDtoList = List.of(INGREDIENT_RESPONSE_CLIENT_DTO_1, INGREDIENT_RESPONSE_CLIENT_DTO_2);
    private static final Pizza PIZZA = new Pizza(1L, "Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.MEDIUM, true, (0.2 + 0.4 + 0.23) * 1.3, 377, DOUGH, List.of(INGREDIENT_1, INGREDIENT_2));
    private static final PizzaResponseDto PIZZA_RESPONSE_DTO = new PizzaResponseDto(1L, "Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.MEDIUM, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList, (0.2 + 0.4 + 0.23) * 1.3, 377);
    private static final Order ORDER = new Order(1L, LocalDateTime.now(), (0.2 + 0.4 + 0.23) * 1.3 * 2, StatusOrder.NEW,
            DELIVERY_ADDRESS, null, USER_APP);
    private static final OrderDetails ORDER_DETAILS = new OrderDetails(1L, LocalDateTime.now().plusHours(1), null, ORDER,
            List.of(PIZZA, PIZZA));

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
    private DoughMapper doughMapper;
    @Mock
    private BasketMapper basketMapper;

    @Mock
    private IngredientMapper ingredientMapper;
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
        List<Pizza> pizzas = new ArrayList<>();
        pizzas.add(PIZZA);
        Map<PizzaResponseDto, Integer> pizzaCountMap = new HashMap<>();
        pizzaCountMap.put(PIZZA_RESPONSE_DTO, countPizza);
        BasketResponseDto basketResponseDto = new BasketResponseDto(pizzaCountMap, userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(USER_APP));
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.of(PIZZA));
        when(basketRepository.findByUserApp(USER_APP)).thenReturn(Optional.of(BASKET));
        when(pizzaMapper.mapPizzasToPizzaResponseDtos(pizzas, doughMapper, ingredientMapper))
                .thenReturn(Collections.singletonList(PIZZA_RESPONSE_DTO));
        BASKET.setPizzas(pizzas);
        when(basketRepository.save(BASKET)).thenReturn(BASKET);
        when(basketMapper.toBasketResponseDto(anyMap(), eq(userId))).thenReturn(basketResponseDto); // Мокирование с любым Map и userId
        BasketResponseDto result = orderService.addPizzaToBasket(userId, pizzaId, countPizza);
        assertEquals(basketResponseDto, result);
    }

    @Test
    void addPizzaToBasket_BadArguments() {
        Long userId = 123L;
        Long pizzaId = 12L;
        int countPizza = -1;
        assertThrows(UserNotFoundException.class, () -> orderService.addPizzaToBasket(userId, pizzaId, countPizza));
    }

    @Test
    void getBasketByUser() {
        Long userId = 1L;
        List<Pizza> pizzas = new ArrayList<>();
        pizzas.add(PIZZA);

        when(userRepository.findById(userId)).thenReturn(Optional.of(USER_APP));
        BASKET.setPizzas(pizzas);
        when(basketRepository.findByUserApp(USER_APP)).thenReturn(Optional.of(BASKET));
        when(pizzaMapper.mapPizzasToPizzaResponseDtos(pizzas, doughMapper, ingredientMapper))
                .thenReturn(Collections.singletonList(PIZZA_RESPONSE_DTO));
        Map<PizzaResponseDto, Integer> pizzaCountMap = new HashMap<>();
        pizzaCountMap.put(PIZZA_RESPONSE_DTO, 1);
        BasketResponseDto basketResponseDto = new BasketResponseDto(pizzaCountMap, userId);
        when(basketMapper.toBasketResponseDto(pizzaCountMap, userId)).thenReturn(basketResponseDto);

        BasketResponseDto result = orderService.getBasketByUser(userId);
        assertEquals(basketResponseDto, result);
    }

    @Test
    void getBasketByUser_UserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> orderService.getBasketByUser(userId));
    }

    @Test
    void changePizzasInBasket() {
        Long userId = 1L;
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(PIZZA_RESPONSE_DTO, 2);
        BasketRequestDto request = new BasketRequestDto(pizzaToCount, userId);

        List<Pizza> oldPizzas = new ArrayList<>();
        oldPizzas.add(PIZZA);
        oldPizzas.add(PIZZA);
        oldPizzas.add(PIZZA);
        BASKET.setPizzas(oldPizzas);
        BasketResponseDto expected = new BasketResponseDto(pizzaToCount, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(USER_APP));
        when(basketRepository.findByUserApp(USER_APP)).thenReturn(Optional.of(BASKET));
        when(basketRepository.save(BASKET)).thenReturn(BASKET);
        when(basketMapper.toBasketResponseDto(pizzaToCount, userId)).thenReturn(expected);
        BasketResponseDto result = orderService.changePizzasInBasket(request);
        verify(basketRepository, times(1)).save(BASKET);
        assertEquals(expected, result);
    }

    @Test
    void changePizzasInBasket_UserNotFound() {
        Long userId = 1L;
        BasketRequestDto request = new BasketRequestDto(Collections.emptyMap(), userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> orderService.changePizzasInBasket(request));
    }

    @Test
    void changePizzasInBasket_BasketNotFound() {
        Long userId = 2L;
        BasketRequestDto request = new BasketRequestDto(Collections.emptyMap(), userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserApp()));
        when(basketRepository.findByUserApp(any())).thenReturn(Optional.empty());
        assertThrows(BasketNotFoundException.class, () -> orderService.changePizzasInBasket(request));
    }

    @Test
    void changePizzasInBasket_NegativePizzaCount() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(USER_APP));
        when(basketRepository.findByUserApp(USER_APP)).thenReturn(Optional.of(BASKET));

        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(PIZZA_RESPONSE_DTO, -1);
        BasketRequestDto request = new BasketRequestDto(pizzaToCount, userId);
        assertThrows(NotCorrectArgumentException.class, () -> orderService.changePizzasInBasket(request));
    }

    @Test
    void moveDetailsBasketToOrder() {
        Long userId = 1L;
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        when(userRepository.findById(userId)).thenReturn(Optional.of(USER_APP));
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(PIZZA_RESPONSE_DTO, 2);
        BasketRequestDto basketRequestDto = new BasketRequestDto(pizzaToCount, userId);
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        ORDER_DETAILS.setDeliveryDateTime(expectedDateTime);
        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(PIZZA));
        when(orderDetailsMapper.toOrderDetails(any(LocalDateTime.class), anyList()))
                .thenReturn(ORDER_DETAILS);
        when(orderDetailsRepository.save(ORDER_DETAILS)).thenReturn(ORDER_DETAILS);
        ORDER.setOrderDetail(ORDER_DETAILS);
        OrderResponseDto orderResponseDto = new OrderResponseDto(
                1L,
                DELIVERY_ADDRESS.getCity(),
                DELIVERY_ADDRESS.getStreetName(),
                DELIVERY_ADDRESS.getHouseNumber(),
                DELIVERY_ADDRESS.getApartmentNumber(),
                expectedDateTime,
                (0.2 + 0.4 + 0.23) * 1.3 * 2,
                StatusOrder.NEW,
                LocalDateTime.now(),
                pizzaToCount,
                userId
        );
        when(orderRepository.save(any(Order.class))).thenReturn(ORDER);
        when(basketRepository.findByUserApp(USER_APP)).thenReturn(Optional.of(BASKET));
        when(orderMapper.toOrderResponseDto(eq(ORDER), eq(DELIVERY_ADDRESS), eq(ORDER_DETAILS), eq(pizzaToCount)))
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
    void moveDetailsBasketToOrder_UserNotFound() {
        BasketRequestDto basketRequestDto = new BasketRequestDto(new HashMap<>(), 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> orderService.moveDetailsBasketToOrder(basketRequestDto));
    }

    @Test
    void moveDetailsBasketToOrder_PizzaNotFound() {
        Long userId = 1L;
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(PIZZA_RESPONSE_DTO, 2);
        BasketRequestDto basketRequestDto = new BasketRequestDto(pizzaToCount, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(USER_APP));
        when(pizzaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(PizzaNotFoundException.class, () -> orderService.moveDetailsBasketToOrder(basketRequestDto));
    }

    @Test
    void updateOrderDetails() {
        Long orderId = 1L;
        Long userID = 1L;
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(PIZZA_RESPONSE_DTO, 2);
        OrderRequestDto orderRequestDto = new OrderRequestDto(orderId,
                "", "test", "10b", "",
                expectedDateTime, pizzaToCount, userID);
        ORDER.setDeliveryAddress(DELIVERY_ADDRESS_NEW);
        ORDER.setOrderDetail(ORDER_DETAILS);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(ORDER));
        ORDER_DETAILS.setDeliveryDateTime(expectedDateTime);
        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(PIZZA));
        when(userRepository.findById(1L)).thenReturn(Optional.of(USER_APP));
        when(orderDetailsRepository.save(any(OrderDetails.class))).thenReturn(ORDER_DETAILS);
        when(orderRepository.save(any(Order.class))).thenReturn(ORDER);
        OrderResponseDto orderResponseDto = new OrderResponseDto(
                1L,
                DELIVERY_ADDRESS_NEW.getCity(),
                DELIVERY_ADDRESS_NEW.getStreetName(),
                DELIVERY_ADDRESS_NEW.getHouseNumber(),
                DELIVERY_ADDRESS_NEW.getApartmentNumber(),
                expectedDateTime,
                (0.2 + 0.4 + 0.23) * 1.3 * 2,
                StatusOrder.NEW,
                LocalDateTime.now(),
                pizzaToCount,
                1L
        );
        when(orderMapper.toOrderResponseDto(ORDER, DELIVERY_ADDRESS_NEW, ORDER_DETAILS, pizzaToCount)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.updateOrderDetails(orderRequestDto);
        assertNotNull(result);
        assertEquals(StatusOrder.NEW, result.statusOrder());
        assertNotNull(result.orderDateTime());
        assertNotNull(result.pizzaToCount());
        assertEquals(expectedSum, result.sum());
    }

    @Test
    void updateOrderDetails_OrderNotFound() {
        Long userID = 1L;
        Long orderId = 999L;
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(PIZZA_RESPONSE_DTO, 2);
        OrderRequestDto orderRequestDto = new OrderRequestDto(orderId,
                "", "test", "10b", "",
                expectedDateTime, pizzaToCount, userID);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderDetails(orderRequestDto));
    }

    @Test
    void deleteOrder() {
        Long orderId = 2L;
        Order order  =new Order();
        order.setId(orderId);
        order.setStatusOrder(StatusOrder.NEW);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        assertDoesNotThrow(() -> orderService.deleteOrder(orderId));
        verify(orderRepository).findById(orderId);
        verify(orderDetailsRepository).delete(order.getOrderDetail());
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_OrderNotFound() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(orderId));
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void updateStatusOrder() {
        Long orderId = 1L;
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(ORDER));
        StatusOrder newStatus = StatusOrder.PAID;
        ORDER.setStatusOrder(newStatus);
        when(orderRepository.save(ORDER)).thenReturn(ORDER);
        OrderStatusResponseDto orderStatusResponseDto = new OrderStatusResponseDto(1L, expectedSum,
                newStatus, LocalDateTime.now(), 1L);
        when(orderMapper.toOrderStatusResponseDto(ORDER)).thenReturn(orderStatusResponseDto);
        OrderStatusResponseDto updatedOrderStatus = orderService.updateStatusOrder(orderId, newStatus);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(ORDER);
        assertEquals(newStatus, updatedOrderStatus.statusOrder());
    }

    @Test
    void updateStatusOrder_OrderNotFound() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.updateStatusOrder(orderId, StatusOrder.PAID));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getAllOrdersByUser() {
        Long userId = 1L;
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(PIZZA_RESPONSE_DTO, 2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(USER_APP));
        when(orderRepository.findAllByUserApp(USER_APP)).thenReturn(List.of(ORDER));
        when(orderRepository.getReferenceById(ORDER.getId())).thenReturn(ORDER);
        ORDER.setOrderDetail(ORDER_DETAILS);
        when(pizzaMapper.mapPizzasToPizzaResponseDtos(List.of(PIZZA, PIZZA), doughMapper, ingredientMapper))
                .thenReturn(List.of(PIZZA_RESPONSE_DTO, PIZZA_RESPONSE_DTO));
        OrderResponseDto expected = new OrderResponseDto(
                1L,
                DELIVERY_ADDRESS.getCity(),
                DELIVERY_ADDRESS.getStreetName(),
                DELIVERY_ADDRESS.getHouseNumber(),
                DELIVERY_ADDRESS.getApartmentNumber(),
                expectedDateTime,
                (0.2 + 0.4 + 0.23) * 1.3 * 2,
                StatusOrder.NEW,
                LocalDateTime.now(),
                pizzaToCount,
                userId
        );
        when(orderMapper.toOrderResponseDto(ORDER, DELIVERY_ADDRESS, ORDER_DETAILS, pizzaToCount)).thenReturn(expected);
        List<OrderResponseDto> orderResponseDtos = orderService.getAllOrdersByUser(userId);
        assertEquals(List.of(expected), orderResponseDtos);
    }

    @Test
    void getAllOrdersByUser_UserNotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> orderService.getAllOrdersByUser(userId));
        verify(orderRepository, never()).findAllByUserApp(any());
    }

    @Test
    void getOrderByUser() {
        Long userId = 1L;
        Long orderId = 1L;
        LocalDateTime expectedDateTime = LocalDateTime.now().plusHours(1);
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(PIZZA_RESPONSE_DTO, 2);
        when(orderRepository.getReferenceById(orderId)).thenReturn(ORDER);
        ORDER.setOrderDetail(ORDER_DETAILS);
        when(pizzaMapper.mapPizzasToPizzaResponseDtos(ORDER.getOrderDetail().getPizzas(), doughMapper, ingredientMapper))
                .thenReturn(List.of(PIZZA_RESPONSE_DTO, PIZZA_RESPONSE_DTO));
        OrderResponseDto expectedOrderResponseDto = new OrderResponseDto(
                1L,
                DELIVERY_ADDRESS.getCity(),
                DELIVERY_ADDRESS.getStreetName(),
                DELIVERY_ADDRESS.getHouseNumber(),
                DELIVERY_ADDRESS.getApartmentNumber(),
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
        verify(orderMapper).toOrderResponseDto(ORDER, DELIVERY_ADDRESS, ORDER_DETAILS, pizzaToCount);
        assertNotNull(result);
        assertEquals(expectedOrderResponseDto, result);
    }

    @Test
    void getOrderByUser_OrderNotFound() {
        Long orderId = 1L;
        when(orderRepository.getReferenceById(orderId)).thenReturn(null);
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderByUser(orderId));
        verify(orderRepository).getReferenceById(orderId);
        verifyNoMoreInteractions(orderMapper);
    }

    @Test
    void getOrderByStatus() {
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        StatusOrder statusOrder = StatusOrder.NEW;
        List<Order> orders = List.of(ORDER);
        when(orderRepository.findAllByStatusOrder(statusOrder)).thenReturn(orders);
        OrderStatusResponseDto orderStatusResponseDto = new OrderStatusResponseDto(1L, expectedSum,
                statusOrder, LocalDateTime.now(), 1L);
        when(orderMapper.toOrderStatusResponseDto(ORDER)).thenReturn(orderStatusResponseDto);
        List<OrderStatusResponseDto> actualResponse = orderService.getOrderByStatus(statusOrder);
        assertEquals(List.of(orderStatusResponseDto), actualResponse);
    }

    @Test
    void getOrderByStatus_NoOrdersFound() {
        StatusOrder statusOrder = StatusOrder.CANCELED;
        List<Order> emptyOrders = Collections.emptyList();
        when(orderRepository.findAllByStatusOrder(statusOrder)).thenReturn(emptyOrders);
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderByStatus(statusOrder));
        verifyNoMoreInteractions(orderMapper);
    }

    @Test
    void getAllOrdersByPeriod() {
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Order> orders = new ArrayList<>();
        orders.add(ORDER);
        OrderStatusResponseDto orderStatusResponseDto = new OrderStatusResponseDto(1L, expectedSum,
                StatusOrder.NEW, LocalDateTime.now(), 1L);

        when(orderRepository.findAllByOrderDateTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(orders);
        when(orderMapper.toOrderStatusResponseDto(ORDER))
                .thenReturn(orderStatusResponseDto);

        List<OrderStatusResponseDto> result = orderService.getAllOrdersByPeriod(startDate, endDate);
        assertEquals(List.of(orderStatusResponseDto), result);
    }

    @Test
    void getAllOrdersByPeriod_DateMissed() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(7);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        assertThrows(NotCorrectArgumentException.class, () -> orderService.getAllOrdersByPeriod(startDate, endDate));
        verify(orderRepository, never()).findAllByOrderDateTimeBetween(startDateTime, endDateTime);
    }
}