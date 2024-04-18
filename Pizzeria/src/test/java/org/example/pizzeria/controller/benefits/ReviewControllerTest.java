package org.example.pizzeria.controller.benefits;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.user.UpdateReviewException;
import org.example.pizzeria.exception.user.UserBlockedException;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.service.auth.JwtService;
import org.example.pizzeria.service.benefits.ReviewServiceImpl;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ReviewServiceImpl reviewService;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private JwtService jwtService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReviewController(reviewService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    void add() throws Exception {
        Long userId = 1L;
        when(reviewService.add(TestData.REVIEW_REQUEST_DTO, userId)).thenReturn(TestData.REVIEW_RESPONSE_DTO);

        mockMvc.perform(post("/review/add/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.REVIEW_REQUEST_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value(TestData.REVIEW_RESPONSE_DTO.comment()))
                .andExpect(jsonPath("$.grade").value(TestData.REVIEW_RESPONSE_DTO.grade()));
    }

    @Test
    void add_UserNotFound() throws Exception {
        Long userId = 1L;
        when(reviewService.add(TestData.REVIEW_REQUEST_DTO, userId)).thenThrow(new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));

        mockMvc.perform(post("/review/add/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.REVIEW_REQUEST_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void update() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;
        when(reviewService.update(reviewId, TestData.REVIEW_REQUEST_DTO, userId)).thenReturn(TestData.REVIEW_RESPONSE_DTO);

        mockMvc.perform(patch("/review/update/{id}", reviewId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString((TestData.REVIEW_REQUEST_DTO)))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment", equalTo("Good pizza")))
                .andExpect(jsonPath("$.grade", equalTo(10)));
    }

    @Test
    void update_ReviewNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;
        doThrow(new EntityInPizzeriaNotFoundException("Review", ErrorMessage.ENTITY_NOT_FOUND))
                .when(reviewService).update(reviewId, TestData.REVIEW_REQUEST_DTO, userId);

        mockMvc.perform(patch("/review/update/{id}", reviewId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString((TestData.REVIEW_REQUEST_DTO)))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_UserNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;
        doThrow(new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND)).when(reviewService).update(reviewId, TestData.REVIEW_REQUEST_DTO, userId);

        mockMvc.perform(patch("/review/update/{id}", reviewId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString((TestData.REVIEW_REQUEST_DTO)))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_UserBlocked_ThrowUserBlockedException() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;
        doThrow(new UserBlockedException(ErrorMessage.USER_BLOCKED)).when(reviewService).update(reviewId, TestData.REVIEW_REQUEST_DTO, userId);

        mockMvc.perform(patch("/review/update/{id}", reviewId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString((TestData.REVIEW_REQUEST_DTO)))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_UserNotOwn_ThrowUpdateReviewException() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;
        doThrow(new UpdateReviewException(ErrorMessage.CANT_REVIEW_UPDATED)).when(reviewService).update(reviewId, TestData.REVIEW_REQUEST_DTO, userId);

        mockMvc.perform(patch("/review/update/{id}", reviewId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString((TestData.REVIEW_REQUEST_DTO)))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete() throws Exception {
        Long reviewId = 1L;
        doNothing().when(reviewService).delete(anyLong());
        mockMvc.perform(MockMvcRequestBuilders.delete("/review/delete/{id}", reviewId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Review deleted successfully"));
    }

    @Test
    void delete_ReviewNotFound_ThrowNotFoundException() throws Exception {
        Long reviewId = 1L;
        doThrow(new InvalidIDException(ErrorMessage.INVALID_ID))
                .when(reviewService).delete(reviewId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/review/delete/{id}", reviewId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll() throws Exception {
        when(reviewService.getAll()).thenReturn(List.of(TestData.REVIEW_RESPONSE_DTO));

        mockMvc.perform(get("/review/getAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].comment").value(TestData.REVIEW_RESPONSE_DTO.comment()))
                .andExpect(jsonPath("$[0].grade").value(TestData.REVIEW_RESPONSE_DTO.grade()));
    }

    @Test
    void getAll_NegativeScenario_ReturnEmptyReviewList() throws Exception {
        when(reviewService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/review/getAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllByUser() throws Exception {
        Long userId = 1L;
        when(reviewService.getAllByUser(userId)).thenReturn(List.of(TestData.REVIEW_RESPONSE_DTO));

        mockMvc.perform(get("/review/getAllByUser/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].comment").value(TestData.REVIEW_RESPONSE_DTO.comment()))
                .andExpect(jsonPath("$[0].grade").value(TestData.REVIEW_RESPONSE_DTO.grade()));
    }

    @Test
    void getAllByUser_NegativeScenario_ReturnEmptyReviewList() throws Exception {
        Long userId = 1L;
        when(reviewService.getAllByUser(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/review/getAllByUser/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllByPeriod() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(100);
        LocalDateTime endDate = LocalDateTime.now();
        when(reviewService.getAllByPeriod(startDate, endDate)).thenReturn(List.of(TestData.REVIEW_RESPONSE_DTO));

        mockMvc.perform(get("/review/getAllByPeriod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate", String.valueOf(startDate))
                        .param("endDate", String.valueOf(endDate))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].comment").value(TestData.REVIEW_RESPONSE_DTO.comment()))
                .andExpect(jsonPath("$[0].grade").value(TestData.REVIEW_RESPONSE_DTO.grade()));
    }

    @Test
    void getAllByPeriod_NegativeScenario_ReturnEmptyReviewList() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(100);
        LocalDateTime endDate = LocalDateTime.now();
        when(reviewService.getAllByPeriod(startDate, endDate)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/review/getAllByPeriod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate", String.valueOf(startDate))
                        .param("endDate", String.valueOf(endDate))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}