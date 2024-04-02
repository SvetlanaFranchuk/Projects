package org.example.pizzeria.controller.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.OrderController;
import org.example.pizzeria.dto.order.OrderRequestDto;
import org.example.pizzeria.dto.order.OrderResponseDto;
import org.example.pizzeria.dto.order.OrderStatusResponseDto;
import org.example.pizzeria.entity.order.StatusOrder;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.order.InvalidOrderStatusException;
import org.example.pizzeria.service.order.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OrderServiceImpl orderService;

    @Test
    void updateOrderAndOrderDetails() throws Exception {
        Long orderId = 1L;
        Map<Long, Integer> pizzaIdToCount = new HashMap<>();
        pizzaIdToCount.put(1L, 2);
        OrderRequestDto orderRequestDto = new OrderRequestDto(TestData.EXPECTED_DATE_TIME,
                TestData.DELIVERY_ADDRESS_NEW.getCity(),
                TestData.DELIVERY_ADDRESS_NEW.getStreetName(),
                TestData.DELIVERY_ADDRESS_NEW.getHouseNumber(),
                TestData.DELIVERY_ADDRESS_NEW.getApartmentNumber(), pizzaIdToCount);
        when(orderService.updateOrderAndOrderDetails(orderId, orderRequestDto)).thenReturn(TestData.ORDER_RESPONSE_DTO);

        mockMvc.perform(patch("/order/updateOrderAndOrderDetails/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryStreetName").value(TestData.ORDER_RESPONSE_DTO.deliveryStreetName()))
                .andExpect(jsonPath("$.sum").value(TestData.ORDER_RESPONSE_DTO.sum()));
    }

    @Test
    void updateOrderAndOrderDetails_OrderNotFound() throws Exception {
        Long orderId = 1L;
        Map<Long, Integer> pizzaIdToCount = new HashMap<>();
        pizzaIdToCount.put(1L, 2);
        OrderRequestDto orderRequestDto = new OrderRequestDto(TestData.EXPECTED_DATE_TIME,
                TestData.DELIVERY_ADDRESS_NEW.getCity(),
                TestData.DELIVERY_ADDRESS_NEW.getStreetName(),
                TestData.DELIVERY_ADDRESS_NEW.getHouseNumber(),
                TestData.DELIVERY_ADDRESS_NEW.getApartmentNumber(), pizzaIdToCount);
        when(orderService.updateOrderAndOrderDetails(orderId, orderRequestDto))
                .thenThrow(new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND));
        mockMvc.perform(patch("/order/updateOrderAndOrderDetails/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrder() throws Exception {
        Long orderId = 1L;
        doNothing().when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/order/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().string("Order deleted successfully"));
    }

    @Test
    void deleteOrder_OrderNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        Long orderId = 1L;
        doThrow(new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND))
                .when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/order/{id}", orderId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Order" + ErrorMessage.ENTITY_NOT_FOUND));
    }

    @Test
    void deleteOrder_InvalidStatus_ThrowInvalidOrderStatusException() throws Exception {
        Long orderId = 1L;
        doThrow(new InvalidOrderStatusException(ErrorMessage.INVALID_STATUS_ORDER_FOR_DELETE))
                .when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/order/{id}", orderId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(ErrorMessage.INVALID_STATUS_ORDER_FOR_DELETE));
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
                        .contentType(MediaType.APPLICATION_JSON))
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
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    void getAllOrdersByUser() throws Exception {
        Long userId = 1L;
        List<OrderResponseDto> orderResponseDtoList = List.of(TestData.ORDER_RESPONSE_DTO);
        when(orderService.getAllOrdersByUser(userId)).thenReturn(orderResponseDtoList);

        mockMvc.perform(get("/order/getAllByUser/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(orderResponseDtoList.size()));
    }

    @Test
    void getAllOrdersByUser_EmptyList() throws Exception {
        Long userId = 1L;
        List<OrderResponseDto> orderResponseDtoList = Collections.emptyList();
        when(orderService.getAllOrdersByUser(userId)).thenReturn(orderResponseDtoList);

        mockMvc.perform(get("/order/getAllByUser/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void getOrderById() throws Exception {
        Long orderId = 1L;
        OrderResponseDto orderResponseDto = TestData.ORDER_RESPONSE_DTO;
        when(orderService.getOrderByUser(orderId)).thenReturn(orderResponseDto);

        mockMvc.perform(get("/order/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderResponseDto.id()))
                .andExpect(jsonPath("$.sum").value(orderResponseDto.sum()));
    }

    @Test
    void getOrderById_OrderNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        Long orderId = 1L;
        when(orderService.getOrderByUser(orderId)).thenThrow(new EntityInPizzeriaNotFoundException("Order", ErrorMessage.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/order/{orderId}", orderId))
                .andExpect(status().isBadRequest());
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
                        .contentType(MediaType.APPLICATION_JSON))
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
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(orderStatusResponseDtos.size()));
    }
    @Test
    void getOrdersByPeriod_InvalidDate() throws Exception {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now();

        mockMvc.perform(get("/order/period")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}