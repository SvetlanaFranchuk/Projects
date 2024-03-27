package org.example.pizzeria.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ProductController;
import org.example.pizzeria.dto.benefits.FavoritesResponseDto;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.product.PizzaAlreadyInFavoritesException;
import org.example.pizzeria.service.product.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest_Favorites {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductServiceImpl productService;

    @Test
    void addPizzaToUserFavorite() throws Exception {
        Long userId = 1L;
        Long pizzaId = 2L;

        when(productService.addPizzaToUserFavorite(anyLong(), anyLong())).thenReturn(new FavoritesResponseDto(List.of(
                TestData.PIZZA_RESPONSE_DTO, TestData.PIZZA_RESPONSE_DTO_2, TestData.PIZZA_RESPONSE_DTO_3)));

        mockMvc.perform(post("/product/addPizzaToUserFavorite")
                        .param("userId", String.valueOf(userId))
                        .param("pizzaId", String.valueOf(pizzaId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void addPizzaToUserFavorite_PizzaAlreadyInFavorites() throws Exception {
        Long userId = 123L;
        Long pizzaId = 2L;

        doThrow(PizzaAlreadyInFavoritesException.class).when(productService).addPizzaToUserFavorite(userId, pizzaId);

        mockMvc.perform(post("/product/addPizzaToUserFavorite")
                        .param("userId", String.valueOf(userId))
                        .param("pizzaId", String.valueOf(pizzaId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletePizzaFromUserFavorite() throws Exception {
        Long userId = 123L;
        Long pizzaId = 2L;

        mockMvc.perform(delete("/product/deletePizzaFromUserFavorite")
                        .param("userId", String.valueOf(userId))
                        .param("pizzaId", String.valueOf(pizzaId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deletePizzaFromUserFavorite_UserNotFound() throws Exception {
        Long userId = 9999L;
        Long pizzaId = 2L;

        doThrow(EntityInPizzeriaNotFoundException.class).when(productService).deletePizzaFromUserFavorite(pizzaId, userId);
        mockMvc.perform(delete("/product/deletePizzaFromUserFavorite")
                        .param("userId", String.valueOf(userId))
                        .param("pizzaId", String.valueOf(pizzaId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllFavoritePizzaByUser() throws Exception {
        Long userId = 123L;
        when(productService.getAllFavoritePizzaByUser(userId)).thenReturn(List.of(TestData.PIZZA_RESPONSE_DTO));

        mockMvc.perform(get("/product/getAllFavoritePizzaByUser/{userId}", userId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value(TestData.PIZZA_RESPONSE_DTO.getTitle()))
                .andExpect(jsonPath("$[0].description").value(TestData.PIZZA_RESPONSE_DTO.getDescription()));
    }

    @Test
    public void getAllFavoritePizzaByUser_EmptyList() throws Exception {
        Long userId = 123L;
        when(productService.getAllFavoritePizzaByUser(userId)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/product/getAllFavoritePizzaByUser/{userId}", userId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}