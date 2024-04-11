package org.example.pizzeria.service.statistic;

import jakarta.persistence.EntityManager;
import org.example.pizzeria.dto.statistic.CountOrdersDto;
import org.example.pizzeria.dto.statistic.IngredientConsumptionDto;
import org.example.pizzeria.dto.statistic.PopularityPizzasDto;
import org.example.pizzeria.dto.statistic.ProfitReportDto;
import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.order.OrderDetails;
import org.example.pizzeria.entity.order.StatusOrder;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticServiceImpl implements StatisticService {
    public EntityManager entityManager;
    public OrderRepository orderRepository;

    @Autowired
    public StatisticServiceImpl(EntityManager entityManager, OrderRepository orderRepository) {
        this.entityManager = entityManager;
        this.orderRepository = orderRepository;
    }

    @Override
    public ProfitReportDto getProfitInformation(LocalDate startDate, LocalDate endDate) {
        double salesAmount;
        double foodAmount = 0.0;
        LocalDateTime startDateTimeAtStartOfDay = startDate.atStartOfDay();
        LocalDateTime endDateTimeAtEndOfDay = endDate.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDateTimeAtStartOfDay, endDateTimeAtEndOfDay, StatusOrder.PAID);
        List<OrderDetails> orderDetails = orders.stream()
                .map(Order::getOrderDetails).toList();
        for (OrderDetails orderDetail : orderDetails) {
            Pizza pizza = orderDetail.getPizza();
            foodAmount += pizza.getAmount() / (1.3 * pizza.getSize().getCoefficient() * orderDetail.getQuantity());
        }
        salesAmount = orders.stream()
                .mapToDouble(Order::getSum)
                .sum();
        return new ProfitReportDto(salesAmount, foodAmount);
    }

    @Override
    public List<IngredientConsumptionDto> getIngredientConsumptionInfo(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), StatusOrder.PAID);
        List<OrderDetails> orderDetails = orders.stream()
                .map(Order::getOrderDetails).toList();
        Map<Ingredient, Integer> ingredientToWeigh = new HashMap<>();
        for (OrderDetails orderDetail : orderDetails) {
            List<Ingredient> ingredients = orderDetail.getPizza().getIngredientsList();
            for (Ingredient ingredient : ingredients) {
                ingredientToWeigh.put(ingredient, ingredient.getWeight());
            }
        }
        Map<Ingredient, Integer> IngredientToTotalWeight = new HashMap<>();
        for (Map.Entry<Ingredient, Integer> entry : ingredientToWeigh.entrySet()) {
            Ingredient ingredient = entry.getKey();
            int weight = entry.getValue();
            IngredientToTotalWeight.merge(ingredient, weight, Integer::sum);
        }
        List<IngredientConsumptionDto> ingredientConsumptionDtoList = new ArrayList<>();
        for (Map.Entry<Ingredient, Integer> entry : IngredientToTotalWeight.entrySet()) {
            ingredientConsumptionDtoList.add(new IngredientConsumptionDto(entry.getKey().getId(),
                    entry.getKey().getName(), entry.getValue() / 1000));
        }
        return ingredientConsumptionDtoList;
    }

    @Override
    public List<CountOrdersDto> getCountOrdersInfo(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), StatusOrder.PAID);
        Map<LocalDate, Long> ordersByDate = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getOrderDateTime().toLocalDate(), Collectors.counting()));
        List<Map.Entry<LocalDate, Long>> sortedOrdersByDate = ordersByDate.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .toList();
        return sortedOrdersByDate.stream()
                .map(entry -> new CountOrdersDto(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageGrade(LocalDate startDate, LocalDate endDate) {
        return entityManager.createQuery(
                        "SELECT AVG(r.grade) FROM Review r " +
                                "WHERE r.reviewDate BETWEEN :startDate AND :endDate", Double.class)
                .setParameter("startDate", startDate.atStartOfDay())
                .setParameter("endDate", endDate.atTime(LocalTime.MAX))
                .getSingleResult();
    }

    @Override
    public List<PopularityPizzasDto> getPopularityPizzasInfo(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), StatusOrder.PAID);
        List<OrderDetails> orderDetails = orders.stream()
                .map(Order::getOrderDetails).toList();
        List<Pizza> pizzas = new ArrayList<>();
        for (OrderDetails orderDetail : orderDetails) {
            for (int i = 0; i < orderDetail.getQuantity(); i++) {
                pizzas.add(orderDetail.getPizza());
            }
        }
        Map<Pizza, Long> pizzaCountMap = pizzas.stream()
                .collect(Collectors.groupingBy(pizza -> pizza, Collectors.counting()));
        return pizzaCountMap.entrySet().stream()
                .map(entry -> new PopularityPizzasDto(entry.getKey().getId(), entry.getKey().getTitle(), entry.getValue().intValue()))
                .toList();
    }
}

