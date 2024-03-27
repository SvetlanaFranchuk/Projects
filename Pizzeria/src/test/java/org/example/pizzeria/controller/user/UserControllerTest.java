package org.example.pizzeria.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.ProductController;
import org.example.pizzeria.controller.UserController;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.user.UserCreateException;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                        .content(new ObjectMapper().writeValueAsString(TestData.USER_REGISTER_REQUEST_DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("IvanAdmin"))
                .andExpect(jsonPath("$.email").value("clientTest@pizzeria.com"));
    }

    @Test
    void testRegisterUser_UserAlreadyExists() throws Exception {
       when(userService.save(TestData.USER_REGISTER_REQUEST_DTO)).thenThrow(new UserCreateException(ErrorMessage.USER_ALREADY_EXIST));
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(TestData.USER_REGISTER_REQUEST_DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessage.USER_ALREADY_EXIST));
    }

    @Test
    void getUserById() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void getUsersByBirthday() {
    }

    @Test
    void getUserByClientRole() {
    }

    @Test
    void getUserBlocked() {
    }

    @Test
    void changeBlockingUser() {
    }
}