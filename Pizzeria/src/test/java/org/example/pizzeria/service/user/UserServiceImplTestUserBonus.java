package org.example.pizzeria.service.user;

import org.example.pizzeria.dto.user.UserBonusDto;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.user.Role;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTestUserBonus {

    private static final Bonus BONUS = new Bonus(10, 125);
    private static final Bonus BONUS_NEW = new Bonus(30, 500);
    private static final UserBonusDto USER_BONUS_DTO = new UserBonusDto(10, 125);
    private static final UserBonusDto USER_BONUS_DTO_NEW = new UserBonusDto(30, 500);

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
        Long userId = 1L;
        UserApp user = USER_APP;
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(userMapper.toUserBonusDto(user.getBonus())).thenReturn(USER_BONUS_DTO);
        UserBonusDto result = userServiceImpl.getUserBonus(userId);

        assertEquals(USER_BONUS_DTO, result);
        assertEquals(10, result.getCountOrders());
        assertEquals(125, result.getSumOrders());
        verify(userRepository, times(1)).getReferenceById(userId);
        verify(userMapper, times(1)).toUserBonusDto(user.getBonus());
    }

    @Test
    void getUserBonusUserNotFound() {
        Long userId = 123L;
        when(userRepository.getReferenceById(userId)).thenReturn(null);

        UserBonusDto result = userServiceImpl.getUserBonus(userId);
        assertNull(result);
    }

    @Test
    void updateUserBonus() {
        Long userId = 1L;
        UserApp user = USER_APP;
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(userRepository.updateUserAppByBonus(USER_APP, BONUS_NEW)).thenReturn(USER_APP_WITH_NEW_BONUS);
        when(userMapper.toUserBonusDto(user.getBonus())).thenReturn(USER_BONUS_DTO);
        UserBonusDto result = userServiceImpl.updateUserBonus(userId, 20, 375);

        assertEquals(USER_BONUS_DTO_NEW, result);
        assertEquals(30, result.getCountOrders());
        assertEquals(500, result.getSumOrders());
        verify(userRepository, times(1)).getReferenceById(userId);
        verify(userMapper, times(1)).toUserBonusDto(user.getBonus());
    }

    @Test
    void updateUserBonusUserNotFound() {
        Long userId = 123L;
        when(userRepository.getReferenceById(userId)).thenReturn(null);

        UserBonusDto result = userServiceImpl.updateUserBonus(userId, 10,20);
        assertNull(result);
    }

}