package org.example.pizzeria.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughUpdateRequestDto;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.product.DeleteProductException;
import org.example.pizzeria.exception.product.DoughCreateException;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.service.auth.JwtService;
import org.example.pizzeria.service.product.DoughServiceImpl;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoughController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class DoughControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private DoughServiceImpl doughService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DoughController(doughService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    public void addDough() throws Exception {
        when(doughService.add(TestData.DOUGH_REQUEST_DTO)).thenReturn(TestData.DOUGH_RESPONSE_DTO);

        mockMvc.perform(post("/dough/add")
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
    public void add_DuplicateDough_ThrowDoughCreateException() throws Exception {
        when(doughService.add(TestData.DOUGH_REQUEST_DTO))
                .thenThrow(new DoughCreateException(ErrorMessage.DOUGH_ALREADY_EXIST));
        mockMvc.perform(post("/dough/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(TestData.DOUGH_REQUEST_DTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        int idDough = 1;
        DoughUpdateRequestDto updateRequestDto = new DoughUpdateRequestDto(idDough, 122, 124);
        when(doughService.update(updateRequestDto, idDough)).thenReturn(TestData.DOUGH_RESPONSE_DTO);
        mockMvc.perform(patch("/dough/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("typeDough").value(TestData.DOUGH_REQUEST_DTO.typeDough().toString()))
                .andExpect(jsonPath("smallWeight").value(TestData.DOUGH_REQUEST_DTO.smallWeight()))
                .andExpect(jsonPath("smallNutrition").value(TestData.DOUGH_REQUEST_DTO.smallNutrition()));
    }

    @Test
    public void update_NonExistingDough_StatusNotFound() throws Exception {
        int idDough = 123;
        DoughUpdateRequestDto doughUpdateRequestDto = new DoughUpdateRequestDto(idDough, 122, 124);
        when(doughService.update(doughUpdateRequestDto, idDough))
                .thenReturn(null);
        mockMvc.perform(patch("/dough/update/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doughUpdateRequestDto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void delete() throws Exception {
        doNothing().when(doughService).delete(123);
        mockMvc.perform(MockMvcRequestBuilders.delete("/dough/delete/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Dough deleted successfully"));
    }

    @Test
    public void delete_DeletingNonExistingDough_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        doThrow(new EntityInPizzeriaNotFoundException("Dough", ErrorMessage.ENTITY_NOT_FOUND))
                .when(doughService).delete(123);
        mockMvc.perform(MockMvcRequestBuilders.delete("/dough/delete/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_DeletingDoughUsedInPizza_ThrowDeleteProductException() throws Exception {
        int doughId = 1;
        doThrow(new DeleteProductException(ErrorMessage.DOUGH_ALREADY_USE_IN_PIZZA))
                .when(doughService).delete(doughId);
        mockMvc.perform(MockMvcRequestBuilders.delete("/dough/delete/{id}", doughId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessage.DOUGH_ALREADY_USE_IN_PIZZA));
    }

    @Test
    void getAllorAdmin() throws Exception {
        when(doughService.getAllForAdmin()).thenReturn(List.of(TestData.DOUGH_RESPONSE_DTO));
        mockMvc.perform(get("/dough/getAllForAdmin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andDo(print())
                .andExpect(jsonPath("$[0].typeDough").value(TestData.DOUGH_RESPONSE_DTO.typeDough().toString()))
                .andExpect(jsonPath("$[0].smallWeight").value(TestData.DOUGH_RESPONSE_DTO.smallWeight()))
                .andExpect(jsonPath("$[0].smallNutrition").value(TestData.DOUGH_RESPONSE_DTO.smallNutrition()))
                .andExpect(jsonPath("$[0].smallPrice").value(TestData.DOUGH_RESPONSE_DTO.smallPrice()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllForAdmin_EmptyList() throws Exception {
        when(doughService.getAllForAdmin()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/dough/getAllForAdmin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllDoughForClient() throws Exception {
        DoughResponseClientDto doughResponseDto = new DoughResponseClientDto(1, TypeDough.NEAPOLITAN, 120, 123);
        when(doughService.getAllForClient()).thenReturn(List.of(doughResponseDto));
        mockMvc.perform(get("/dough/getAllForClient")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andDo(print())
                .andExpect(jsonPath("$[0].typeDough").value("NEAPOLITAN"))
                .andExpect(jsonPath("$[0].smallWeight").value(120))
                .andExpect(jsonPath("$[0].smallNutrition").value(123))
                .andExpect(status().isOk());
    }

    @Test
    void getAllDoughForClient_EmptyList() throws Exception {
        when(doughService.getAllForClient()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/dough/getAllForClient")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


}