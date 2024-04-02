package org.example.pizzeria.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.UserController;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.user.StatusAlreadyExistsException;
import org.example.pizzeria.exception.user.UserCreateException;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserServiceImpl userService;

    @Test
    void registerUser() throws Exception {
        when(userService.save(TestData.USER_REGISTER_REQUEST_DTO)).thenReturn(TestData.USER_RESPONSE_DTO);

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.USER_REGISTER_REQUEST_DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("IvanAdmin"))
                .andExpect(jsonPath("$.email").value("iv.admin@pizzeria.com"));
    }

    @Test
    void registerUser_UserAlreadyExists_throwUserCreateException() throws Exception {
        when(userService.save(TestData.USER_REGISTER_REQUEST_DTO)).thenThrow(new UserCreateException(ErrorMessage.USER_ALREADY_EXIST));
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.USER_REGISTER_REQUEST_DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessage.USER_ALREADY_EXIST));
    }

    @Test
    void getUserById() throws Exception {
        Long userId = 1L;
        when(userService.getUser(userId)).thenReturn(TestData.USER_RESPONSE_DTO);

        mockMvc.perform(get("/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("IvanAdmin"))
                .andExpect(jsonPath("$.email").value("iv.admin@pizzeria.com"));
    }

    @Test
    public void getUserById_UserNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        doThrow(new EntityInPizzeriaNotFoundException("User", ErrorMessage.INVALID_ID))
                .when(userService).getUser(1234L);
        mockMvc.perform(get("/user/1234")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User " + ErrorMessage.INVALID_ID));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.update(1L, TestData.USER_REQUEST_DTO)).thenReturn(TestData.USER_RESPONSE_DTO);

        mockMvc.perform(put("/user/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.USER_REQUEST_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("IvanAdmin"))
                .andExpect(jsonPath("$.email").value("iv.admin@pizzeria.com"));
    }

    @Test
    public void updateUser_UserNotFound_TrowEntityInPizzeriaNotFoundException() throws Exception {
        when(userService.update(1L, TestData.USER_REQUEST_DTO)).thenThrow(new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));

        mockMvc.perform(put("/user/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.USER_REQUEST_DTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_NothingToUpdate() throws Exception {
        mockMvc.perform(put("/user/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersByBirthday() throws Exception {
        LocalDate date = LocalDate.of(2000, 1, 15);
        when(userService.getUsersByBirthday(date)).thenReturn(List.of(TestData.USER_RESPONSE_DTO));

        mockMvc.perform(get("/user/birthday")
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void getUsersByBirthday_InvalidDate() throws Exception {
        String invalidDate = "invalidDate";

        mockMvc.perform(get("/user/birthday")
                        .param("date", invalidDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUsersByBirthday_Negative_NoUsersFound() throws Exception {
        LocalDate date = LocalDate.of(2000, 1, 1);
        when(userService.getUsersByBirthday(date)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/birthday")
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getUserByClientRole() throws Exception {
        when(userService.getUserByClientRole()).thenReturn(List.of(TestData.USER_RESPONSE_DTO));

        mockMvc.perform(get("/user/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

    }

    @Test
    public void getUserByClientRole_EmptyList() throws Exception {
        when(userService.getUserByClientRole()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/user/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getUserBlocked() throws Exception {
        when(userService.getUserBlocked()).thenReturn(List.of(TestData.USER_BLOCKED_RESPONSE_DTO));

        mockMvc.perform(get("/user/blocked_clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void getUserBlocked_EmptyList() throws Exception {
        when(userService.getUserBlocked()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/user/blocked_clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void changeBlockingUser() throws Exception {
        long userId = 1L;
        boolean isBlocked = true;
        when(userService.changeUserBlocking(userId, isBlocked)).thenReturn(TestData.USER_BLOCKED_RESPONSE_DTO);

        mockMvc.perform(put("/user/change_blocking/1")
                        .param("isBlocked", String.valueOf(isBlocked))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(TestData.USER_BLOCKED_RESPONSE_DTO.getUserName()))
                .andExpect(jsonPath("$.blocked").value(isBlocked));
    }

    @Test
    public void changeBlockingUser_StatusAlreadyExists() throws Exception {
        long userId = 1L;
        boolean isBlocked = true;
        when(userService.changeUserBlocking(userId, isBlocked)).thenThrow(new StatusAlreadyExistsException("User is already blocked"));

        mockMvc.perform(put("/user/change_blocking/1", userId)
                        .param("isBlocked", String.valueOf(isBlocked))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}