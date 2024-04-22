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
import org.example.pizzeria.repository.order.OrderDetailsRepository;
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
    public OrderDetailsRepository orderDetailsRepository;

    @Autowired
    public StatisticServiceImpl(EntityManager entityManager, OrderRepository orderRepository, OrderDetailsRepository orderDetailsRepository) {
        this.entityManager = entityManager;
        this.orderRepository = orderRepository;
        this.orderDetailsRepository = orderDetailsRepository;
    }

    /**
     * Retrieves profit information for a specified period.
     * *
     * This method calculates and retrieves profit information for the specified period, including total sales
     * amount and food cost. It returns a ProfitReportDto object containing the calculated profit information.
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @return a ProfitReportDto object containing the calculated profit information
     */
    @Override
    public ProfitReportDto getProfitInformation(LocalDate startDate, LocalDate endDate) {
        double salesAmount = 0.0;
        double foodAmount = 0.0;
        LocalDateTime startDateTimeAtStartOfDay = startDate.atStartOfDay();
        LocalDateTime endDateTimeAtEndOfDay = endDate.atTime(LocalTime.MAX);
        List<Order> orders = orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDateTimeAtStartOfDay, endDateTimeAtEndOfDay, StatusOrder.PAID);
        for (Order order : orders) {
            salesAmount += order.getSum();
            List<OrderDetails> orderDetails = orderDetailsRepository.findAllByOrder(order);
            for (OrderDetails orderDetail : orderDetails) {
                Pizza pizza = orderDetail.getPizza();
                foodAmount += pizza.getAmount() / (1.3 * pizza.getSize().getCoefficient() * orderDetail.getQuantity());
            }
        }
        return new ProfitReportDto(salesAmount, foodAmount);
    }

    /**
     * Retrieves information on ingredient consumption for a specified period.
     * *
     * This method calculates and retrieves information on ingredient consumption for the specified period, based on the orders placed and the ingredients used in those orders. It returns a list of IngredientConsumptionDto objects containing the calculated information.
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @return a list of IngredientConsumptionDto objects containing the calculated information on ingredient consumption
     */
    @Override
    public List<IngredientConsumptionDto> getIngredientConsumptionInfo(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), StatusOrder.PAID);
        List<OrderDetails> orderDetails = orders.stream()
                .flatMap(order -> orderDetailsRepository.findAllByOrder(order).stream())
                .toList();
        Map<Ingredient, Integer> ingredientToTotalWeight = new HashMap<>();
        for (OrderDetails orderDetail : orderDetails) {
            List<Ingredient> ingredients = orderDetail.getPizza().getIngredientsList();
            for (Ingredient ingredient : ingredients) {
                ingredientToTotalWeight.merge(ingredient, ingredient.getWeight() * orderDetail.getQuantity(), Integer::sum);
            }
        }
        return getingredientConsumptionDtoList(ingredientToTotalWeight);
    }

    private List<IngredientConsumptionDto> getingredientConsumptionDtoList(Map<Ingredient, Integer> ingredientToTotalWeight){
        List<IngredientConsumptionDto> ingredientConsumptionDtoList = new ArrayList<>();
        for (Map.Entry<Ingredient, Integer> entry : ingredientToTotalWeight.entrySet()) {
            Ingredient ingredient = entry.getKey();
            int totalWeightInGrams = entry.getValue();
            double totalWeightInKilograms = totalWeightInGrams / 1000.0;
            ingredientConsumptionDtoList.add(new IngredientConsumptionDto(ingredient.getId(), ingredient.getName(), totalWeightInKilograms));
        }
        return ingredientConsumptionDtoList;
    }
    /**
     * Retrieves information on the count of orders placed for each day within a specified period.
     * *
     * This method calculates and retrieves information on the count of orders placed for each day within the specified
     * period. It returns a list of CountOrdersDto objects containing the calculated information.
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @return a list of CountOrdersDto objects containing the calculated information on the count of orders placed for
     * period
     */
    @Override
    public List<CountOrdersDto> getCountOrdersInfo(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX),
                StatusOrder.PAID
        );
        Map<LocalDate, Long> ordersByDate = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getOrderDateTime().toLocalDate(),
                        TreeMap::new,
                        Collectors.counting()
                ));
        return ordersByDate.entrySet().stream()
                .map(entry -> new CountOrdersDto(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the average grade of reviews submitted within a specified period.
     * *
     * This method calculates and retrieves the average grade of reviews submitted within the specified period. It returns the calculated average grade as a Double value.
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @return the average grade of reviews submitted within the specified period
     */
    @Override
    public Double getAverageGrade(LocalDate startDate, LocalDate endDate) {
        return entityManager.createQuery(
                        "SELECT AVG(r.grade) FROM Review r " +
                                "WHERE r.reviewDate BETWEEN :startDate AND :endDate", Double.class)
                .setParameter("startDate", startDate.atStartOfDay())
                .setParameter("endDate", endDate.atTime(LocalTime.MAX))
                .getSingleResult();
    }

    /**
     * Retrieves information about the popularity of pizzas within a specified date range.
     * This method calculates the popularity of each pizza based on the quantity ordered within the specified time frame.
     * Popularity is determined by counting the total quantity of each pizza ordered across all orders.
     *
     * @param startDate The start date of the time frame to consider (inclusive).
     * @param endDate   The end date of the time frame to consider (inclusive).
     * @return A list of {@link PopularityPizzasDto} objects representing the popularity of each pizza.
     * Each object contains the ID, title, and total quantity ordered of a pizza.
     */
    @Override
    public List<PopularityPizzasDto> getPopularityPizzasInfo(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), StatusOrder.PAID);
        Map<Pizza, Long> pizzaCountMap = new HashMap<>();
        for (Order order : orders) {
            List<OrderDetails> orderDetails = orderDetailsRepository.findAllByOrder(order);
            for (OrderDetails orderDetail : orderDetails) {
                Pizza pizza = orderDetail.getPizza();
                pizzaCountMap.put(pizza, pizzaCountMap.getOrDefault(pizza, 0L) + orderDetail.getQuantity());
            }
        }
        return pizzaCountMap.entrySet().stream()
                .map(entry -> new PopularityPizzasDto(entry.getKey().getId(), entry.getKey().getTitle(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

}

