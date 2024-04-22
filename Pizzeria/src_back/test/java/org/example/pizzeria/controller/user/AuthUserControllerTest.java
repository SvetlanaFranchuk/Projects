package org.example.pizzeria.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.dto.user.auth.UserLoginFormRequestDto;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.user.UnauthorizedException;
import org.example.pizzeria.exception.user.UserCreateException;
import org.example.pizzeria.filter.JwtAuthenticationFilter;
import org.example.pizzeria.mapper.user.UserMapper;
import org.example.pizzeria.repository.user.UserRepository;
import org.example.pizzeria.service.auth.AuthenticationService;
import org.example.pizzeria.service.auth.JwtService;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthUserController.class)
@ContextConfiguration(classes = {JwtAuthenticationFilter.class, JwtService.class})
class AuthUserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserMapper userMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthUserController(authenticationService))
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        jwtToken = "generated_jwt_token";
        when(jwtService.generateToken(any())).thenReturn(jwtToken);
    }

    @Test
    void registration() throws Exception {
        doReturn(TestData.JWT_AUTHENTICATION_RESPONSE).when(authenticationService).registration(TestData.USER_REGISTER_REQUEST_DTO);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.USER_REGISTER_REQUEST_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value(Role.ROLE_ADMIN.toString()));
    }

    @Test
    void registration_UnsuccessfulRegistration_ThrowException() throws Exception {
        doThrow(UserCreateException.class).when(authenticationService).registration(TestData.USER_REGISTER_REQUEST_DTO);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.USER_REGISTER_REQUEST_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void authentication() throws Exception {
        when(authenticationService.authentication(any(UserLoginFormRequestDto.class))).thenReturn(TestData.JWT_AUTHENTICATION_RESPONSE);
        mockMvc.perform(post("/auth/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.USER_LOGIN_FORM_REQUEST_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.userResponseDto").isNotEmpty())
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    void testInvalidAuthentication() throws Exception {
        doThrow(UnauthorizedException.class).when(authenticationService).authentication(any());
        mockMvc.perform(post("/auth/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserLoginFormRequestDto("invalidUsername", "invalidPassword")))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser() throws Exception {
        when(authenticationService.update(1L, TestData.USER_REQUEST_DTO)).thenReturn(TestData.USER_RESPONSE_DTO);

        mockMvc.perform(put("/auth/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.USER_REQUEST_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("IvanAdmin"))
                .andExpect(jsonPath("$.email").value("iv.admin@pizzeria.com"));
    }

    @Test
    public void updateUser_UserNotFound_TrowEntityInPizzeriaNotFoundException() throws Exception {
        when(authenticationService.update(1L, TestData.USER_REQUEST_DTO)).thenThrow(new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));

        mockMvc.perform(put("/auth/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestData.USER_REQUEST_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_NothingToUpdate() throws Exception {
        mockMvc.perform(put("/auth/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }
}