package org.example.pizzeria.service.user;

import org.example.pizzeria.dto.user.UserBonusDto;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.user.UserNotFoundException;
import org.example.pizzeria.mapper.user.UserMapper;
import org.example.pizzeria.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTestUserBonus {

    private static final Bonus BONUS = new Bonus(10, 125.0);
    private static final Bonus BONUS_NEW = new Bonus(30, 500.0);
    private static final UserBonusDto USER_BONUS_DTO = new UserBonusDto(10, 125.0);

    private static final UserApp USER_APP = UserApp.builder()
            .id(1L)
            .userName("IvanAdmin")
            .email("iv.admin@pizzeria.com")
            .isBlocked(false)
            .role(Role.CLIENT)
            .bonus(BONUS)
            .build();

    private static final UserApp USER_APP_WITH_NEW_BONUS = UserApp.builder()
            .id(1L)
            .userName("IvanAdmin")
            .email("iv.admin@pizzeria.com")
            .isBlocked(false)
            .role(Role.CLIENT)
            .bonus(BONUS_NEW)
            .build();

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
        when(userRepository.getReferenceById(1L)).thenReturn(USER_APP);
        when(userMapper.toUserBonusDto(BONUS.getCountOrders(), BONUS.getSumOrders()))
                .thenReturn(new UserBonusDto(BONUS.getCountOrders(), BONUS.getSumOrders()));
        UserBonusDto result = userServiceImpl.getUserBonus(1L);

        assertEquals(USER_BONUS_DTO, result);
        assertEquals(BONUS.getCountOrders(), result.getCountOrders());
        assertEquals(BONUS.getSumOrders(), result.getSumOrders());
    }

    @Test
    void getUserBonusUserNotFound() {
        Long userId = 123L;
        when(userRepository.getReferenceById(userId)).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.getUserBonus(123L));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserBonus() {
        int countToAdd = 20;
        double sumToAdd = 375.0;
        Bonus expectedBonus = new Bonus(30, 500.0);
        when(userRepository.getReferenceById(1L)).thenReturn(USER_APP);
        when(userRepository.save(USER_APP_WITH_NEW_BONUS)).thenReturn(USER_APP_WITH_NEW_BONUS);
        when(userMapper.toUserBonusDto(expectedBonus.getCountOrders(), expectedBonus.getSumOrders()))
                .thenReturn(new UserBonusDto(expectedBonus.getCountOrders(), expectedBonus.getSumOrders()));
        UserBonusDto result = userServiceImpl.updateUserBonus(1L, countToAdd, sumToAdd);

        assertNotNull(result);
        assertEquals(expectedBonus.getCountOrders(), result.getCountOrders());
        assertEquals(expectedBonus.getSumOrders(), result.getSumOrders());
    }

    @Test
    void updateUserBonusUserNotFound() {
        Long userId=123L;
        when(userRepository.getReferenceById(userId)).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.updateUserBonus(123L, 20, 375));
        verifyNoMoreInteractions(userRepository);
    }

}