package org.example.pizzeria.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.controller.OrderController;
import org.example.pizzeria.dto.order.BasketRequestDto;
import org.example.pizzeria.dto.order.BasketResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaToBasketRequestDto;
import org.example.pizzeria.entity.order.Basket;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.NotCorrectArgumentException;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.service.auth.JwtService;
import org.example.pizzeria.service.order.OrderServiceImpl;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class OrderControllerTest_Basket {
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
    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    void addPizzaToBasket() throws Exception {
        Map<PizzaResponseDto, Integer> pizzaToCount = new HashMap<>();
        pizzaToCount.put(TestData.PIZZA_RESPONSE_DTO, 3);
        BasketResponseDto responseDto = new BasketResponseDto(pizzaToCount, 1L);
        when(orderService.addPizzaToBasket(any(Long.class), any(PizzaToBasketRequestDto.class))).thenReturn(responseDto);
        mockMvc.perform(post("/order/addPizzaToBasket/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PizzaToBasketRequestDto(1L, 3)))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pizzaCountMap.size()").value(responseDto.pizzaCountMap().size()));
    }

    @Test
    void getBasketByUser_EntityNotFound() throws Exception {
        when(orderService.getBasketByUser(anyLong())).thenThrow(new EntityInPizzeriaNotFoundException("Basket", ErrorMessage.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/basket/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
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
                        .content(objectMapper.writeValueAsString(basketRequestDto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pizzaCountMap.size()").value(responseDto.pizzaCountMap().size()));
    }

    @Test
    void changePizzasInBasket_Negative() throws Exception {
        doThrow(new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT))
                .when(orderService).changePizzasInBasket(any(BasketRequestDto.class));
        Map<Long, Integer> pizzaRequestToCount = new HashMap<>();
        pizzaRequestToCount.put(1L, 3);
        BasketRequestDto basketRequestDto = new BasketRequestDto(pizzaRequestToCount, 1L);
        mockMvc.perform(patch("/order/changePizzasInBasket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(basketRequestDto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void moveDetailsBasketToOrder() throws Exception {
        Basket basket = TestData.BASKET;
        when(orderService.moveDetailsBasketToOrder(anyLong())).thenReturn(TestData.ORDER_RESPONSE_DTO);
        mockMvc.perform(post("/order/moveDetailsBasketToOrder/{id}", basket.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isCreated());
    }

    @Test
    void moveDetailsBasketToOrder_EmptyBasket_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        Basket emptyBasket = new Basket();
        emptyBasket.setId(2L);
        when(orderService.moveDetailsBasketToOrder(anyLong())).thenThrow(EntityInPizzeriaNotFoundException.class);
        mockMvc.perform(post("/order/moveDetailsBasketToOrder/{id}", emptyBasket.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }


}