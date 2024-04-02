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

   @Autowired
   public OrderServiceImpl(UserRepository userRepository, BasketRepository basketRepository,
                           PizzaRepository pizzaRepository, OrderDetailsRepository orderDetailsRepository,
                           OrderRepository orderRepository, PizzaMapper pizzaMapper, BasketMapper basketMapper,
                           OrderMapper orderMapper) {
        this.userRepository = userRepository;
        this.basketRepository = basketRepository;
        this.pizzaRepository = pizzaRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderRepository = orderRepository;
        this.pizzaMapper = pizzaMapper;
        this.basketMapper = basketMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public BasketResponseDto addPizzaToBasket(Long userId, Long pizzaId, int countPizza) {
        Pizza pizza = pizzaRepository.getReferenceById(pizzaId);
        if (countPizza <= 0) {
            throw new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT);
        }
        Basket basket = basketRepository.findByUserApp_Id(userId)
                .orElseGet(() -> {
                    Basket newBasket = new Basket();
                    newBasket.setUserApp(userRepository.getReferenceById(userId));
                    return basketRepository.save(newBasket);
                });

        List<Pizza> pizzas = basket.getPizzas();
        for (int i = 0; i < countPizza; i++) {
            pizzas.add(pizza);
        }
        basket.setPizzas(pizzas);
        basketRepository.save(basket);
        Map<PizzaResponseDto, Integer> pizzaCountMap = pizzaMapper.mapPizzasToPizzaResponseDtos(basket.getPizzas()).stream()
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
        Map<Long, Integer> pizzaToCount = request.pizzaToCount();
        List<Pizza> newListPizzas = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : pizzaToCount.entrySet()) {
            Long pizzaId = entry.getKey();
            Integer count = entry.getValue();
            if (count < 0) {
                throw new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT);
            } else if (count == 0) {
                continue;
            }
            Pizza pizza = pizzaRepository.getReferenceById(pizzaId);
            for (int i = 0; i < count; i++) {
                newListPizzas.add(pizza);
            }
        }
        basket.setPizzas(newListPizzas);
        basketRepository.save(basket);
        Map<PizzaResponseDto, Integer> pizzaResponseCountMap = newListPizzas.stream()
                .collect(Collectors.groupingBy(pizzaMapper::toPizzaResponseDto, Collectors.summingInt(e -> 1)));
        return basketMapper.toBasketResponseDto(pizzaResponseCountMap, request.userId());
    }

    @Override
    @Transactional
    public OrderResponseDto moveDetailsBasketToOrder(Long idBasket) {
        Basket basket = basketRepository.findById(idBasket).orElseThrow(() ->
                new EntityInPizzeriaNotFoundException("Basket", ErrorMessage.ENTITY_NOT_FOUND));
        List<Pizza> pizzas = basket.getPizzas();
        if (pizzas.isEmpty()) {
            throw new EntityInPizzeriaNotFoundException("Pizza", ErrorMessage.ENTITY_NOT_FOUND);
        }
        UserApp userApp = userRepository.findById(basket.getUserApp().getId())
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));

        Order order = createNewOrder(userApp);
        double totalAmount = pizzas.stream().mapToDouble(Pizza::getAmount).sum();
        order.setSum(totalAmount);
        applyBonus(userApp, order, pizzas, totalAmount);

        OrderDetails orderDetails = createOrderDetails(pizzas, order);
        orderDetails = orderDetailsRepository.save(orderDetails);
        order.setOrderDetails(orderDetails);
        order = orderRepository.save(order);
        Map<PizzaResponseDto, Integer> pizzaDtoToCount = pizzas.stream()
                .collect(Collectors.groupingBy(pizzaMapper::toPizzaResponseDto, Collectors.summingInt(e -> 1)));
        basket.clear();
        basketRepository.save(basket);
        userApp.addOrder(order);
        userRepository.save(userApp);

        return orderMapper.toOrderResponseDto(order, order.getDeliveryAddress(), pizzaDtoToCount);
   }

    private Order createNewOrder(UserApp userApp){
        DeliveryAddress deliveryAddress= new DeliveryAddress(userApp.getAddress().getCity(), userApp.getAddress().getStreetName(),
                userApp.getAddress().getHouseNumber(), userApp.getAddress().getApartmentNumber());
        return orderMapper.toOrder(userApp, deliveryAddress);
    }

    private void applyBonus(UserApp userApp, Order order, List<Pizza> pizzas, double sum) {
        Bonus userBonus = userApp.getBonus();
        if (userBonus == null) {
            userBonus = new Bonus();
        }
        if (userBonus.getCountOrders() != null && userBonus.getCountOrders() >= TypeBonus.DISCOUNT_30.getCountConditions()) {
            order.setTypeBonus(TypeBonus.DISCOUNT_30);
            applyDiscount(order, 30);
            userBonus.setCountOrders(userBonus.getCountOrders() - TypeBonus.DISCOUNT_30.getCountConditions());
        } else if (userBonus.getSumOrders() != null && userBonus.getSumOrders() >= TypeBonus.DISCOUNT_50.getSumConditions()) {
            order.setTypeBonus(TypeBonus.DISCOUNT_50);
            applyDiscount(order, 50);
            userBonus.setSumOrders(userBonus.getSumOrders() - TypeBonus.DISCOUNT_50.getSumConditions());
        } else if (sum >= TypeBonus.DISCOUNT_100.getSumConditions() || pizzas.size() >= TypeBonus.DISCOUNT_100.getSumConditions()) {
            order.setTypeBonus(TypeBonus.DISCOUNT_100);
            applyFreePizza(pizzas, order);
        } else{
            userBonus.setCountOrders((userBonus.getCountOrders()==null ? 0: userBonus.getCountOrders()) + pizzas.size());
            userBonus.setSumOrders((userBonus.getSumOrders()==null ? 0: userBonus.getSumOrders()) + sum);
        }
        userApp.setBonus(userBonus);
    }

    private void applyDiscount(Order order, int discountPercent) {
        double discountMultiplier = 1.0 - (double) discountPercent / 100.0;
        double discountedSum = order.getSum() * discountMultiplier;
        order.setSum(discountedSum);
    }

    private void applyFreePizza(List<Pizza> pizzas, Order order) {
        if (!pizzas.isEmpty()) {
            Pizza mostExpensivePizza = pizzas.stream()
                    .max(Comparator.comparing(Pizza::getAmount))
                    .stream().findFirst()
                    .orElseThrow();
            double sum = mostExpensivePizza.getAmount();
            order.setSum(order.getSum()-sum);
        }
    }

    public static Map<Pizza, Integer> convertToPizzaCountMap(List<Pizza> pizzas) {
        Map<Pizza, Integer> pizzaCountMap = new HashMap<>();
        for (Pizza pizza : pizzas) {
            pizzaCountMap.put(pizza, pizzaCountMap.getOrDefault(pizza, 0) + 1);
        }
        return pizzaCountMap;
    }
    private List<Pizza> convertToPizzasList(Map<Long, Integer> pizzaToCount) {
        List<Pizza> pizzas = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : pizzaToCount.entrySet()) {
            Long pizzaId = entry.getKey();
            Integer count = entry.getValue();
            for (int i = 0; i < count; i++) {
                Pizza pizza = pizzaRepository.findById(pizzaId).orElse(null);
                if (pizza != null) {
                    pizzas.add(pizza);
                } else {
                    throw new EntityInPizzeriaNotFoundException("Pizza", ErrorMessage.ENTITY_NOT_FOUND);
                }
            }
        }
        return pizzas;
    }

    private OrderDetails createOrderDetails(List<Pizza> pizzas, Order order){
        Map<Pizza, Integer> pizzaToCount = convertToPizzaCountMap(pizzas);
        OrderDetails newOrderDetails = new OrderDetails();
        for (Map.Entry<Pizza, Integer> entry : pizzaToCount.entrySet()) {
            newOrderDetails.setOrder(order);
            newOrderDetails.setPizza(entry.getKey());
            newOrderDetails.setQuantity(entry.getValue());
        }
        return newOrderDetails;
    }

    private void returnOldBonus(Order order, UserApp userApp){
        List<Pizza> oldListPizzas = new ArrayList<>();
        List<OrderDetails> oldOrderDetails = orderDetailsRepository.findAllByOrder(order);
        for (OrderDetails orderDetail : oldOrderDetails) {
            for (int j = 0; j < orderDetail.getQuantity(); j++) {
                oldListPizzas.add(orderDetail.getPizza());
            }
        }
        double totalOldFullAmount = oldListPizzas.stream().mapToDouble(Pizza::getAmount).sum();
        TypeBonus typeBonus = order.getTypeBonus();
        Bonus bonus = userApp.getBonus();
        if (typeBonus == TypeBonus.DISCOUNT_30) {
            bonus.setCountOrders(bonus.getCountOrders() + TypeBonus.DISCOUNT_30.getCountConditions());
        }
        if (typeBonus == TypeBonus.DISCOUNT_50) {
            bonus.setSumOrders(bonus.getSumOrders() + TypeBonus.DISCOUNT_50.getSumConditions());
        }
        if (typeBonus == null) {
            if (bonus != null) {
                bonus.setCountOrders(bonus.getCountOrders() - oldListPizzas.size());
                bonus.setSumOrders(bonus.getSumOrders() - totalOldFullAmount);
            }
        }
        userApp.setBonus(bonus);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderAndOrderDetails(Long orderId, OrderRequestDto orderRequestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Order",ErrorMessage.ENTITY_NOT_FOUND));
        if (!order.getStatusOrder().equals(StatusOrder.NEW))
            throw new InvalidOrderStatusException(ErrorMessage.INVALID_STATUS_ORDER_FOR_UPDATE);
        UserApp userApp = order.getUserApp();
        returnOldBonus(order, userApp);
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAllByOrder(order);
        Map<Pizza, Integer> oldPizzaToCount = new HashMap<>();
        for (OrderDetails details : orderDetailsList) {
            oldPizzaToCount.put(details.getPizza(), details.getQuantity());
        }
        List<Pizza> pizzas = convertToPizzasList(orderRequestDto.pizzaToCount());
        Map<Pizza, Integer> newPizzaToCount = convertToPizzaCountMap(pizzas);
        for (OrderDetails details : orderDetailsList) {
            Pizza pizza = details.getPizza();
            if (newPizzaToCount.containsKey(pizza)) {
                details.setQuantity(newPizzaToCount.get(pizza));
            } else {
                orderDetailsRepository.delete(details);
            }
        }
        for (Map.Entry<Pizza, Integer> entry : newPizzaToCount.entrySet()) {
            Pizza pizza = entry.getKey();
            Integer quantity = entry.getValue();
            if (!oldPizzaToCount.containsKey(pizza)) {
                OrderDetails newOrderDetails = new OrderDetails();
                newOrderDetails.setOrder(order);
                newOrderDetails.setPizza(pizza);
                newOrderDetails.setQuantity(quantity);
                orderDetailsRepository.save(newOrderDetails);
            }
        }
        double totalAmount = pizzas.stream().mapToDouble(Pizza::getAmount).sum();
        applyBonus(userApp, order, pizzas, totalAmount);
        DeliveryAddress deliveryAddress = new DeliveryAddress(orderRequestDto.deliveryCity(),
                orderRequestDto.deliveryStreetName(),
                orderRequestDto.deliveryHouseNumber(),
                orderRequestDto.deliveryApartmentNumber());
        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryDateTime(orderRequestDto.deliveryDateTime());
        order.setSum(totalAmount);
        order = orderRepository.save(order);
        userRepository.save(userApp);
        Map<PizzaResponseDto, Integer> pizzaDtoToCount = pizzas.stream()
                .collect(Collectors.groupingBy(pizzaMapper::toPizzaResponseDto, Collectors.summingInt(e -> 1)));
        return orderMapper.toOrderResponseDto(order, deliveryAddress, pizzaDtoToCount);
    }


    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND));
        if (order.getStatusOrder() == StatusOrder.NEW) {
            UserApp userApp = order.getUserApp();
            userApp.removeOrder(order);
            userRepository.save(userApp);
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
        return orderMapper.toOrderStatusResponseDto(updatedOrder, updatedOrder.getUserApp().getId());
    }

    @Override
    public List<OrderResponseDto> getAllOrdersByUser(Long userId) {
        List<Order> orders = orderRepository.findAllByUserApp_Id(userId);
        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        for (Order order : orders) {
            orderResponseDtoList.add(getOrderByUser(order.getId()));
        }
        return orderResponseDtoList;
    }

    @Override
    public OrderResponseDto getOrderByUser(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> orderMapper.toOrderResponseDto(order, order.getDeliveryAddress(), createMapPizzaResponseDtoToCount(order)))
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND));
    }

    private Map<PizzaResponseDto, Integer> createMapPizzaResponseDtoToCount(Order order){
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAllByOrder(order);
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        for (OrderDetails details : orderDetailsList) {
            pizzaToCount.put(pizzaMapper.toPizzaResponseDto(details.getPizza()), details.getQuantity());
        }
        return pizzaToCount;
    }
    @Override
    public List<OrderStatusResponseDto> getOrderByStatus(StatusOrder statusOrder) {
        List<Order> orders = orderRepository.findAllByStatusOrder(statusOrder);
        if (!orders.isEmpty()) {
            return orders.stream()
                    .map(o -> orderMapper.toOrderStatusResponseDto(o, o.getUserApp().getId()))
                    .collect(Collectors.toList());
        } else {
            throw new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND);
        }
   }

    @Override
    public List<OrderStatusResponseDto> getAllOrdersByPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT);
        }
        LocalTime localTime = LocalTime.of(0, 0);
        List<Order> orders = orderRepository.findAllByOrderDateTimeBetween(LocalDateTime.of(startDate, localTime),
                LocalDateTime.of(endDate, localTime));
        return orders.stream().map(o -> orderMapper.toOrderStatusResponseDto(o, o.getUserApp().getId())).toList();
    }
}
