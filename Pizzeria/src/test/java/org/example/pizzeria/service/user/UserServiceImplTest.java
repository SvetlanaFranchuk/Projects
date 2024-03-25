package org.example.pizzeria.service.user;

import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.user.UserBlockedResponseDto;
import org.example.pizzeria.dto.user.UserResponseDto;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.order.Basket;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.user.UserCreateException;
import org.example.pizzeria.mapper.user.UserMapper;
import org.example.pizzeria.repository.benefits.FavoritesRepository;
import org.example.pizzeria.repository.benefits.ReviewRepository;
import org.example.pizzeria.repository.order.BasketRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private FavoritesRepository favoritesRepository;
    @Mock
    private BasketRepository basketRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        Mockito.reset(userRepository);
        Mockito.reset(basketRepository);
        Mockito.reset(favoritesRepository);
    }

    @Test
    void save() {
        when(userMapper.toUserApp(TestData.USER_REGISTER_REQUEST_DTO)).thenReturn(TestData.USER_APP);
        UserResponseDto expectedResponseDto = TestData.USER_RESPONSE_DTO;
        when(userRepository.save(TestData.USER_APP)).thenReturn(TestData.USER_APP);
        when(userMapper.toUserResponseDto(TestData.USER_APP)).thenReturn(expectedResponseDto);
        Basket basket = new Basket();
        basket.setUserApp(TestData.USER_APP);
        Favorites favorites = new Favorites();
        favorites.setUserApp(TestData.USER_APP);

        UserResponseDto actualResponseDto = userServiceImpl.save(TestData.USER_REGISTER_REQUEST_DTO);
        assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    void save_UserAlreadyExists() {
        when(userRepository.findByUserNameAndEmail(TestData.USER_REGISTER_REQUEST_DTO.userName(),
                TestData.USER_REGISTER_REQUEST_DTO.email())).thenReturn(Optional.ofNullable(TestData.USER_APP));

        assertThrows(UserCreateException.class, () -> userServiceImpl.save(TestData.USER_REGISTER_REQUEST_DTO));
        verify(userRepository).findByUserNameAndEmail(TestData.USER_REGISTER_REQUEST_DTO.userName(), TestData.USER_REGISTER_REQUEST_DTO.email());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUser() {
        Long userId = 1L;
        when(userRepository.getReferenceById(userId)).thenReturn(TestData.USER_APP);
        when(userMapper.toUserResponseDto(TestData.USER_APP)).thenReturn(TestData.USER_RESPONSE_DTO);

        UserResponseDto result = userServiceImpl.getUser(userId);
        assertEquals(TestData.USER_RESPONSE_DTO, result);
        verify(userMapper, times(1)).toUserResponseDto(TestData.USER_APP);
        verify(userRepository, times(1)).getReferenceById(userId);
    }


    @Test
    public void update() {
        when(userRepository.getReferenceById(1L)).thenReturn(TestData.USER_APP);

        userServiceImpl.update(1L, TestData.USER_REQUEST_DTO);
        assertEquals(TestData.ADDRESS_NEW, TestData.USER_APP.getAddress());
        assertEquals(TestData.CONTACT_INFORMATION_NEW, TestData.USER_APP.getPhoneNumber());
    }

    @Test
    void update_NoDataToChange() {
        Long userId = 1L;
        when(userRepository.getReferenceById(userId)).thenReturn(TestData.USER_APP);
        UserResponseDto updatedUser = userServiceImpl.update(userId, TestData.USER_REQUEST_DTO);
        assertNull(updatedUser);
        verify(userRepository).save(any(UserApp.class));
    }


    @Test
    void getUsersByBirthday() {
        LocalDate date = LocalDate.of(2000, 1, 15);
        userRepository.save(TestData.USER_APP);
        List<UserApp> users = List.of(TestData.USER_APP);
        when(userRepository.findUserAppByBirthDate(date)).thenReturn(users);
        when(userMapper.toUserResponseDto(any())).thenAnswer(invocation -> {
            UserApp user = invocation.getArgument(0);
            return new UserResponseDto(user.getUserName(), user.getEmail(), user.getBirthDate(),
                    user.getAddress(), user.getPhoneNumber());
        });
        List<UserResponseDto> responseDtoList = userServiceImpl.getUsersByBirthday(date);

        assertNotNull(responseDtoList);
        assertEquals("IvanAdmin", responseDtoList.getFirst().getUserName());
    }

    @Test
    void getUsersByBirthday_NoUsersFound() {
        LocalDate birthday = LocalDate.of(1990, 10, 15);
        when(userRepository.findUserAppByBirthDate(birthday)).thenReturn(Collections.emptyList());
        List<UserResponseDto> result = userServiceImpl.getUsersByBirthday(birthday);
        assertEquals(0, result.size());
    }

    @Test
    void getUserByClientRole() {
        when(userRepository.findAllByRole(Role.CLIENT)).thenReturn(List.of(TestData.USER_APP_2));
        when(userMapper.toUserResponseDto(TestData.USER_APP_2)).thenReturn(TestData.USER_RESPONSE_DTO_2);
        List<UserResponseDto> userResponseDtoList = userServiceImpl.getUserByClientRole();

        assertNotNull(userResponseDtoList);
        assertEquals(1, userResponseDtoList.size());
        assertEquals(TestData.USER_APP_2.getUserName(), userResponseDtoList.getFirst().getUserName());
    }

    @Test
    void getUserByClientRole_EmptyList() {
        when(userRepository.findAllByRole(Role.CLIENT)).thenReturn(Collections.emptyList());
        List<UserResponseDto> userResponseDtoList = userServiceImpl.getUserByClientRole();
        assertNotNull(userResponseDtoList);
        assertTrue(userResponseDtoList.isEmpty());
    }

    @Test
    void getUserBlocked() {
        LocalDateTime dateTime = LocalDateTime.now();
        Review review1 = new Review(2L, "Bad review", 10, dateTime, TestData.USER_APP_BLOCKED);

        when(userRepository.findAllByIsBlocked(true)).thenReturn(List.of(TestData.USER_APP_BLOCKED));
        when(reviewRepository.findAllByUserApp(TestData.USER_APP_BLOCKED)).thenReturn(List.of(review1));
        when(userMapper.toUserBlockedResponseDto(2L, "TestClient", true, dateTime)).thenReturn(
                new UserBlockedResponseDto(2L, "TestClient", true, dateTime));
        List<UserBlockedResponseDto> result = userServiceImpl.getUserBlocked();

        assertEquals(1, result.size());
        assertEquals(TestData.USER_APP_BLOCKED.getUserName(), result.getFirst().getUserName());
        assertTrue(result.getFirst().isBlocked());
    }

    @Test
    void getUserBlockedNoBlockedUsers() {
        when(userRepository.findAllByIsBlocked(true)).thenReturn(List.of());
        List<UserBlockedResponseDto> result = userServiceImpl.getUserBlocked();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void changeUserBlocking() {
        when(userRepository.getReferenceById(2L)).thenReturn(TestData.USER_APP_NOT_BLOCKED);
        LocalDateTime reviewDate = LocalDateTime.now();
        Review review1 = new Review(1L, "Good pizza", 10, reviewDate, TestData.USER_APP_NOT_BLOCKED);
        when(reviewRepository.findAllByUserApp_Id(TestData.USER_APP_NOT_BLOCKED.getId())).thenReturn(List.of(review1));
        when(userRepository.save(TestData.USER_APP_BLOCKED)).thenReturn(TestData.USER_APP_BLOCKED);
        when(userMapper.toUserBlockedResponseDto(2L, TestData.USER_APP_BLOCKED.getUserName(), true, reviewDate))
                .thenReturn(new UserBlockedResponseDto(2L, TestData.USER_APP_BLOCKED.getUserName(), true, reviewDate));

        UserBlockedResponseDto result = userServiceImpl.changeUserBlocking(2L, true);
        assertEquals(TestData.USER_BLOCKED_RESPONSE_DTO.getUserName(), result.getUserName());
        assertEquals(TestData.USER_BLOCKED_RESPONSE_DTO.isBlocked(), result.isBlocked());
    }

    @Test
    void testChangeUserBlocking_NoReviewsFound_TrowEntityInPizzeriaNotFoundException() {
        Long userId = 123L;
        UserApp user = new UserApp();
        user.setId(userId);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(reviewRepository.findAllByUserApp_Id(userId)).thenReturn(Collections.emptyList());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> userServiceImpl.changeUserBlocking(userId, true));
    }
}

