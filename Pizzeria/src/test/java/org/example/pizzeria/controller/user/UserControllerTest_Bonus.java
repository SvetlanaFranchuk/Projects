package org.example.pizzeria.controller.user;

import org.example.pizzeria.TestData;
import org.example.pizzeria.controller.UserController;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest_Bonus {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;

    @Test
    void getUserBonus() throws Exception {
        when(userService.getUserBonus(1L)).thenReturn(TestData.USER_BONUS_DTO);

        mockMvc.perform(get("/user/getBonus/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countOrders").value(TestData.USER_BONUS_DTO.getCountOrders()))
                .andExpect(jsonPath("$.sumOrders").value(TestData.USER_BONUS_DTO.getSumOrders()));
    }

    @Test
    void getUserBonus_UserNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        long userId = 1L;
        when(userService.getUserBonus(anyLong())).thenThrow(EntityInPizzeriaNotFoundException.class);

        mockMvc.perform(get("/user/getBonus/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserBonus() throws Exception {
        when(userService.updateUserBonus(1L, 20, 375))
                .thenReturn(TestData.USER_NEW_BONUS_DTO);
        mockMvc.perform(put("/user/updateBonus/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("count", String.valueOf(20))
                        .param("sum", String.valueOf(375.0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countOrders").value(TestData.USER_NEW_BONUS_DTO.getCountOrders()))
                .andExpect(jsonPath("$.sumOrders").value(TestData.USER_NEW_BONUS_DTO.getSumOrders()));
    }

    @Test
    void updateUserBonus_UserNotFound_ThrowEntityInPizzeriaNotFoundException() throws Exception {
        long userId = 1L;
        when(userService.updateUserBonus(userId, 20, 357)).thenThrow(EntityInPizzeriaNotFoundException.class);

        mockMvc.perform(put("/user/updateBonus/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("count", String.valueOf(20))
                        .param("sum", String.valueOf(375.0)))
                .andExpect(status().isNotFound());
    }
}