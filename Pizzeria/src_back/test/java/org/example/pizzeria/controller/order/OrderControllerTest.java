package org.example.pizzeria.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.controller.OrderController;
import org.example.pizzeria.dto.order.OrderResponseDto;
import org.example.pizzeria.dto.order.OrderStatusResponseDto;
import org.example.pizzeria.entity.order.StatusOrder;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.NotCorrectArgumentException;
import org.example.pizzeria.exception.order.InvalidOrderStatusException;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.repository.order.OrderRepository;
import org.example.pizzeria.service.auth.JwtService;
import org.example.pizzeria.service.order.OrderServiceImpl;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OrderServiceImpl orderService;
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
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    void updateOrderAndOrderDetails() throws Exception {
        Long orderId = 1L;
        when(orderService.updateOrderAndOrderDetails(orderId, TestData.ORDER_REQUEST_DTO)).thenReturn(TestData.ORDER_RESPONSE_DTO);

        mockMvc.perform(patch("/order/updateOrderAndOrderDetails/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.ORDER_REQUEST_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryStreetName").value(TestData.ORDER_RESPONSE_DTO.deliveryStreetName()))
                .andExpect(jsonPath("$.sum").value(TestData.ORDER_RESPONSE_DTO.sum()));
    }

    @Test
    void updateOrderAndOrderDetails_OrderNotFound() throws Exception {
        Long orderId = 1L;
        when(orderService.updateOrderAndOrderDetails(orderId, TestData.ORDER_REQUEST_DTO))
                .thenThrow(new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND));
        mockMvc.perform(patch("/order/updateOrderAndOrderDetails/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.ORDER_REQUEST_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrder() throws Exception {
        Long orderId = 1L;
        doNothing().when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/order/{id}", orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Order deleted successfully"));
    }

    @Test
    void deleteOrder_OrderNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        Long orderId = 1L;
        doThrow(new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND))
                .when(orderService).deleteOrder(orderId);
        mockMvc.perform(delete("/order/{id}", orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteOrder_InvalidStatus_ThrowInvalidOrderStatusException() throws Exception {
        Long orderId = 1L;
        doThrow(new InvalidOrderStatusException(ErrorMessage.INVALID_STATUS_ORDER_FOR_DELETE))
                .when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/order/{id}", orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrderStatus() throws Exception {
        Long orderId = 1L;
        StatusOrder status = StatusOrder.CANCELED;
        OrderStatusResponseDto orderStatusResponseDto = new OrderStatusResponseDto(1L, TestData.EXPECTED_SUM,
                status, LocalDateTime.now(), 1L);
        when(orderService.updateStatusOrder(orderId, status)).thenReturn(orderStatusResponseDto);

        mockMvc.perform(patch("/order/{id}/status", orderId)
                        .param("status", StatusOrder.CANCELED.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusOrder").value(StatusOrder.CANCELED.name()));
    }

    @Test
    void updateOrderStatus_OrderNotFound() throws Exception {
        Long orderId = 1L;
        StatusOrder status = StatusOrder.PAID;
        doThrow(new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND))
                .when(orderService).updateStatusOrder(orderId, status);

        mockMvc.perform(patch("/order/{id}/status", orderId)
                        .param("status", StatusOrder.PAID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllOrdersByUser() throws Exception {
        Long userId = 1L;
        List<OrderResponseDto> orderResponseDtoList = List.of(TestData.ORDER_RESPONSE_DTO);
        when(orderService.getAllOrdersByUser(userId)).thenReturn(orderResponseDtoList);

        mockMvc.perform(get("/order/getAllByUser/{userId}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(orderResponseDtoList.size()));
    }

    @Test
    void getAllOrdersByUser_EmptyList() throws Exception {
        Long userId = 1L;
        List<OrderResponseDto> orderResponseDtoList = Collections.emptyList();
        when(orderService.getAllOrdersByUser(userId)).thenReturn(orderResponseDtoList);

        mockMvc.perform(get("/order/getAllByUser/{userId}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void getOrderById() throws Exception {
        Long orderId = 1L;
        OrderResponseDto orderResponseDto = TestData.ORDER_RESPONSE_DTO;
        when(orderService.getOrderByUser(orderId)).thenReturn(orderResponseDto);

        mockMvc.perform(get("/order/{orderId}", orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderResponseDto.id()))
                .andExpect(jsonPath("$.sum").value(orderResponseDto.sum()));
    }

    @Test
    void getOrderByUser_ReturnEmptyList() throws Exception {
        Long orderId = 1L;
        when(orderService.getOrderByUser(orderId)).thenReturn(null);

        mockMvc.perform(get("/order/{orderId}", orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void getOrdersByStatus() throws Exception {
        StatusOrder status = StatusOrder.NEW;
        OrderStatusResponseDto orderStatusResponseDto = new OrderStatusResponseDto(1L, TestData.EXPECTED_SUM,
                StatusOrder.NEW, LocalDateTime.now(), 1L);
        List<OrderStatusResponseDto> orderStatusResponseDtos = List.of(orderStatusResponseDto);
        when(orderService.getOrderByStatus(status)).thenReturn(orderStatusResponseDtos);

        mockMvc.perform(get("/order/status")
                        .param("status", status.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(orderStatusResponseDtos.size()));

    }

    @Test
    void getOrdersByStatus_OrderNotFound() throws Exception {
        StatusOrder status = StatusOrder.NEW;
        when(orderService.getOrderByStatus(status)).thenThrow(new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/order/status")
                        .param("status", status.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrdersByPeriod() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        OrderStatusResponseDto orderStatusResponseDto = new OrderStatusResponseDto(1L, TestData.EXPECTED_SUM,
                StatusOrder.NEW, LocalDateTime.now(), 1L);
        List<OrderStatusResponseDto> orderStatusResponseDtos = List.of(orderStatusResponseDto);
        when(orderService.getAllOrdersByPeriod(startDate, endDate)).thenReturn(orderStatusResponseDtos);

        mockMvc.perform(get("/order/period")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(orderStatusResponseDtos.size()));
    }

    @Test
    void getOrdersByPeriod_InvalidDate() throws Exception {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now();
        doThrow(new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT))
                .when(orderService).getAllOrdersByPeriod(startDate, endDate);

        mockMvc.perform(get("/order/period")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }
}