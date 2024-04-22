package org.example.pizzeria.controller.benefits;

import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.dto.benefits.FavoritesResponseDto;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.product.PizzaAlreadyInFavoritesException;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.service.auth.JwtService;
import org.example.pizzeria.service.benefits.FavoritesServiceImpl;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoritesController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class FavoritesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private FavoritesServiceImpl favoritesService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FavoritesController(favoritesService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    void addPizzaToUserFavorite() throws Exception {
        Long userId = 1L;
        Long pizzaId = 2L;

        when(favoritesService.addPizzaToUserFavorite(anyLong(), anyLong())).thenReturn(new FavoritesResponseDto(List.of(
                TestData.PIZZA_RESPONSE_DTO, TestData.PIZZA_RESPONSE_DTO_2, TestData.PIZZA_RESPONSE_DTO_3)));

        mockMvc.perform(post("/favorites/addPizzaToUserFavorite")
                        .param("userId", String.valueOf(userId))
                        .param("pizzaId", String.valueOf(pizzaId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isCreated());
    }

    @Test
    void addPizzaToUserFavorite_PizzaAlreadyInFavorites() throws Exception {
        Long userId = 123L;
        Long pizzaId = 2L;

        doThrow(PizzaAlreadyInFavoritesException.class).when(favoritesService).addPizzaToUserFavorite(userId, pizzaId);

        mockMvc.perform(post("/favorites/addPizzaToUserFavorite")
                        .param("userId", String.valueOf(userId))
                        .param("pizzaId", String.valueOf(pizzaId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletePizzaFromUserFavorite() throws Exception {
        Long userId = 123L;
        Long pizzaId = 2L;

        mockMvc.perform(delete("/favorites/deletePizzaFromUserFavorite")
                        .param("userId", String.valueOf(userId))
                        .param("pizzaId", String.valueOf(pizzaId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void deletePizzaFromUserFavorite_UserNotFound() throws Exception {
        Long userId = 9999L;
        Long pizzaId = 2L;

        doThrow(EntityInPizzeriaNotFoundException.class).when(favoritesService).deletePizzaFromUserFavorite(pizzaId, userId);
        mockMvc.perform(delete("/favorites/deletePizzaFromUserFavorite")
                        .param("userId", String.valueOf(userId))
                        .param("pizzaId", String.valueOf(pizzaId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllFavoritePizzaByUser() throws Exception {
        Long userId = 123L;
        when(favoritesService.getAllFavoritePizzaByUser(userId)).thenReturn(List.of(TestData.PIZZA_RESPONSE_DTO));

        mockMvc.perform(get("/favorites/getAllFavoritePizzaByUser/{userId}", userId)
                        .param("userId", String.valueOf(userId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value(TestData.PIZZA_RESPONSE_DTO.getTitle()))
                .andExpect(jsonPath("$[0].description").value(TestData.PIZZA_RESPONSE_DTO.getDescription()));
    }

    @Test
    public void getAllFavoritePizzaByUser_EmptyList() throws Exception {
        Long userId = 123L;
        when(favoritesService.getAllFavoritePizzaByUser(userId)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/favorites/getAllFavoritePizzaByUser/{userId}", userId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}