package org.example.pizzeria.controller.user;

import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.exception.user.StatusAlreadyExistsException;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.service.auth.JwtService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserControllerAdmin.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class UserControllerAdminTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private JwtService jwtService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserControllerAdmin(userService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    void getUsersByBirthday() throws Exception {
        LocalDate date = LocalDate.of(2000, 1, 15);
        String formattedDate = date.format(DateTimeFormatter.ISO_DATE);
        when(userService.getUsersByBirthday(date)).thenReturn(List.of(TestData.USER_RESPONSE_DTO));

        mockMvc.perform(get("/admin/user/birthday")
                        .param("date", formattedDate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void getUsersByBirthday_InvalidDate() throws Exception {
        String invalidDate = "invalidDate";

        mockMvc.perform(get("/admin/user/birthday")
                        .param("date", invalidDate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUsersByBirthday_NoUsersFound() throws Exception {
        LocalDate date = LocalDate.of(2000, 1, 1);
        when(userService.getUsersByBirthday(date)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/user/birthday")
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getUserByClientRole() throws Exception {
        when(userService.getUserByClientRole()).thenReturn(List.of(TestData.USER_RESPONSE_DTO_FOR_ADMIN));

        mockMvc.perform(get("/admin/user/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

    }

    @Test
    public void getUserByClientRole_EmptyList() throws Exception {
        when(userService.getUserByClientRole()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/user/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getUserBlocked() throws Exception {
        when(userService.getUserBlocked()).thenReturn(List.of(TestData.USER_BLOCKED_RESPONSE_DTO));

        mockMvc.perform(get("/admin/user/blocked_clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void getUserBlocked_EmptyList() throws Exception {
        when(userService.getUserBlocked()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/user/blocked_clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void changeBlockingUser() throws Exception {
        long userId = 1L;
        boolean isBlocked = true;
        when(userService.changeUserBlocking(userId, isBlocked)).thenReturn(TestData.USER_BLOCKED_RESPONSE_DTO);

        mockMvc.perform(put("/admin/user/change_blocking/1")
                        .param("isBlocked", String.valueOf(isBlocked))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(TestData.USER_BLOCKED_RESPONSE_DTO.getUserName()))
                .andExpect(jsonPath("$.blocked").value(isBlocked));
    }

    @Test
    public void changeBlockingUser_StatusAlreadyExists() throws Exception {
        long userId = 1L;
        boolean isBlocked = true;
        doThrow(new StatusAlreadyExistsException("User is already blocked"))
                .when(userService).changeUserBlocking(userId, isBlocked);

        mockMvc.perform(put("/admin/user/change_blocking/1")
                        .param("isBlocked", String.valueOf(isBlocked))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }
}