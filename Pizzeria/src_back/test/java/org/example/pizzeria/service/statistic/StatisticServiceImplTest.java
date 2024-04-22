package org.example.pizzeria.service.statistic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.statistic.CountOrdersDto;
import org.example.pizzeria.dto.statistic.IngredientConsumptionDto;
import org.example.pizzeria.dto.statistic.PopularityPizzasDto;
import org.example.pizzeria.dto.statistic.ProfitReportDto;
import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.order.StatusOrder;
import org.example.pizzeria.repository.order.OrderDetailsRepository;
import org.example.pizzeria.repository.order.OrderRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailsRepository orderDetailsRepository;
    @Mock
    public EntityManager entityManager;

    @InjectMocks
    private StatisticServiceImpl statisticService;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderRepository);
        Mockito.reset(orderDetailsRepository);
        statisticService.entityManager = entityManager;
    }

    @Test
    void getProfitInformation() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 5, 31);
        LocalDateTime startDateTimeAtStartOfDay = startDate.atStartOfDay();
        LocalDateTime endDateTimeAtEndOfDay = endDate.atTime(LocalTime.MAX);
        List<Order> orders = List.of(TestData.ORDER_PAID_1);
        when(orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDateTimeAtStartOfDay, endDateTimeAtEndOfDay, StatusOrder.PAID))
                .thenReturn(orders);
        when(orderDetailsRepository.findAllByOrder(TestData.ORDER_PAID_1)).thenReturn(List.of(TestData.ORDER_DETAILS_1));
        ProfitReportDto profitReportDto = statisticService.getProfitInformation(startDate, endDate);
        double sumProfitExpect = TestData.EXPECTED_SUM;
        double sumFoodExpected = TestData.ORDER_DETAILS_1.getPizza().getAmount() / (1.3 * TestData.ORDER_DETAILS_1.getPizza().getSize().getCoefficient() * TestData.ORDER_DETAILS_1.getQuantity());
        assertEquals(sumProfitExpect, profitReportDto.salesAmount());
        assertEquals(sumFoodExpected, profitReportDto.foodAmount());
    }

    @Test
    void getProfitInformation_NoOrders() {
        when(orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        ProfitReportDto profitReportDto = statisticService.getProfitInformation(startDate, endDate);
        assertEquals(0.0, profitReportDto.salesAmount());
        assertEquals(0.0, profitReportDto.foodAmount());
    }

    @Test
    void getIngredientConsumptionInfo() {
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 1, 31);
        LocalDateTime startDateTimeAtStartOfDay = startDate.atStartOfDay();
        LocalDateTime endDateTimeAtEndOfDay = endDate.atTime(LocalTime.MAX);

        when(orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDateTimeAtStartOfDay, endDateTimeAtEndOfDay, StatusOrder.PAID))
                .thenReturn(List.of(TestData.ORDER_PAID));
        when(orderDetailsRepository.findAllByOrder(TestData.ORDER_PAID)).thenReturn(List.of(TestData.ORDER_DETAILS));
        List<IngredientConsumptionDto> result = statisticService.getIngredientConsumptionInfo(startDate, endDate);

        assertEquals(TestData.ORDER_DETAILS.getPizza().getIngredientsList().size(), result.size());
    }

    @Test
    void getIngredientConsumptionInfo_EmptyList() {
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 1, 31);
        LocalDateTime startDateTimeAtStartOfDay = startDate.atStartOfDay();
        LocalDateTime endDateTimeAtEndOfDay = endDate.atTime(LocalTime.MAX);

        when(orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDateTimeAtStartOfDay, endDateTimeAtEndOfDay, StatusOrder.PAID))
                .thenReturn(Collections.emptyList());
        List<IngredientConsumptionDto> result = statisticService.getIngredientConsumptionInfo(startDate, endDate);

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void getCountOrdersInfo() {
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 1, 31);
        LocalDateTime startDateTimeAtStartOfDay = startDate.atStartOfDay();
        LocalDateTime endDateTimeAtEndOfDay = endDate.atTime(LocalTime.MAX);
        when(orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDateTimeAtStartOfDay, endDateTimeAtEndOfDay, StatusOrder.PAID))
                .thenReturn(List.of(TestData.ORDER_PAID));
        List<CountOrdersDto> result = statisticService.getCountOrdersInfo(startDate, endDate);
        assertEquals(1, result.size());
    }

    @Test
    void getCountOrdersInfo_EmptyList() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        LocalDateTime startDateTimeAtStartOfDay = startDate.atStartOfDay();
        LocalDateTime endDateTimeAtEndOfDay = endDate.atTime(LocalTime.MAX);

        when(orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(startDateTimeAtStartOfDay, endDateTimeAtEndOfDay, StatusOrder.PAID))
                .thenReturn(new ArrayList<>());
        List<CountOrdersDto> result = statisticService.getCountOrdersInfo(startDate, endDate);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAverageGrade() {
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 1, 31);
        Double expectedAverageGrade = 4.5;
        TypedQuery<Double> mockTypedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Double.class)))
                .thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(anyString(), any()))
                .thenReturn(mockTypedQuery);
        when(mockTypedQuery.getSingleResult()).thenReturn(expectedAverageGrade);
        Double result = statisticService.getAverageGrade(startDate, endDate);
        assertEquals(expectedAverageGrade, result);
    }

    @Test
    void getPopularityPizzasInfo() {
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 1, 31);
        when(orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(any(), any(), any()))
                .thenReturn(List.of(TestData.ORDER_PAID));

        List<PopularityPizzasDto> result = statisticService.getPopularityPizzasInfo(startDate, endDate);
        assertNotNull(result);
    }

    @Test
    void testGetPopularityPizzasInfo_EmptyList() {
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 1, 31);
        when(orderRepository.findAllByOrderDateTimeBetweenAndStatusOrder(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        List<PopularityPizzasDto> result = statisticService.getPopularityPizzasInfo(startDate, endDate);

        assertEquals(Collections.emptyList(), result);
    }
}