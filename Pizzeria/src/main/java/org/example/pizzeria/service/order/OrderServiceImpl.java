package org.example.pizzeria.service.order;

import org.example.pizzeria.dto.order.*;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.order.*;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final BasketRepository basketRepository;
    private final PizzaRepository pizzaRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderRepository orderRepository;
    private final PizzaMapper pizzaMapper;
    private final BasketMapper basketMapper;
    private final OrderMapper orderMapper;
    private final OrderDetailsMapper orderDetailsMapper;

   @Autowired
   public OrderServiceImpl(UserRepository userRepository, BasketRepository basketRepository, PizzaRepository pizzaRepository, OrderDetailsRepository orderDetailsRepository, OrderRepository orderRepository, PizzaMapper pizzaMapper, BasketMapper basketMapper, OrderMapper orderMapper, OrderDetailsMapper orderDetailsMapper) {
        this.userRepository = userRepository;
        this.basketRepository = basketRepository;
        this.pizzaRepository = pizzaRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderRepository = orderRepository;
        this.pizzaMapper = pizzaMapper;
        this.basketMapper = basketMapper;
        this.orderMapper = orderMapper;
        this.orderDetailsMapper = orderDetailsMapper;
    }

    @Override
    @Transactional
    public BasketResponseDto addPizzaToBasket(Long userId, Long pizzaId, int countPizza) {
        Pizza pizza = pizzaRepository.getReferenceById(pizzaId);
        if (countPizza <= 0) {
            throw new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT);
        }
        Optional<Basket> optionalBasket = basketRepository.findByUserApp_Id(userId);
        Basket basket = optionalBasket.orElseThrow(() -> new EntityInPizzeriaNotFoundException("Basket", ErrorMessage.ENTITY_NOT_FOUND));

        List<Pizza> pizzas = new ArrayList<>(basket.getPizzas());
        for (int i = 0; i < countPizza; i++) {
            pizzas.add(pizza);
        }
        basket.setPizzas(pizzas);
        basketRepository.save(basket);
        List<PizzaResponseDto> pizzaResponseDtoList = pizzaMapper.mapPizzasToPizzaResponseDtos(pizzas);
        Map<PizzaResponseDto, Integer> pizzaCountMap = pizzaResponseDtoList.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.reducing(0, e -> 1, Integer::sum)));
        return basketMapper.toBasketResponseDto(pizzaCountMap, userId);
    }

    @Override
    public BasketResponseDto getBasketByUser(Long userId) {
        Basket basket = basketRepository.findByUserApp_Id(userId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Basket",ErrorMessage.ENTITY_NOT_FOUND));
        List<PizzaResponseDto> pizzaResponseDtoList = new ArrayList<>();
        if (basket.getPizzas() != null) {
            pizzaResponseDtoList = pizzaMapper.mapPizzasToPizzaResponseDtos(
                    basket.getPizzas());
        }
        Map<PizzaResponseDto, Integer> pizzaCountMap = pizzaResponseDtoList.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.reducing(0, e -> 1, Integer::sum)));
        return basketMapper.toBasketResponseDto(pizzaCountMap, userId);
    }

    @Override
    @Transactional
    public BasketResponseDto changePizzasInBasket(BasketRequestDto request) {
        Basket basket = basketRepository.findByUserApp_Id(request.userId())
        .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Basket",ErrorMessage.ENTITY_NOT_FOUND));
        Map<PizzaResponseDto, Integer> pizzaToCount = request.pizzaToCount();
        List<Pizza> newListPizzas = new ArrayList<>();

        for (Map.Entry<PizzaResponseDto, Integer> entry : pizzaToCount.entrySet()) {
            PizzaResponseDto pizzaResponseDto = entry.getKey();
            Integer count = entry.getValue();
            if (count <= 0) {
                throw new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT);
            }
            for (int i = 0; i < count; i++) {
                newListPizzas.add(pizzaRepository.getReferenceById(pizzaResponseDto.getId()));
            }
        }
        basket.setPizzas(newListPizzas);
        basketRepository.save(basket);
        return basketMapper.toBasketResponseDto(pizzaToCount, request.userId());
    }

    @Override
    @Transactional
    public OrderResponseDto moveDetailsBasketToOrder(BasketRequestDto basketRequestDto) {
        Map<PizzaResponseDto, Integer> pizzaToCount = basketRequestDto.pizzaToCount();
        Long userId = basketRequestDto.userId();
        UserApp userApp = userRepository.findById(userId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));
        Order order = new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setDeliveryAddress(new DeliveryAddress(userApp.getAddress().getCity(), userApp.getAddress().getStreetName(),
                userApp.getAddress().getHouseNumber(), userApp.getAddress().getApartmentNumber()));
        order.setStatusOrder(StatusOrder.NEW);
        order.setUserApp(userApp);

        OrderDetails orderDetails = orderDetailsMapper.toOrderDetails(LocalDateTime.now().plusHours(1), new ArrayList<>());
        addPizzasToListAndCountSum(orderDetails, order, pizzaToCount);

        OrderDetails savedOrderDetails = orderDetailsRepository.save(orderDetails);
        order.setOrderDetail(savedOrderDetails);
        applyBonus(userApp, order, orderDetails, order.getSum());
        Order savedOrder = orderRepository.save(order);
        Basket basket = basketRepository.findByUserApp_Id(userApp.getId())
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Basket",ErrorMessage.ENTITY_NOT_FOUND));
        if (basket.getPizzas() != null) {
            basket.getPizzas().clear();
        }
        basketRepository.save(basket);
        return orderMapper.toOrderResponseDto(savedOrder, savedOrder.getDeliveryAddress(), savedOrderDetails, basketRequestDto.pizzaToCount());
    }

    private void applyBonus(UserApp userApp, Order order, OrderDetails orderDetails, double sum) {
        Bonus userBonus = userApp.getBonus();
        if (userBonus == null) {
            userBonus = new Bonus();
        }
        if (userBonus.getCountOrders() != null && userBonus.getCountOrders() >= TypeBonus.DISCOUNT_30.getCountConditions()) {
            orderDetails.setTypeBonus(TypeBonus.DISCOUNT_30);
            applyDiscount(order, 30);
            userBonus.setCountOrders(0);
        } else if (userBonus.getSumOrders() != null && userBonus.getSumOrders() >= TypeBonus.DISCOUNT_50.getSumConditions()) {
            orderDetails.setTypeBonus(TypeBonus.DISCOUNT_50);
            applyDiscount(order, 50);
            userBonus.setSumOrders(0.0);
        } else if (sum >= TypeBonus.DISCOUNT_100.getSumConditions() || orderDetails.getPizzas().size() >= TypeBonus.DISCOUNT_100.getSumConditions()) {
            orderDetails.setTypeBonus(TypeBonus.DISCOUNT_100);
            applyFreePizza(orderDetails, order);
        } else{
            userBonus.setCountOrders((userBonus.getCountOrders()==null ? 0: userBonus.getCountOrders()) +orderDetails.getPizzas().size());
            userBonus.setSumOrders((userBonus.getSumOrders()==null ? 0: userBonus.getSumOrders()) + sum);
            orderDetails.setTypeBonus(TypeBonus.DISCOUNT_100);
        }
        userApp.setBonus(userBonus);
        orderDetailsRepository.save(orderDetails);
        userRepository.save(userApp);
    }

    private void applyDiscount(Order order, int discountPercent) {
        double discountMultiplier = 1.0 - (double) discountPercent / 100.0;
        double discountedSum = order.getSum() * discountMultiplier;
        order.setSum(discountedSum);
    }

    private void applyFreePizza(OrderDetails orderDetails, Order order) {
        if (!orderDetails.getPizzas().isEmpty()) {
            Pizza mostExpensivePizza = orderDetails.getPizzas().stream()
                    .max(Comparator.comparing(Pizza::getAmount))
                    .stream().findFirst()
                    .orElseThrow();
            double sum = mostExpensivePizza.getAmount();
            order.setSum(order.getSum()-sum);
        }
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderDetails(OrderRequestDto orderRequestDto) {
        Long orderId = orderRequestDto.id();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Order",ErrorMessage.ENTITY_NOT_FOUND));
        OrderDetails orderDetails = order.getOrderDetail();

        order.setDeliveryAddress(new DeliveryAddress(orderRequestDto.deliveryCity(),
                orderRequestDto.deliveryStreetName(),
                orderRequestDto.deliveryHouseNumber(),
                orderRequestDto.deliveryApartmentNumber()));
        addPizzasToListAndCountSum(orderDetails, order, orderRequestDto.pizzaToCount());
        orderDetails.setDeliveryDateTime(orderRequestDto.deliveryDateTime());

        UserApp userApp = userRepository.findById(orderRequestDto.userAppId())
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));
        applyBonus(userApp, order, orderDetails, order.getSum());

        OrderDetails savedOrderDetails = orderDetailsRepository.save(orderDetails);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponseDto(savedOrder, savedOrder.getDeliveryAddress(), savedOrderDetails, orderRequestDto.pizzaToCount());
    }

    private void addPizzasToListAndCountSum(OrderDetails orderDetails, Order order, Map<PizzaResponseDto, Integer>  pizzaToCount){
        List<Pizza> pizzas = new ArrayList<>();
        double sum = 0;
        for (Map.Entry<PizzaResponseDto, Integer> entry : pizzaToCount.entrySet()) {
            PizzaResponseDto pizzaResponseDto = entry.getKey();
            Integer count = entry.getValue();
            Pizza pizza = pizzaRepository.findById(pizzaResponseDto.getId())
                    .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Pizza", ErrorMessage.ENTITY_NOT_FOUND));
            for (int i = 0; i < count; i++) {
                pizzas.add(pizza);
                sum += pizza.getAmount();
            }
        }
        orderDetails.setPizzas(pizzas);
        order.setSum(sum);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND));
        if (order.getStatusOrder() == StatusOrder.NEW) {
            orderDetailsRepository.delete(order.getOrderDetail());
            orderRepository.delete(order);
        } else {
            throw new InvalidOrderStatusException(ErrorMessage.INVALID_STATUS_ORDER_FOR_DELETE);
        }
    }

    @Override
    @Transactional
    public OrderStatusResponseDto updateStatusOrder(Long orderId, StatusOrder statusOrder) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND));
        order.setStatusOrder(statusOrder);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderStatusResponseDto(updatedOrder);
    }

    @Override
    public List<OrderResponseDto> getAllOrdersByUser(Long userId) {
        UserApp userApp = userRepository.findById(userId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));
        List<Order> orders = orderRepository.findAllByUserApp(userApp);
        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        for (Order order : orders) {
            orderResponseDtoList.add(getOrderByUser(order.getId()));
        }
        return orderResponseDtoList;
    }

    @Override
    public OrderResponseDto getOrderByUser(Long orderId) {
        Order order = orderRepository.getReferenceById(orderId);
        List<PizzaResponseDto> pizzaResponseDtoList = pizzaMapper.mapPizzasToPizzaResponseDtos(
                order.getOrderDetail().getPizzas());
        Map<PizzaResponseDto, Integer> pizzaCountMap = pizzaResponseDtoList.stream()
                    .collect(Collectors.groupingBy(p -> p, Collectors.reducing(0, e -> 1, Integer::sum)));
        return orderMapper.toOrderResponseDto(order, order.getDeliveryAddress(),  order.getOrderDetail(),pizzaCountMap);
    }

    @Override
    public List<OrderStatusResponseDto> getOrderByStatus(StatusOrder statusOrder) {
        List<Order> orders = orderRepository.findAllByStatusOrder(statusOrder);
        if (!orders.isEmpty())
            return orders.stream().map(orderMapper::toOrderStatusResponseDto).toList();
        else throw new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND);
    }

    @Override
    public List<OrderStatusResponseDto> getAllOrdersByPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT);
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<Order> orders = orderRepository.findAllByOrderDateTimeBetween(startDateTime, endDateTime);
        return orders.stream().map(orderMapper::toOrderStatusResponseDto).toList();
    }
}
