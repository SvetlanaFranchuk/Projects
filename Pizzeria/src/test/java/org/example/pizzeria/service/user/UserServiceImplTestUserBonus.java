package org.example.pizzeria.service.user;

import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.user.UserBonusDto;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.mapper.user.UserMapper;
import org.example.pizzeria.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTestUserBonus {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        Mockito.reset(userRepository);
    }

    @Test
    void getUserBonus() {
        when(userRepository.getReferenceById(1L)).thenReturn(TestData.USER_APP);
        when(userMapper.toUserBonusDto(TestData.BONUS.getCountOrders(), TestData.BONUS.getSumOrders()))
                .thenReturn(TestData.USER_BONUS_DTO);
        UserBonusDto result = userServiceImpl.getUserBonus(1L);

        assertEquals(TestData.USER_BONUS_DTO, result);
    }

    @Test
    void updateUserBonus() {
        int countToAdd = 20;
        double sumToAdd = 375.0;
        Bonus expectedBonus = new Bonus(30, 500.0);
        when(userRepository.getReferenceById(1L)).thenReturn(TestData.USER_APP);
        when(userRepository.save(any(UserApp.class))).thenReturn(TestData.USER_APP_WITH_NEW_BONUS);
        when(userMapper.toUserBonusDto(expectedBonus.getCountOrders(), expectedBonus.getSumOrders()))
                .thenReturn(TestData.USER_NEW_BONUS_DTO);
        UserBonusDto result = userServiceImpl.updateUserBonus(1L, countToAdd, sumToAdd);

        assertNotNull(result);
        assertEquals(expectedBonus.getCountOrders(), result.getCountOrders());
        assertEquals(expectedBonus.getSumOrders(), result.getSumOrders());
    }

}