package org.example.pizzeria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.statistic.ProfitReportDto;
import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.order.StatusOrder;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.repository.order.OrderRepository;
import org.example.pizzeria.service.auth.JwtService;
import org.example.pizzeria.service.statistic.StatisticServiceImpl;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class StatisticControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private StatisticServiceImpl statisticService;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private OrderRepository orderRepository;
    private String jwtToken;
    @BeforeEach
    void setUp() {
        Mockito.reset(orderRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(new StatisticController(statisticService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }
    @Test
    void getProfitInformation() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        when(statisticService.getProfitInformation(any(), any())).thenReturn(new ProfitReportDto(TestData.EXPECTED_SUM, 10.0));

        mockMvc.perform(get("/stat/getProfitInformation")
                        .param("startDate", startDate.format(DateTimeFormatter.ISO_DATE))
                        .param("endDate", endDate.format(DateTimeFormatter.ISO_DATE))
                        .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.salesAmount").isNumber())
                .andExpect(jsonPath("$.foodAmount").isNumber());
    }

    @Test
    void getIngredientConsumptionInfo() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        mockMvc.perform(get("/stat/getIngredientConsumptionInfo")
                        .param("startDate", startDate.format(DateTimeFormatter.ISO_DATE))
                        .param("endDate", endDate.format(DateTimeFormatter.ISO_DATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCountOrdersInfo() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        mockMvc.perform(get("/stat/getCountOrdersInfo")
                        .param("startDate", startDate.format(DateTimeFormatter.ISO_DATE))
                        .param("endDate", endDate.format(DateTimeFormatter.ISO_DATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAverageGrade() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 5, 31);

        mockMvc.perform(get("/stat/getAverageGrade")
                        .param("startDate", startDate.format(DateTimeFormatter.ISO_DATE))
                        .param("endDate", endDate.format(DateTimeFormatter.ISO_DATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    void getPopularityPizzasInfo() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        mockMvc.perform(get("/stat/getPopularityPizzasInfo")
                        .param("startDate", startDate.format(DateTimeFormatter.ISO_DATE))
                        .param("endDate", endDate.format(DateTimeFormatter.ISO_DATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
    }
