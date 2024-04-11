package org.example.pizzeria.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.controller.ProductController;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseDto;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.product.DeleteProductException;
import org.example.pizzeria.exception.product.IngredientsCreateException;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.repository.product.IngredientRepository;
import org.example.pizzeria.repository.product.PizzaRepository;
import org.example.pizzeria.service.auth.JwtService;
import org.example.pizzeria.service.product.ProductServiceImpl;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class ProductControllerTest_Ingredient {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private IngredientRepository ingredientRepository;
    @MockBean
    private PizzaRepository pizzaRepository;
    @MockBean
    private JwtService jwtService;

    @MockBean
    private ProductServiceImpl productService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        Mockito.reset(ingredientRepository);
        Mockito.reset(pizzaRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    void addIngredient() throws Exception {
        when(productService.addIngredient(TestData.INGREDIENT_REQUEST_DTO)).thenReturn(TestData.INGREDIENT_RESPONSE_DTO);

        mockMvc.perform(post("/product/addIngredient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.INGREDIENT_RESPONSE_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(TestData.INGREDIENT_REQUEST_DTO.name()))
                .andExpect(jsonPath("$.weight").value(TestData.INGREDIENT_REQUEST_DTO.weight()))
                .andExpect(jsonPath("$.nutrition").value(TestData.INGREDIENT_REQUEST_DTO.nutrition()))
                .andExpect(jsonPath("$.price").value(TestData.INGREDIENT_REQUEST_DTO.price()))
                .andExpect(jsonPath("$.groupIngredient").value(TestData.INGREDIENT_REQUEST_DTO.groupIngredient().name()));
    }

    @Test
    public void addIngredient_IngredientAlreadyExists_ThrowIngredientsCreateException() throws Exception {
        ingredientRepository.save(TestData.INGREDIENT_1);
        doReturn(List.of(TestData.INGREDIENT_1))
                .when(ingredientRepository).findAllByNameAndPrice(
                        TestData.INGREDIENT_REQUEST_DTO.name(),
                        TestData.INGREDIENT_REQUEST_DTO.price()
                );
        doThrow(new IngredientsCreateException(ErrorMessage.INGREDIENT_ALREADY_EXIST))
                .when(productService).addIngredient(TestData.INGREDIENT_REQUEST_DTO);
        mockMvc.perform(post("/product/addIngredient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.INGREDIENT_RESPONSE_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateIngredient() throws Exception {
        Long existingIngredientId = 1L;
        when(productService.updateIngredient(TestData.INGREDIENT_REQUEST_DTO_NEW, existingIngredientId)).thenReturn(TestData.INGREDIENT_RESPONSE_DTO_NEW);

        mockMvc.perform(put("/product/updateIngredient/{id}", existingIngredientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.INGREDIENT_RESPONSE_DTO_NEW))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingIngredientId))
                .andExpect(jsonPath("$.name").value("Tomato"))
                .andExpect(jsonPath("$.weight").value(110))
                .andExpect(jsonPath("$.nutrition").value(120))
                .andExpect(jsonPath("$.price").value(0.25))
                .andExpect(jsonPath("$.groupIngredient").value("EXTRA"));
    }

    @Test
    public void updateIngredient_NullId() throws Exception {
        Long nullId = null;
        mockMvc.perform(put("/product/updateIngredient/{id}", nullId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.INGREDIENT_RESPONSE_DTO_NEW))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteIngredient() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/product/deleteIngredient/1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Ingredient deleted successfully"));
    }

    @Test
    public void deleteIngredient_IngredientAlreadyUsedInPizza_ThrowDeleteProductException() throws Exception {
        Long ingredientId = 1L;
        Ingredient ingredient = TestData.INGREDIENT_1;
        Optional<Ingredient> optionalIngredient = Optional.of(ingredient);

        when(ingredientRepository.findById(ingredientId)).thenReturn(optionalIngredient);
        when(pizzaRepository.findAllByIngredientsListIsContaining(ingredient)).thenReturn(List.of(TestData.PIZZA));
        doThrow(new DeleteProductException(ErrorMessage.INGREDIENT_ALREADY_USE_IN_PIZZA))
                .when(productService).deleteIngredient(ingredientId);
        mockMvc.perform(delete("/product/deleteIngredient/{id}", ingredientId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteIngredient_InvalidId() throws Exception {
        when(ingredientRepository.findById(1234L)).thenReturn(Optional.empty());
        doThrow(new InvalidIDException(ErrorMessage.INVALID_ID))
                .when(productService).deleteIngredient(1234L);
        mockMvc.perform(delete("/product/deleteIngredient/1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessage.INVALID_ID));
    }

    @Test
    void getAllIngredientForAdmin() throws Exception {
        when(productService.getAllIngredientForAdmin()).thenReturn(List.of(TestData.INGREDIENT_RESPONSE_DTO));
        mockMvc.perform(get("/product/getAllIngredientForAdmin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Tomato"));
    }

    @Test
    void getAllIngredientByGroup() throws Exception {
        GroupIngredient groupIngredient = GroupIngredient.BASIC;
        List<IngredientResponseDto> expectedResponse = List.of(TestData.INGREDIENT_RESPONSE_DTO);
        when(productService.getAllIngredientByGroup(groupIngredient)).thenReturn(expectedResponse);

        mockMvc.perform(get("/product/getAllIngredientByGroup")
                        .param("groupIngredient", groupIngredient.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Tomato"));
    }

    @Test
    void getAllIngredientByGroup_EmptyList() throws Exception {
        when(productService.getAllIngredientByGroup(GroupIngredient.SAUCE)).thenReturn(Collections.emptyList());
        mockMvc.perform(MockMvcRequestBuilders.get("/product/getAllIngredientByGroup")
                        .param("groupIngredient", GroupIngredient.SAUCE.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllIngredientsForPizza() throws Exception {
        Long pizzaId = 1L;
        List<IngredientResponseClientDto> expectedResponse = List.of(TestData.INGREDIENT_RESPONSE_CLIENT_DTO_1,
                TestData.INGREDIENT_RESPONSE_CLIENT_DTO_2);
        when(productService.getAllIngredientForPizza(pizzaId)).thenReturn(expectedResponse);
        mockMvc.perform(get("/product/{idPizza}/ingredients", pizzaId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Tomato"))
                .andExpect(jsonPath("$[1].name").value("Cheese"));
    }

    @Test
    void getAllIngredientsForPizza_EmptyList() throws Exception {
        when(productService.getAllIngredientForPizza(1L)).thenReturn(Collections.emptyList());
        mockMvc.perform(MockMvcRequestBuilders.get("/product/{idPizza}/ingredients", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}