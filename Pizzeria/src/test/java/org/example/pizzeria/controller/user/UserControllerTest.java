package org.example.pizzeria.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ExceptionHandlerController;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    void getBonus() throws Exception {
        when(userService.getBonus(1L)).thenReturn(TestData.USER_BONUS_DTO);

        mockMvc.perform(get("/user/getBonus/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countOrders").value(TestData.USER_BONUS_DTO.getCountOrders()))
                .andExpect(jsonPath("$.sumOrders").value(TestData.USER_BONUS_DTO.getSumOrders()));
    }

    @Test
    void getBonus_UserNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        long userId = 999L;
        doThrow(EntityInPizzeriaNotFoundException.class).when(userService).getBonus(userId);

        mockMvc.perform(get("/user/getBonus/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBonus() throws Exception {
        when(userService.updateBonus(1L, 20, 375))
                .thenReturn(TestData.USER_NEW_BONUS_DTO);
        mockMvc.perform(put("/user/updateBonus/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("count", String.valueOf(20))
                        .param("sum", String.valueOf(375.0))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countOrders").value(TestData.USER_NEW_BONUS_DTO.getCountOrders()))
                .andExpect(jsonPath("$.sumOrders").value(TestData.USER_NEW_BONUS_DTO.getSumOrders()));
    }

    @Test
    void updateBonus_UserNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        long userId = 999L;
        doThrow(EntityInPizzeriaNotFoundException.class).when(userService).updateBonus(userId, 20, 375.0);

        mockMvc.perform(put("/user/updateBonus/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("count", String.valueOf(20))
                        .param("sum", String.valueOf(375.0))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
}