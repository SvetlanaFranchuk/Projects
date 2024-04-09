package org.example.pizzeria.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.user.UserCreateException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private JwtService jwtService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }


    @Test
    void getUserById() throws Exception {
        Long userId = 1L;
        when(userService.getUser(userId)).thenReturn(TestData.USER_RESPONSE_DTO);

        mockMvc.perform(get("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("IvanAdmin"))
                .andExpect(jsonPath("$.email").value("iv.admin@pizzeria.com"));
    }

    @Test
    public void getUserById_UserNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        doThrow(new EntityInPizzeriaNotFoundException("User", ErrorMessage.INVALID_ID))
                .when(userService).getUser(1234L);
        mockMvc.perform(get("/user/1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser() throws Exception {
        when(userService.update(1L, TestData.USER_REQUEST_DTO)).thenReturn(TestData.USER_RESPONSE_DTO);

        mockMvc.perform(put("/user/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.USER_REQUEST_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("IvanAdmin"))
                .andExpect(jsonPath("$.email").value("iv.admin@pizzeria.com"));
    }

    @Test
    public void updateUser_UserNotFound_TrowEntityInPizzeriaNotFoundException() throws Exception {
        when(userService.update(1L, TestData.USER_REQUEST_DTO)).thenThrow(new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));

        mockMvc.perform(put("/user/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.USER_REQUEST_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_NothingToUpdate() throws Exception {
        mockMvc.perform(put("/user/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

}