package org.example.pizzeria.service.user;

import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.user.UserBlockedResponseDto;
import org.example.pizzeria.dto.user.UserBonusDto;
import org.example.pizzeria.dto.user.UserResponseDto;
import org.example.pizzeria.dto.user.UserResponseDtoForAdmin;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.order.Basket;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.user.StatusAlreadyExistsException;
import org.example.pizzeria.exception.user.UserCreateException;
import org.example.pizzeria.mapper.user.UserMapper;
import org.example.pizzeria.repository.benefits.FavoritesRepository;
import org.example.pizzeria.repository.benefits.ReviewRepository;
import org.example.pizzeria.repository.order.BasketRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.example.pizzeria.service.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
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
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationService authenticationService;
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
        when(userMapper.toUserApp(TestData.USER_REGISTER_REQUEST_DTO, "validPassword")).thenReturn(TestData.USER_APP);
        UserResponseDto expectedResponseDto = TestData.USER_RESPONSE_DTO;
        when(userRepository.save(TestData.USER_APP)).thenReturn(TestData.USER_APP);
        when(userMapper.toUserResponseDto(TestData.USER_APP)).thenReturn(expectedResponseDto);
        Basket basket = new Basket();
        basket.setUserApp(TestData.USER_APP);
        Favorites favorites = new Favorites();
        favorites.setUserApp(TestData.USER_APP);

        UserResponseDto actualResponseDto = userServiceImpl.save(TestData.USER_REGISTER_REQUEST_DTO, "validPassword");
        assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    void save_UserAlreadyExists_TrowUserCreateException() {
        when(userRepository.findByUserNameAndEmail(TestData.USER_REGISTER_REQUEST_DTO.userName(),
                TestData.USER_REGISTER_REQUEST_DTO.email())).thenReturn(Optional.ofNullable(TestData.USER_APP));

        assertThrows(UserCreateException.class, () -> userServiceImpl.save(TestData.USER_REGISTER_REQUEST_DTO, "validPassword"));
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
    void getUser_UserNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long userId = 1L;
        when(userRepository.getReferenceById(userId)).thenThrow(NoSuchElementException.class);
        assertThrows(EntityInPizzeriaNotFoundException.class,
                () -> userServiceImpl.getUser(userId));
    }

    @Test
    public void update() {
        when(userRepository.getReferenceById(1L)).thenReturn(TestData.USER_APP);
        when(userRepository.save(TestData.USER_APP)).thenReturn(TestData.USER_APP);
        when(userMapper.toUserResponseDto(TestData.USER_APP)).thenReturn(TestData.USER_RESPONSE_DTO);
        UserResponseDto result = userServiceImpl.update(1L, TestData.USER_REQUEST_DTO, "12345");
        assertEquals(TestData.ADDRESS_NEW, TestData.USER_APP.getAddress());
        assertEquals(TestData.CONTACT_INFORMATION_NEW, TestData.USER_APP.getPhoneNumber());
    }

    @Test
    void update_NoDataToChange() {
        Long userId = 1L;
        when(userRepository.getReferenceById(userId)).thenReturn(TestData.USER_APP);
        UserResponseDto updatedUser = userServiceImpl.update(userId, TestData.USER_REQUEST_DTO, "12345");
        assertNull(updatedUser);
    }

    @Test
    void update_UserNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long userId = 999L;
        when(userRepository.getReferenceById(userId)).thenThrow(NoSuchElementException.class);
        assertThrows(EntityInPizzeriaNotFoundException.class,
                () -> userServiceImpl.update(999L, TestData.USER_REQUEST_DTO, "12345"));
    }

    @Test
    void getUsersByBirthday() {
        LocalDate date = LocalDate.of(2000, 1, 15);
        userRepository.save(TestData.USER_APP);
        List<UserApp> users = List.of(TestData.USER_APP);
        when(userRepository.findAllByBirthDate(date)).thenReturn(users);
        when(userMapper.toUserResponseDto(any())).thenAnswer(invocation -> {
            UserApp user = invocation.getArgument(0);
            return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getBirthDate(),
                    user.getAddress(), user.getPhoneNumber());
        });
        List<UserResponseDto> responseDtoList = userServiceImpl.getUsersByBirthday(date);

        assertNotNull(responseDtoList);
        assertEquals("IvanAdmin", responseDtoList.getFirst().getUserName());
    }

    @Test
    void getUsersByBirthday_NoUsersFound_ThrowEntityInPizzeriaNotFoundException() {
        LocalDate date = LocalDate.of(2000, 1, 1);
        when(userRepository.findAllByBirthDate(date)).thenReturn(Collections.emptyList());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> userServiceImpl.getUsersByBirthday(date));
        verify(userRepository, times(1)).findAllByBirthDate(date);
        verify(userMapper, never()).toUserResponseDto(any());
    }

    @Test
    void getUserByClientRole() {
        when(userRepository.findAllByRole(Role.ROLE_CLIENT)).thenReturn(List.of(TestData.USER_APP_2));
        when(userMapper.toUserResponseDtoForAdmin(TestData.USER_APP_2)).thenReturn(TestData.USER_RESPONSE_DTO_FOR_ADMIN);
        List<UserResponseDtoForAdmin> userResponseDtoList = userServiceImpl.getUserByClientRole();

        assertNotNull(userResponseDtoList);
        assertEquals(1, userResponseDtoList.size());
        assertEquals(TestData.USER_APP_2.getUsername(), userResponseDtoList.getFirst().getUserName());
    }

    @Test
    void getUserByClientRole_EmptyList() {
        when(userRepository.findAllByRole(Role.ROLE_CLIENT)).thenReturn(Collections.emptyList());
        List<UserResponseDtoForAdmin> userResponseDtoList = userServiceImpl.getUserByClientRole();
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
        assertEquals(TestData.USER_APP_BLOCKED.getUsername(), result.getFirst().getUserName());
        assertTrue(result.getFirst().isBlocked());
    }

    @Test
    void getUserBlocked_NoBlockedUsers_EmptyList() {
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
        when(userMapper.toUserBlockedResponseDto(2L, TestData.USER_APP_BLOCKED.getUsername(), true, reviewDate))
                .thenReturn(new UserBlockedResponseDto(2L, TestData.USER_APP_BLOCKED.getUsername(), true, reviewDate));

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

    @Test
    void changeUserBlocking_UserAlreadyBlocked() {
        UserApp userApp = new UserApp();
        userApp.setBlocked(true);
        when(userRepository.getReferenceById(1L)).thenReturn(userApp);
        Review review = new Review();
        review.setReviewDate(LocalDateTime.now());
        when(reviewRepository.findAllByUserApp_Id(1L)).thenReturn(Collections.singletonList(review));
        assertThrows(StatusAlreadyExistsException.class, () -> userServiceImpl.changeUserBlocking(1L, true));

        verify(userRepository, times(1)).getReferenceById(1L);
    }

    @Test
    void getByUserName() {
        String userName = "IvanAdmin";
        UserApp expectedUser = TestData.USER_APP;
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(expectedUser));
        UserApp actualUser = userServiceImpl.getByUserName(userName);
        assertNotNull(actualUser);
        assertEquals(userName, actualUser.getUsername());
    }

    @Test
    void getByUserName_BadName_ThrowEntityInPizzeriaNotFoundException() {
        String userName = "nonExistingUser";
        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> userServiceImpl.getByUserName(userName));
    }

    @Test
    void userDetailsService() {
        String userName = "IvanAdmin";
        UserApp expectedUser = TestData.USER_APP;
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(expectedUser));

        UserDetailsService userDetailsService = userServiceImpl.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        assertNotNull(userDetails);
        assertEquals(userName, userDetails.getUsername());
    }

    @Test
    void userDetailsService_UserNotFound_ThrowEntityInPizzeriaNotFoundException() {
        String userName = "NonExistentUser";
        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        UserDetailsService userDetailsService = userServiceImpl.userDetailsService();
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> userDetailsService.loadUserByUsername(userName));
    }

    @Test
    void getCurrentUser() {
        String userName = "IvanAdmin";
        UserApp expectedUser = TestData.USER_APP;

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userName, null));
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(expectedUser));
        UserApp currentUser = userServiceImpl.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals(expectedUser, currentUser);
    }

    @Test
    void getCurrentUser_NoAuthenticatedUser() {
        SecurityContextHolder.clearContext();
        Throwable exception = assertThrows(NullPointerException.class, () -> userServiceImpl.getCurrentUser());
        assertInstanceOf(NullPointerException.class, exception, "Expected a NullPointerException to be thrown, but got a different type of exception.");
    }

    @Test
    void getBonus() {
        when(userRepository.getReferenceById(1L)).thenReturn(TestData.USER_APP);
        when(userMapper.toUserBonusDto(TestData.BONUS.getCountOrders(), TestData.BONUS.getSumOrders()))
                .thenReturn(TestData.USER_BONUS_DTO);
        UserBonusDto result = userServiceImpl.getBonus(1L);

        assertEquals(TestData.USER_BONUS_DTO, result);
    }

    @Test
    void getBonus_UserNotFound_ThrowEntityInPizzeriaNotFoundException() {
        when(userRepository.getReferenceById(99L)).thenReturn(null);
        assertThrows(EntityInPizzeriaNotFoundException.class, () ->
                userServiceImpl.getBonus(99L));
    }

    @Test
    void updateBonus() {
        int countToAdd = 20;
        double sumToAdd = 375.0;
        Bonus expectedBonus = new Bonus(30, 500.0);
        when(userRepository.getReferenceById(1L)).thenReturn(TestData.USER_APP);
        when(userRepository.save(any(UserApp.class))).thenReturn(TestData.USER_APP_WITH_NEW_BONUS);
        when(userMapper.toUserBonusDto(expectedBonus.getCountOrders(), expectedBonus.getSumOrders()))
                .thenReturn(TestData.USER_NEW_BONUS_DTO);
        UserBonusDto result = userServiceImpl.updateBonus(1L, countToAdd, sumToAdd);

        assertNotNull(result);
        assertEquals(expectedBonus.getCountOrders(), result.getCountOrders());
        assertEquals(expectedBonus.getSumOrders(), result.getSumOrders());
    }

    @Test
    void updateBonus_UserNotFound_ThrowEntityInPizzeriaNotFoundException() {
        when(userRepository.getReferenceById(99L)).thenReturn(null);
        assertThrows(EntityInPizzeriaNotFoundException.class, () ->
                userServiceImpl.updateBonus(99L, 20, 375));
    }


}

