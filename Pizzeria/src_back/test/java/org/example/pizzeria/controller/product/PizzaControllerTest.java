package org.example.pizzeria.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.dto.product.pizza.PizzaRequestDto;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.service.auth.JwtService;
import org.example.pizzeria.service.product.PizzaServiceImpl;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PizzaController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class PizzaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PizzaServiceImpl pizzaService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PizzaController(pizzaService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    void add() throws Exception {
        when(pizzaService.add(any(), anyLong())).thenReturn(TestData.PIZZA_RESPONSE_DTO);

        mockMvc.perform(post("/pizza/add")
                        .param("userId", "1")
                        .content(objectMapper.writeValueAsString(TestData.PIZZA_REQUEST_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(TestData.PIZZA_RESPONSE_DTO.getTitle()))
                .andExpect(jsonPath("$.description").value(TestData.PIZZA_RESPONSE_DTO.getDescription()))
                .andExpect(jsonPath("$.dough.id").value(TestData.PIZZA_RESPONSE_DTO.getDough().id()))
                .andExpect(jsonPath("$.ingredientsList").isArray())
                .andExpect(jsonPath("$.ingredientsList.length()").value(TestData.PIZZA_RESPONSE_DTO.getIngredientsList().size()))
                .andExpect(jsonPath("$.ingredientsList[0].name").value(TestData.PIZZA_RESPONSE_DTO.getIngredientsList().getFirst().name()));
    }

    @Test
    public void testAdd_NoUserId() throws Exception {
        mockMvc.perform(post("/pizza/add")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        when(pizzaService.update(any(PizzaRequestDto.class), anyLong()))
                .thenReturn(TestData.PIZZA_RESPONSE_DTO_NEW);

        String requestJson = new ObjectMapper().writeValueAsString(TestData.PIZZA_REQUEST_DTO_NEW);

        mockMvc.perform(put("/pizza/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(TestData.PIZZA_RESPONSE_DTO_NEW.getTitle()))
                .andExpect(jsonPath("$.nutrition").value(TestData.PIZZA_RESPONSE_DTO_NEW.getNutrition()));
    }

    @Test
    public void testUpdate_PizzaNotFound() throws Exception {
        when(pizzaService.update(any(PizzaRequestDto.class), anyLong()))
                .thenReturn(null);
        String requestJson = new ObjectMapper().writeValueAsString(TestData.PIZZA_REQUEST_DTO_NEW);

        mockMvc.perform(put("/pizza/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deletePizzaRecipe() throws Exception {
        doNothing().when(pizzaService).deletePizzaRecipe(anyLong());
        mockMvc.perform(delete("/pizza/deletePizzaRecipe/{id}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Pizza recipe deleted successfully"));
    }

    @Test
    public void deletePizzaRecipe_BadId() throws Exception {
        doThrow(new InvalidIDException(ErrorMessage.INVALID_ID)).when(pizzaService).deletePizzaRecipe(anyLong());

        mockMvc.perform(delete("/pizza/deletePizzaRecipe/999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessage.INVALID_ID));
    }

    @Test
    void getAllPizzaStandardRecipe() throws Exception {
        when(pizzaService.getAllPizzaStandardRecipe()).thenReturn(List.of(TestData.PIZZA_RESPONSE_DTO));

        mockMvc.perform(get("/pizza/getAllPizzaStandardRecipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value(TestData.PIZZA_RESPONSE_DTO.getTitle()));
    }

    @Test
    public void getAllPizzaStandardRecipe_EmptyList() throws Exception {
        when(pizzaService.getAllPizzaStandardRecipe()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/pizza/getAllPizzaStandardRecipe")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllPizzaStandardRecipeByStyles() throws Exception {
        when(pizzaService.getAllPizzaStandardRecipeByStyles(Styles.CLASSIC_ITALIAN)).thenReturn(List.of(TestData.PIZZA_RESPONSE_DTO));
        mockMvc.perform(get("/pizza/getAllPizzaStandardRecipeByStyles")
                        .param("styles", "CLASSIC_ITALIAN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value(TestData.PIZZA_RESPONSE_DTO.getTitle()))
                .andExpect(jsonPath("$[0].description").value(TestData.PIZZA_RESPONSE_DTO.getDescription()));
    }

    @Test
    public void getAllPizzaStandardRecipeByStyles_EmptyList() throws Exception {
        when(pizzaService.getAllPizzaStandardRecipeByStyles(Styles.CLASSIC_ITALIAN)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/pizza/getAllPizzaStandardRecipeByStyles")
                        .param("styles", "CLASSIC_ITALIAN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllPizzaStandardRecipeByTopping() throws Exception {
        when(pizzaService.getAllPizzaStandardRecipeByTopping(ToppingsFillings.CHEESE)).thenReturn(List.of(TestData.PIZZA_RESPONSE_DTO));

        mockMvc.perform(get("/pizza/getAllPizzaStandardRecipeByTopping")
                        .param("toppingsFillings", "CHEESE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value(TestData.PIZZA_RESPONSE_DTO.getTitle()))
                .andExpect(jsonPath("$[0].description").value(TestData.PIZZA_RESPONSE_DTO.getDescription()));
    }

    @Test
    public void getAllPizzaStandardRecipeByTopping_EmptyList() throws Exception {
        when(pizzaService.getAllPizzaStandardRecipeByTopping(ToppingsFillings.CHEESE)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/pizza/getAllPizzaStandardRecipeByTopping")
                        .param("toppingsFillings", "CHEESE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllPizzaStandardRecipeByToppingByStyles() throws Exception {
        when(pizzaService.getAllPizzaStandardRecipeByToppingByStyles(ToppingsFillings.CHEESE, Styles.CLASSIC_ITALIAN))
                .thenReturn(List.of(TestData.PIZZA_RESPONSE_DTO));

        mockMvc.perform(get("/pizza/getAllPizzaStandardRecipeByToppingByStyles")
                        .param("toppingsFillings", "CHEESE")
                        .param("styles", "CLASSIC_ITALIAN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value(TestData.PIZZA_RESPONSE_DTO.getTitle()))
                .andExpect(jsonPath("$[0].description").value(TestData.PIZZA_RESPONSE_DTO.getDescription()));
    }

    @Test
    public void getAllPizzaStandardRecipeByToppingByStyles_EmptyList() throws Exception {
        when(pizzaService.getAllPizzaStandardRecipeByToppingByStyles(ToppingsFillings.CHEESE, Styles.CLASSIC_ITALIAN)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/pizza/getAllPizzaStandardRecipeByToppingByStyles")
                        .param("toppingsFillings", "CHEESE")
                        .param("styles", "CLASSIC_ITALIAN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

}