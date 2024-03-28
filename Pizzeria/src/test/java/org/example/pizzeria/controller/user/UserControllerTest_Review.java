package org.example.pizzeria.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.UserController;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.user.UpdateReviewException;
import org.example.pizzeria.exception.user.UserBlockedException;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest_Review {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserServiceImpl userService;

    @Test
    void addReview() throws Exception {
        Long userId = 1L;
        when(userService.addReview(TestData.REVIEW_REQUEST_DTO, userId)).thenReturn(TestData.REVIEW_RESPONSE_DTO);

        mockMvc.perform(post("/user/addReview/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.REVIEW_REQUEST_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value(TestData.REVIEW_RESPONSE_DTO.comment()))
                .andExpect(jsonPath("$.grade").value(TestData.REVIEW_RESPONSE_DTO.grade()));
    }

    @Test
    void addReview_UserNotFound() throws Exception {
        Long userId = 1L;
        when(userService.addReview(TestData.REVIEW_REQUEST_DTO, userId)).thenThrow(new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));

        mockMvc.perform(post("/user/addReview/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.REVIEW_REQUEST_DTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReview() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;
        when(userService.updateReview(reviewId, TestData.REVIEW_REQUEST_DTO, userId)).thenReturn(TestData.REVIEW_RESPONSE_DTO);

        mockMvc.perform(patch("/user/updateReview/{id}", reviewId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString((TestData.REVIEW_REQUEST_DTO))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment", equalTo("Good pizza")))
                .andExpect(jsonPath("$.grade", equalTo(10)));
    }

    @Test
    void updateReview_ReviewNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;
        when(userService.updateReview(reviewId, TestData.REVIEW_REQUEST_DTO, userId))
                .thenThrow(new EntityInPizzeriaNotFoundException("Review", ErrorMessage.ENTITY_NOT_FOUND));

        mockMvc.perform(patch("/user/updateReview/{id}", reviewId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString((TestData.REVIEW_REQUEST_DTO))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReview_UserNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;
        when(userService.updateReview(reviewId, TestData.REVIEW_REQUEST_DTO, userId))
                .thenThrow(new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));

        mockMvc.perform(patch("/user/updateReview/{id}", reviewId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString((TestData.REVIEW_REQUEST_DTO))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReview_UserBlocked_ThrowUserBlockedException() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;
        when(userService.updateReview(reviewId, TestData.REVIEW_REQUEST_DTO, userId))
                .thenThrow(new UserBlockedException(ErrorMessage.USER_BLOCKED));

        mockMvc.perform(patch("/user/updateReview/{id}", reviewId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString((TestData.REVIEW_REQUEST_DTO))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReview_UserBlocked_ThrowUpdateReviewException() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;
        when(userService.updateReview(reviewId, TestData.REVIEW_REQUEST_DTO, userId))
                .thenThrow(new UpdateReviewException(ErrorMessage.CANT_REVIEW_UPDATED));

        mockMvc.perform(patch("/user/updateReview/{id}", reviewId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString((TestData.REVIEW_REQUEST_DTO))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReview() throws Exception {
        Long reviewId = 1L;
        mockMvc.perform(delete("/user/deleteReview/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(content().string("Review deleted successfully"));
    }

    @Test
    void deleteReview_ReviewNotFound_ThrowNotFoundException() throws Exception {
        Long reviewId = 1L;
        doThrow(new InvalidIDException(ErrorMessage.INVALID_ID))
                .when(userService).deleteReview(reviewId);

        mockMvc.perform(delete("/user/deleteReview/{id}", reviewId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllReview() throws Exception {
        when(userService.getAllReview()).thenReturn(List.of(TestData.REVIEW_RESPONSE_DTO));

        mockMvc.perform(get("/user/getAllReview")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].comment").value(TestData.REVIEW_RESPONSE_DTO.comment()))
                .andExpect(jsonPath("$[0].grade").value(TestData.REVIEW_RESPONSE_DTO.grade()));
    }

    @Test
    void getAllReview_NegativeScenario_ReturnEmptyReviewList() throws Exception {
        when(userService.getAllReview()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/getAllReview")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllReviewByUser() throws Exception {
        Long userId = 1L;
        when(userService.getAllReviewByUser(userId)).thenReturn(List.of(TestData.REVIEW_RESPONSE_DTO));

        mockMvc.perform(get("/user/getAllReviewByUser/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].comment").value(TestData.REVIEW_RESPONSE_DTO.comment()))
                .andExpect(jsonPath("$[0].grade").value(TestData.REVIEW_RESPONSE_DTO.grade()));
    }

    @Test
    void getAllReviewByUser_NegativeScenario_ReturnEmptyReviewList() throws Exception {
        Long userId = 1L;
        when(userService.getAllReviewByUser(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/getAllReviewByUser/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllReviewByPeriod() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(100);
        LocalDateTime endDate = LocalDateTime.now();
        when(userService.getAllReviewByPeriod(startDate, endDate)).thenReturn(List.of(TestData.REVIEW_RESPONSE_DTO));

        mockMvc.perform(get("/user/getAllReviewByPeriod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate", String.valueOf(startDate))
                        .param("endDate", String.valueOf(endDate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].comment").value(TestData.REVIEW_RESPONSE_DTO.comment()))
                .andExpect(jsonPath("$[0].grade").value(TestData.REVIEW_RESPONSE_DTO.grade()));
    }

    @Test
    void getAllReviewByPeriod_NegativeScenario_ReturnEmptyReviewList() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(100);
        LocalDateTime endDate = LocalDateTime.now();
        when(userService.getAllReviewByPeriod(startDate, endDate)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/getAllReviewByPeriod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate", String.valueOf(startDate))
                        .param("endDate", String.valueOf(endDate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}