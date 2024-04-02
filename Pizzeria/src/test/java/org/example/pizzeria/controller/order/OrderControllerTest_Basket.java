package org.example.pizzeria.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.OrderController;
import org.example.pizzeria.dto.order.BasketRequestDto;
import org.example.pizzeria.dto.order.BasketResponseDto;
import org.example.pizzeria.dto.order.OrderResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.order.Basket;
import org.example.pizzeria.entity.order.StatusOrder;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.NotCorrectArgumentException;
import org.example.pizzeria.service.order.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest_Basket {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OrderServiceImpl orderService;

    @Test
    void addPizzaToBasket() throws Exception {
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 3);
        BasketResponseDto responseDto = new BasketResponseDto(pizzaToCount, 1L);
        when(orderService.addPizzaToBasket(any(Long.class), any(Long.class), any(Integer.class))).thenReturn(responseDto);
        mockMvc.perform(post("/order/addPizzaToBasket/{userId}/{pizzaId}/{countPizza}", 1L, 1L, 3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pizzaCountMap.size()").value(responseDto.pizzaCountMap().size()));
    }

    @Test
    void getBasketByUser() throws Exception {
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 3);
        BasketResponseDto responseDto = new BasketResponseDto(pizzaToCount, 1L);

        when(orderService.getBasketByUser(anyLong())).thenReturn(responseDto);

        mockMvc.perform(get("/order/getBasketByUser/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pizzaCountMap.size()").value(responseDto.pizzaCountMap().size()));
    }

    @Test
    void getBasketByUser_EntityNotFound() throws Exception {
        when(orderService.getBasketByUser(anyLong())).thenThrow(EntityInPizzeriaNotFoundException.class);

        mockMvc.perform(get("/order/getBasketByUser/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePizzasInBasket() throws Exception {
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 3);
        BasketResponseDto responseDto = new BasketResponseDto(pizzaToCount, 1L);
        when(orderService.changePizzasInBasket(any(BasketRequestDto.class))).thenReturn(responseDto);
        Map<Long, Integer> pizzaRequestToCount = new HashMap<>();
        pizzaRequestToCount.put(1L, 3);
        BasketRequestDto basketRequestDto = new BasketRequestDto(pizzaRequestToCount, 1L);
        mockMvc.perform(patch("/order/changePizzasInBasket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(basketRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pizzaCountMap.size()").value(responseDto.pizzaCountMap().size()));
    }

    @Test
    void changePizzasInBasket_Negative() throws Exception {
        when(orderService.changePizzasInBasket(any(BasketRequestDto.class)))
                .thenThrow(new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT));
        Map<Long, Integer> pizzaRequestToCount = new HashMap<>();
        pizzaRequestToCount.put(1L, 3);
        BasketRequestDto basketRequestDto = new BasketRequestDto(pizzaRequestToCount, 1L);
        mockMvc.perform(patch("/order/changePizzasInBasket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(basketRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void moveDetailsBasketToOrder() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedDateTime = now.plusHours(1);
        double expectedSum = (0.2 + 0.4 + 0.23) * 1.3 * 2;
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 2);

        Basket basket = TestData.BASKET;
        OrderResponseDto expectedOrderResponseDto = new OrderResponseDto(1L,
                TestData.DELIVERY_ADDRESS.getCity(),
                TestData.DELIVERY_ADDRESS.getStreetName(),
                TestData.DELIVERY_ADDRESS.getHouseNumber(),
                TestData.DELIVERY_ADDRESS.getApartmentNumber(),
                expectedDateTime,
                expectedSum,
                StatusOrder.NEW,
                null,
                now,
                pizzaToCount,
                1L);

        when(orderService.moveDetailsBasketToOrder(anyLong())).thenReturn(expectedOrderResponseDto);
        mockMvc.perform(post("/order/moveDetailsBasketToOrder/{id}", basket.getId()))
                .andExpect(status().isCreated());
    }
    @Test
    void moveDetailsBasketToOrder_EmptyBasket_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        Basket emptyBasket = new Basket();
        emptyBasket.setId(2L);
        when(orderService.moveDetailsBasketToOrder(anyLong())).thenThrow(EntityInPizzeriaNotFoundException.class);
        mockMvc.perform(post("/order/moveDetailsBasketToOrder/{id}", emptyBasket.getId()))
                .andExpect(status().isBadRequest());
    }


}