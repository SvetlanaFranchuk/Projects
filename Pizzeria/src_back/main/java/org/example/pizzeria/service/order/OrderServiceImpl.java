package org.example.pizzeria.service.order;

import org.example.pizzeria.dto.order.*;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaToBasketRequestDto;
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
    public OrderServiceImpl(UserRepository userRepository, BasketRepository basketRepository,
                            PizzaRepository pizzaRepository, OrderDetailsRepository orderDetailsRepository,
                            OrderRepository orderRepository, PizzaMapper pizzaMapper, BasketMapper basketMapper,
                            OrderMapper orderMapper, OrderDetailsMapper orderDetailsMapper) {
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

    /**
     * Adds a specified number of pizzas to the user's basket.
     * *
     * This method adds a specified number of pizzas to the user's basket. It first retrieves the pizza
     * to be added and validates the count of pizzas. Then, it retrieves the user's basket from the database
     * or creates a new one if it doesn't exist. Next, it adds the specified number of pizzas to the basket,
     * updates the basket in the database, and maps the pizzas in the basket to a response DTO while counting
     * the occurrences of each pizza. Finally, it creates and returns a BasketResponseDto representing the
     * updated basket. If the count of pizzas to add is not positive, it throws a NotCorrectArgumentException.
     *
     * @param userId               the ID of the user
     * @param pizzaToBasketRequestDto the request containing the pizza ID and the count of pizzas to add to the basket
     * @return a BasketResponseDto representing the updated basket
     * @throws NotCorrectArgumentException if the count of pizzas to add is not positive
     */
    @Override
    @Transactional
    public BasketResponseDto addPizzaToBasket(Long userId, PizzaToBasketRequestDto pizzaToBasketRequestDto) {
        Pizza pizza = pizzaRepository.getReferenceById(pizzaToBasketRequestDto.getPizzaId());
        if (pizzaToBasketRequestDto.getCountPizza() <= 0) {
            throw new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT);
        }
        Basket basket = basketRepository.findByUserApp_Id(userId)
                .orElseGet(() -> {
                    Basket newBasket = new Basket();
                    newBasket.setUserApp(userRepository.getReferenceById(userId));
                    return basketRepository.save(newBasket);
                });

        List<Pizza> pizzas = basket.getPizzas();
        for (int i = 0; i < pizzaToBasketRequestDto.getCountPizza(); i++) {
            pizzas.add(pizza);
        }
        basket.setPizzas(pizzas);
        basketRepository.save(basket);
        Map<PizzaResponseDto, Integer> pizzaCountMap = pizzaMapper.mapPizzasToPizzaResponseDtos(basket.getPizzas()).stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.reducing(0, e -> 1, Integer::sum)));
        return basketMapper.toBasketResponseDto(pizzaCountMap, userId);
    }

    /**
     * Retrieves the basket of a user.
     * *
     * This method retrieves the basket of a user by their ID. It first retrieves the basket from the database
     * and then maps the pizzas in the basket to response DTOs while counting the occurrences of each pizza.
     * Finally, it creates and returns a BasketResponseDto representing the user's basket. If the user's basket
     * is not found, it throws an EntityInPizzeriaNotFoundException.
     *
     * @param userId the ID of the user
     * @return a BasketResponseDto representing the user's basket
     * @throws EntityInPizzeriaNotFoundException if the user's basket is not found
     */
    @Override
    public BasketResponseDto getBasketByUser(Long userId) {
        Basket basket = basketRepository.findByUserApp_Id(userId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Basket", ErrorMessage.ENTITY_NOT_FOUND));
        List<PizzaResponseDto> pizzaResponseDtoList = new ArrayList<>();
        if (basket.getPizzas() != null) {
            pizzaResponseDtoList = pizzaMapper.mapPizzasToPizzaResponseDtos(
                    basket.getPizzas());
        }
        Map<PizzaResponseDto, Integer> pizzaCountMap = pizzaResponseDtoList.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.reducing(0, e -> 1, Integer::sum)));
        return basketMapper.toBasketResponseDto(pizzaCountMap, userId);
    }

    /**
     * Changes the pizzas in the user's basket based on the provided request.
     * *
     * This method changes the pizzas in the user's basket based on the provided BasketRequestDto.
     * It retrieves the basket of the user from the database, then iterates over the mapping of pizza IDs
     * to counts in the request to update the pizzas in the basket. If the count of any pizza is negative,
     * it throws a NotCorrectArgumentException. Finally, it maps the updated pizzas in the basket to response
     * DTOs, counts the occurrences of each pizza, and returns a BasketResponseDto representing the updated basket.
     * If the user's basket is not found, it throws an EntityInPizzeriaNotFoundException.
     *
     * @param request the BasketRequestDto containing the user ID and the mapping of pizza IDs to counts
     * @return a BasketResponseDto representing the updated basket
     * @throws EntityInPizzeriaNotFoundException if the user's basket is not found
     * @throws NotCorrectArgumentException      if the count of any pizza is negative
     */
    @Override
    @Transactional
    public BasketResponseDto changePizzasInBasket(BasketRequestDto request) {
        Basket basket = basketRepository.findByUserApp_Id(request.userId())
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Basket", ErrorMessage.ENTITY_NOT_FOUND));
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


    /**
     * Moves pizzas from the user's basket to a newly created order. Clears the basket afterwards.
     * When creating an order, the order amount is calculated and the bonus is calculated and applied
     *
     * @param idBasket the ID of the basket containing pizzas to be moved to the order
     * @return the response DTO representing the newly created order with details
     * @throws EntityInPizzeriaNotFoundException if the basket or pizzas are not found
     */
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

        order = orderRepository.save(order);
        List<OrderDetails> orderDetailsList= createOrderDetails(pizzas, order);

        basket.clear();
        basketRepository.save(basket);
        userApp.addOrder(order);
        userRepository.save(userApp);

        return orderMapper.toOrderResponseDto(order, order.getDeliveryAddress(), orderDetailsMapper.orderDetailsListToOrderDetailsResponseDtoList(orderDetailsList));
    }

    private Order createNewOrder(UserApp userApp) {
        DeliveryAddress deliveryAddress = new DeliveryAddress(userApp.getAddress().getCity(), userApp.getAddress().getStreetName(),
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
        } else {
            userBonus.setCountOrders((userBonus.getCountOrders() == null ? 0 : userBonus.getCountOrders()) + pizzas.size());
            userBonus.setSumOrders((userBonus.getSumOrders() == null ? 0 : userBonus.getSumOrders()) + sum);
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
            order.setSum(order.getSum() - sum);
        }
    }

    private static Map<Pizza, Integer> convertToPizzaCountMap(List<Pizza> pizzas) {
        Map<Pizza, Integer> pizzaCountMap = new HashMap<>();
        for (Pizza pizza : pizzas) {
            if (pizzaCountMap.containsKey(pizza)) {
                pizzaCountMap.put(pizza, pizzaCountMap.get(pizza) + 1);
            } else {
                pizzaCountMap.put(pizza, 1);
            }
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

    private List<OrderDetails> createOrderDetails(List<Pizza> pizzas, Order order) {
        List<OrderDetails> orderDetailsList = new ArrayList<>();
        Map<Pizza, Integer> pizzaToCount = convertToPizzaCountMap(pizzas);
        for (Map.Entry<Pizza, Integer> entry : pizzaToCount.entrySet()) {
            OrderDetails newOrderDetails = new OrderDetails();
            newOrderDetails.setOrder(order);
            newOrderDetails.setPizza(entry.getKey());
            newOrderDetails.setQuantity(entry.getValue());
            orderDetailsRepository.save(newOrderDetails);
            order.addOrderDetails(newOrderDetails);
            orderDetailsList.add(newOrderDetails);
        }
        return orderDetailsList;
    }

    private void returnOldBonus(Order order, UserApp userApp) {
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

    /**
     * Updates the order and its associated order details based on the provided order request data.
     * *
     * This method retrieves the order with the specified ID from the database and checks if its status is 'NEW'.
     * If the order status is not 'NEW', it throws an {@link InvalidOrderStatusException}.
     * The method then updates the order details according to the provided pizza-to-count mapping in the order request.
     * It removes any order details for pizzas not present in the updated order request.
     * Additionally, it adds new order details for pizzas that are not currently included in the order.
     * After updating the order details, it recalculates the total amount for the order based on the updated pizzas
     * and applies any applicable bonuses.
     * Finally, it updates the delivery address, delivery date and time, and total sum of the order, and saves the
     * changes to the database.
     *
     * @param orderId the ID of the order to be updated
     * @param orderRequestDto the order request data containing the updated information
     * @return the response DTO representing the updated order with details
     * @throws EntityInPizzeriaNotFoundException if the specified order is not found in the database
     * @throws InvalidOrderStatusException if the status of the order is not 'NEW'
     */
    @Override
    @Transactional
    public OrderResponseDto updateOrderAndOrderDetails(Long orderId, OrderRequestDto orderRequestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND));
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
                order.removeOrderDetails(details);
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
                order.addOrderDetails(newOrderDetails);
            }
        }
        List<OrderDetails> orderDetailsListNew = orderDetailsRepository.findAllByOrder(order);;
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
        return orderMapper.toOrderResponseDto(order, deliveryAddress, orderDetailsMapper.orderDetailsListToOrderDetailsResponseDtoList(orderDetailsListNew));
    }

    /**
     * Deletes the order with the specified ID from the database.
     * *
     * This method retrieves the order with the given ID from the database.
     * If the order is found and its status is 'NEW', it removes the order from the associated user and deletes it from the database.
     * If the order status is not 'NEW', indicating that it has already been processed or is in progress, the method throws an {@link InvalidOrderStatusException}.
     *
     * @param id the ID of the order to be deleted
     * @throws EntityInPizzeriaNotFoundException if the specified order is not found in the database
     * @throws InvalidOrderStatusException if the status of the order is not 'NEW'
     */
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

    /**
     * Updates the status of the order with the specified ID in the database.
     * *
     * This method retrieves the order with the given ID from the database and updates its status to the specified status.
     * It then saves the updated order to the database and returns a {@link OrderStatusResponseDto} containing the updated order information.
     *
     * @param orderId the ID of the order to update
     * @param statusOrder the new status of the order
     * @return an {@link OrderStatusResponseDto} containing the updated order information
     * @throws EntityInPizzeriaNotFoundException if the specified order is not found in the database
     */
    @Override
    @Transactional
    public OrderStatusResponseDto updateStatusOrder(Long orderId, StatusOrder statusOrder) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND));
        order.setStatusOrder(statusOrder);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderStatusResponseDto(updatedOrder, updatedOrder.getUserApp().getId());
    }

    /**
     * Retrieves all orders associated with the user specified by their ID.
     * *
     * This method fetches all orders belonging to the user with the given ID from the database.
     * It then converts each order into an {@link OrderResponseDto} using the {@link #getOrderByUser(Long)} method and adds it to a list.
     * The list of order response DTOs is then returned.
     *
     * @param userId the ID of the user whose orders to retrieve
     * @return a list of {@link OrderResponseDto} objects representing the orders associated with the user
     */
    @Override
    public List<OrderResponseDto> getAllOrdersByUser(Long userId) {
        List<Order> orders = orderRepository.findAllByUserApp_Id(userId);
        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        for (Order order : orders) {
            orderResponseDtoList.add(getOrderByUser(order.getId()));
        }
        return orderResponseDtoList;
    }

    /**
     * Retrieves an order by its ID and maps it to an {@link OrderResponseDto}.
     * *
     * This method fetches the order with the specified ID from the database. If the order is found,
     * it retrieves the associated order details and maps them to {@link OrderDetailsResponseDto} objects.
     * Then, it maps the order along with its delivery address and order details to an {@link OrderResponseDto}.
     * If no order is found with the given ID, it throws an {@link EntityInPizzeriaNotFoundException}.
     *
     * @param orderId the ID of the order to retrieve
     * @return an {@link OrderResponseDto} representing the order
     * @throws EntityInPizzeriaNotFoundException if no order is found with the specified ID
     */
    @Override
    public OrderResponseDto getOrderByUser(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    List<OrderDetails> orderDetailsList = orderDetailsRepository.findAllByOrder(order);
                    List<OrderDetailsResponseDto> orderDetailsDtoList = orderDetailsMapper.orderDetailsListToOrderDetailsResponseDtoList(orderDetailsList);
                    return orderMapper.toOrderResponseDto(order, order.getDeliveryAddress(), orderDetailsDtoList);
                })
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND));
    }

    /**
     * Retrieves orders by their status and maps them to a list of {@link OrderStatusResponseDto}.
     * *
     * This method fetches orders from the database with the specified status. If orders are found,
     * it maps each order along with its associated user ID to an {@link OrderStatusResponseDto}.
     * If no orders are found with the given status, it throws an {@link EntityInPizzeriaNotFoundException}.
     *
     * @param statusOrder the status of the orders to retrieve
     * @return a list of {@link OrderStatusResponseDto} representing the orders with the specified status
     * @throws EntityInPizzeriaNotFoundException if no orders are found with the specified status
     */
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

    /**
     * Retrieves orders within a specified period and maps them to a list of {@link OrderStatusResponseDto}.
     * *
     * This method fetches orders from the database that fall within the specified period, defined by the start date
     * and end date inclusively. If the start date is after the end date, it throws a {@link NotCorrectArgumentException}.
     *
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return a list of {@link OrderStatusResponseDto} representing the orders within the specified period
     * @throws NotCorrectArgumentException if the start date is after the end date
     */
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
