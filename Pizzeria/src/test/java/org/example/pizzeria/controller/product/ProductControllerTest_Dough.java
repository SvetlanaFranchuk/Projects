package org.example.pizzeria.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.controller.ProductController;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughUpdateRequestDto;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.product.DeleteProductException;
import org.example.pizzeria.exception.product.DoughCreateException;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.service.auth.JwtService;
import org.example.pizzeria.service.product.ProductServiceImpl;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class ProductControllerTest_Dough {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ProductServiceImpl productService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    public void addDough() throws Exception {
        when(productService.addDough(TestData.DOUGH_REQUEST_DTO)).thenReturn(TestData.DOUGH_RESPONSE_DTO);

        mockMvc.perform(post("/product/addDough")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(TestData.DOUGH_RESPONSE_DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("typeDough").value(TestData.DOUGH_REQUEST_DTO.typeDough().toString()))
                .andExpect(jsonPath("smallWeight").value(TestData.DOUGH_REQUEST_DTO.smallWeight()))
                .andExpect(jsonPath("smallNutrition").value(TestData.DOUGH_REQUEST_DTO.smallNutrition()))
                .andExpect(jsonPath("smallPrice").value(TestData.DOUGH_REQUEST_DTO.smallPrice()));
    }

    @Test
    public void addDough_DuplicateDough_ThrowDoughCreateException() throws Exception {
        when(productService.addDough(TestData.DOUGH_REQUEST_DTO))
                .thenThrow(new DoughCreateException(ErrorMessage.DOUGH_ALREADY_EXIST));
        mockMvc.perform(post("/product/addDough")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(TestData.DOUGH_REQUEST_DTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDough() throws Exception {
        int idDough = 1;
        DoughUpdateRequestDto updateRequestDto = new DoughUpdateRequestDto(idDough, 122, 124);
        when(productService.updateDough(updateRequestDto, idDough)).thenReturn(TestData.DOUGH_RESPONSE_DTO);
        mockMvc.perform(patch("/product/updateDough/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("typeDough").value(TestData.DOUGH_REQUEST_DTO.typeDough().toString()))
                .andExpect(jsonPath("smallWeight").value(TestData.DOUGH_REQUEST_DTO.smallWeight()))
                .andExpect(jsonPath("smallNutrition").value(TestData.DOUGH_REQUEST_DTO.smallNutrition()));
    }

    @Test
    public void updateDough_NonExistingDough_StatusNotFound() throws Exception {
        int idDough = 123;
        DoughUpdateRequestDto doughUpdateRequestDto = new DoughUpdateRequestDto(idDough, 122, 124);
        when(productService.updateDough(doughUpdateRequestDto, idDough))
                .thenReturn(null);
        mockMvc.perform(patch("/product/updateDough/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doughUpdateRequestDto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deleteDough() throws Exception {
        doNothing().when(productService).deleteDough(123);
        mockMvc.perform(delete("/product/deleteDough/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Dough deleted successfully"));
    }

    @Test
    public void deleteDough_DeletingNonExistingDough_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        doThrow(new EntityInPizzeriaNotFoundException("Dough", ErrorMessage.ENTITY_NOT_FOUND))
                .when(productService).deleteDough(123);
        mockMvc.perform(delete("/product/deleteDough/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteDough_DeletingDoughUsedInPizza_ThrowDeleteProductException() throws Exception {
        int doughId = 1;
        doThrow(new DeleteProductException(ErrorMessage.DOUGH_ALREADY_USE_IN_PIZZA))
                .when(productService)
                .deleteDough(doughId);
        mockMvc.perform(delete("/product/deleteDough/{id}", doughId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessage.DOUGH_ALREADY_USE_IN_PIZZA));
    }

    @Test
    void getAllDoughForAdmin() throws Exception {
        when(productService.getAllDoughForAdmin()).thenReturn(List.of(TestData.DOUGH_RESPONSE_DTO));
        mockMvc.perform(get("/product/getAllDoughForAdmin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andDo(print())
                .andExpect(jsonPath("$[0].typeDough").value(TestData.DOUGH_RESPONSE_DTO.typeDough().toString()))
                .andExpect(jsonPath("$[0].smallWeight").value(TestData.DOUGH_RESPONSE_DTO.smallWeight()))
                .andExpect(jsonPath("$[0].smallNutrition").value(TestData.DOUGH_RESPONSE_DTO.smallNutrition()))
                .andExpect(jsonPath("$[0].smallPrice").value(TestData.DOUGH_RESPONSE_DTO.smallPrice()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllDoughForAdmin_EmptyList() throws Exception {
        when(productService.getAllDoughForAdmin()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/product/getAllDoughForAdmin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllDoughForClient() throws Exception {
        DoughResponseClientDto doughResponseDto = new DoughResponseClientDto(1, TypeDough.NEAPOLITAN, 120, 123);
        when(productService.getAllDoughForClient()).thenReturn(List.of(doughResponseDto));
        mockMvc.perform(get("/product/getAllDoughForClient")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andDo(print())
                .andExpect(jsonPath("$[0].typeDough").value("NEAPOLITAN"))
                .andExpect(jsonPath("$[0].smallWeight").value(120))
                .andExpect(jsonPath("$[0].smallNutrition").value(123))
                .andExpect(status().isOk());
    }

    @Test
    void getAllDoughForClient_EmptyList() throws Exception {
        when(productService.getAllDoughForClient()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/product/getAllDoughForClient")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


}