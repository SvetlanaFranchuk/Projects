package org.example.pizzeria.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.controller.ProductController;
import org.example.pizzeria.dto.product.dough.DoughCreateRequestDto;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.dto.product.dough.DoughUpdateRequestDto;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.product.DeleteProductException;
import org.example.pizzeria.exception.product.DoughCreateException;
import org.example.pizzeria.service.product.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest_Dough {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductServiceImpl productService;

    @Test
    public void addDough_Successfully() throws Exception {
        DoughCreateRequestDto newDough = new DoughCreateRequestDto(TypeDough.NEAPOLITAN, 120, 123, 1.63);
        DoughResponseDto doughResponseDto = new DoughResponseDto(1, TypeDough.NEAPOLITAN, 120, 123, 1.63);
        when(productService.addDough(newDough)).thenReturn(doughResponseDto);
        mockMvc.perform(post("/product/addDough").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doughResponseDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("typeDough").value("NEAPOLITAN"))
                .andExpect(jsonPath("smallWeight").value(120))
                .andExpect(jsonPath("smallNutrition").value(123))
                .andExpect(jsonPath("smallPrice").value(1.63));
    }

    @Test
    public void addDough_DuplicateDough_ThrowDoughCreateException() throws Exception {
        DoughCreateRequestDto newDough = new DoughCreateRequestDto(TypeDough.NEAPOLITAN, 120, 123, 1.63);
        when(productService.addDough(newDough))
                .thenThrow(new DoughCreateException(ErrorMessage.DOUGH_ALREADY_EXIST));
        mockMvc.perform(post("/product/addDough")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDough)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessage.DOUGH_ALREADY_EXIST));
    }

    @Test
    void updateDough() throws Exception {
        int idDough = 1;
        DoughUpdateRequestDto updateRequestDto = new DoughUpdateRequestDto(idDough, 122, 124);
        DoughResponseDto doughResponseDto = new DoughResponseDto(idDough, TypeDough.NEAPOLITAN, 122, 124, 1.63);
        when(productService.updateDough(updateRequestDto, idDough)).thenReturn(doughResponseDto);
        mockMvc.perform(patch("/product/updateDough/1").contentType(MediaType.APPLICATION_JSON) // Заменяем put на patch
                        .content(objectMapper.writeValueAsString(doughResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("typeDough").value("NEAPOLITAN"))
                .andExpect(jsonPath("smallWeight").value(122))
                .andExpect(jsonPath("smallNutrition").value(124))
                .andExpect(jsonPath("smallPrice").value(1.63));
    }

    @Test
    public void updateDough_NonExistingDough_StatusNotFound() throws Exception {
        int idDough = 123;
        DoughUpdateRequestDto doughUpdateRequestDto = new DoughUpdateRequestDto(idDough, 122, 124);
        when(productService.updateDough(doughUpdateRequestDto, idDough))
                .thenReturn(null);
        mockMvc.perform(patch("/product/updateDough/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doughUpdateRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDough() throws Exception {
        doNothing().when(productService).deleteDough(123);
        mockMvc.perform(delete("/product/deleteDough/123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Dough deleted successfully"));
    }

    @Test
    public void deleteDough_DeletingNonExistingDough_ThrowEntityInPizzeriaNotFoundException () throws Exception {
        doThrow(new EntityInPizzeriaNotFoundException("Dough",ErrorMessage.INVALID_ID))
                .when(productService).deleteDough(123);
        mockMvc.perform(delete("/product/deleteDough/123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dough " + ErrorMessage.INVALID_ID));
    }

    @Test
    public void deleteDough_DeletingDoughUsedInPizza_ThrowDeleteProductException() throws Exception {
        int doughId = 1;
        doThrow(new DeleteProductException(ErrorMessage.DOUGH_ALREADY_USE_IN_PIZZA))
                .when(productService)
                .deleteDough(doughId);
        mockMvc.perform(delete("/product/deleteDough/{id}", doughId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessage.DOUGH_ALREADY_USE_IN_PIZZA));
    }

    @Test
    void getAllDoughForAdmin() throws Exception {
        DoughResponseDto doughResponseDto = new DoughResponseDto(1, TypeDough.NEAPOLITAN, 120, 123, 1.63);
        when(productService.getAllDoughForAdmin()).thenReturn(List.of(doughResponseDto));
        mockMvc.perform(get("/product/getAllDoughForAdmin"))
                .andDo(print())
                .andExpect(jsonPath("$[0].typeDough").value("NEAPOLITAN"))
                .andExpect(jsonPath("$[0].smallWeight").value(120))
                .andExpect(jsonPath("$[0].smallNutrition").value(123))
                .andExpect(jsonPath("$[0].smallPrice").value(1.63))
                .andExpect(status().isOk());
    }

    @Test
    void getAllDoughForAdmin_EmptyList() throws Exception {
        when(productService.getAllDoughForAdmin()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/product/getAllDoughForAdmin"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllDoughForClient() throws Exception {
        DoughResponseClientDto doughResponseDto = new DoughResponseClientDto(1, TypeDough.NEAPOLITAN, 120, 123);
        when(productService.getAllDoughForClient()).thenReturn(List.of(doughResponseDto));
        mockMvc.perform(get("/product/getAllDoughForClient"))
                .andDo(print())
                .andExpect(jsonPath("$[0].typeDough").value("NEAPOLITAN"))
                .andExpect(jsonPath("$[0].smallWeight").value(120))
                .andExpect(jsonPath("$[0].smallNutrition").value(123))
                .andExpect(status().isOk());
    }

    @Test
    void getAllDoughForClient_EmptyList() throws Exception {
        when(productService.getAllDoughForClient()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/product/getAllDoughForClient"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


}