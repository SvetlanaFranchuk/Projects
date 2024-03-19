package org.example.pizzeria.service.user;

import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.dto.user.UserBlockedResponseDto;
import org.example.pizzeria.dto.user.UserRegisterRequestDto;
import org.example.pizzeria.dto.user.UserRequestDto;
import org.example.pizzeria.dto.user.UserResponseDto;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.order.Basket;
import org.example.pizzeria.entity.user.Address;
import org.example.pizzeria.entity.user.ContactInformation;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exeption.DateIsNullException;
import org.example.pizzeria.exeption.UserCreateError;
import org.example.pizzeria.exeption.UserNotFoundException;
import org.example.pizzeria.mapper.benefits.ReviewMapper;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final UserRegisterRequestDto USER_REGISTER_REQUEST_DTO = new UserRegisterRequestDto("IvanAdmin",
            "Qwerty", "iv.admin@pizzeria.com", LocalDate.of(2000, 1, 15),
            "", "", "", "", "+490246562332");
    private static final UserRequestDto USER_REQUEST_DTO = new UserRequestDto("IvanAdmin",
            "Qwerty", "iv.admin@pizzeria.com", LocalDate.of(2000, 1, 15),
            "", "test", "10", "", "+480246562332");
    private static final Address ADDRESS = new Address("", "", "", "");
    private static final Address ADDRESS_NEW = new Address("", "test", "10", "");
    private static final ContactInformation CONTACT_INFORMATION = new ContactInformation("+490246562332");
    private static final ContactInformation CONTACT_INFORMATION_NEW = new ContactInformation("+480246562332");
    private static final UserResponseDto USER_RESPONSE_DTO = new UserResponseDto("IvanAdmin",
            "iv.admin@pizzeria.com", LocalDate.of(2000, 1, 15), ADDRESS,
            CONTACT_INFORMATION);
    private static final UserApp USER_APP = UserApp.builder()
            .id(1L)
            .userName("IvanAdmin")
            .password("12345")
            .email("iv.admin@pizzeria.com")
            .birthDate(LocalDate.of(2000, 1, 15))
            .dateRegistration(LocalDate.now())
            .address(ADDRESS)
            .phoneNumber(CONTACT_INFORMATION)
            .isBlocked(false)
            .role(Role.CLIENT)
            .bonus(new Bonus(0,0.0))
            .build();
    private static final UserApp USER_APP_NOT_BLOCKED = UserApp.builder()
            .id(2L)
            .userName("TestClient")
            .password("12345")
            .email("clientTest@pizzeria.com")
            .birthDate(LocalDate.of(2001, 10, 15))
            .dateRegistration(LocalDate.now())
            .address(ADDRESS)
            .phoneNumber(CONTACT_INFORMATION)
            .isBlocked(false)
            .role(Role.CLIENT)
            .bonus(new Bonus(0,0.0))
            .build();
    private static final UserApp USER_APP_BLOCKED = UserApp.builder()
            .id(2L)
            .userName("TestClient")
            .password("12345")
            .email("clientTest@pizzeria.com")
            .birthDate(LocalDate.of(2001, 10, 15))
            .dateRegistration(LocalDate.now())
            .address(ADDRESS)
            .phoneNumber(CONTACT_INFORMATION)
            .isBlocked(true)
            .role(Role.CLIENT)
            .bonus(new Bonus(0,0.0))
            .build();

    private static final UserBlockedResponseDto USER_BLOCKED_RESPONSE_DTO = new UserBlockedResponseDto(2L,
            "TestClient", true, LocalDateTime.now());
    private static final Review REVIEW = new Review(1L, "Bad comment", 0,
            LocalDateTime.of(2024,3,17, 7,15), USER_APP_NOT_BLOCKED);

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
        when(userMapper.toUserApp(USER_REGISTER_REQUEST_DTO.userName(), USER_REGISTER_REQUEST_DTO.password(),
                USER_REGISTER_REQUEST_DTO.email(), USER_REGISTER_REQUEST_DTO.birthDate(), ADDRESS,
                        CONTACT_INFORMATION, false, Role.CLIENT,
                        new Bonus(0,0.0))).thenReturn(USER_APP);
        UserResponseDto expectedResponseDto = USER_RESPONSE_DTO;
        when(userRepository.save(USER_APP)).thenReturn(USER_APP);
        when(userMapper.toUserResponseDto(USER_APP)).thenReturn(expectedResponseDto);
        Basket basket = new Basket();
        basket.setUserApp(USER_APP);
        Favorites favorites = new Favorites();
        favorites.setUserApp(USER_APP);

        UserResponseDto actualResponseDto = userServiceImpl.save(USER_REGISTER_REQUEST_DTO);
        assertEquals(expectedResponseDto, actualResponseDto);
        assertEquals("IvanAdmin", actualResponseDto.getUserName());
        verify(basketRepository, times(1)).save(basket);
        verify(favoritesRepository, times(1)).save(favorites);
    }

    @Test
    void save_UserAlreadyExists() {
        when(userRepository.findAllByUserNameAndEmail(USER_REGISTER_REQUEST_DTO.userName(),
                USER_REGISTER_REQUEST_DTO.email())).thenReturn(Collections.singletonList(new UserApp()));

        assertThrows(UserCreateError.class, () -> userServiceImpl.save(USER_REGISTER_REQUEST_DTO));
        verify(userRepository).findAllByUserNameAndEmail(USER_REGISTER_REQUEST_DTO.userName(), USER_REGISTER_REQUEST_DTO.email());
        verifyNoMoreInteractions(userRepository);
    }
    @Test
    void getUser() {
        Long userId = 1L;
        when(userRepository.getReferenceById(userId)).thenReturn(USER_APP);
        when(userMapper.toUserResponseDto(USER_APP)).thenReturn(USER_RESPONSE_DTO);

        UserResponseDto result = userServiceImpl.getUser(userId);
        assertEquals(USER_RESPONSE_DTO, result);
        verify(userMapper, times(1)).toUserResponseDto(USER_APP);
        verify(userRepository, times(1)).getReferenceById(userId);
    }

    @Test
    void getUser_UserNotFound() {
        Long userId = 2L;
        when(userRepository.getReferenceById(userId)).thenReturn(null);
        UserResponseDto result = userServiceImpl.getUser(userId);
        assertNull(result);
    }

    @Test
        public void update()  {
            when(userRepository.getReferenceById(1L)).thenReturn(USER_APP);

            userServiceImpl.update(1L, USER_REQUEST_DTO);
            assertEquals(ADDRESS_NEW, USER_APP.getAddress());
            assertEquals(CONTACT_INFORMATION_NEW, USER_APP.getPhoneNumber());
        }

    @Test
    void update_UserNotFound() {
        Long id = 123L;
        when(userRepository.getReferenceById(id)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userServiceImpl.update(id, USER_REQUEST_DTO));
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUsersByBirthday() {
        LocalDate date = LocalDate.of(2000, 1, 15);
        userRepository.save(USER_APP);
        List<UserApp> users = List.of(USER_APP);
        when(userRepository.findUserAppByBirthDate(date)).thenReturn(users);
        when(userMapper.toUserResponseDto(any())).thenAnswer(invocation -> {
            UserApp user = invocation.getArgument(0);
            return new UserResponseDto(user.getUserName(), user.getEmail(), user.getBirthDate(),
                    user.getAddress(), user.getPhoneNumber());
        });
        List<UserResponseDto> responseDtoList = userServiceImpl.getUsersByBirthday(date);

        assertNotNull(responseDtoList);
        assertEquals("IvanAdmin", responseDtoList.get(0).getUserName());
    }

    @Test
    void getUsersByBirthday_NullDate() {
        LocalDate date = null;
        assertThrows(DateIsNullException.class, () -> userServiceImpl.getUsersByBirthday(date));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserByClientRole() {
        List<UserApp> users = new ArrayList<>();
        users.add(USER_APP);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserResponseDto(any())).thenAnswer(invocation -> {
            UserApp user = invocation.getArgument(0);
            return new UserResponseDto(user.getUserName(), user.getEmail(), user.getBirthDate(),
                    user.getAddress(), user.getPhoneNumber());
        });
        List<UserResponseDto> responseDtoList = userServiceImpl.getUserByClientRole();

        assertNotNull(responseDtoList);
        assertEquals(1, responseDtoList.size());
        assertEquals("IvanAdmin", responseDtoList.get(0).getUserName());
        verify(userRepository).findAll();
        verify(userMapper, times(1)).toUserResponseDto(any());
    }

    @Test
    void getUserByClientRole_EmptyResult() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        List<UserResponseDto> responseDtoList = userServiceImpl.getUserByClientRole();
        assertNotNull(responseDtoList);
        assertTrue(responseDtoList.isEmpty());

        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserBlocked() {
        LocalDateTime dateTime = LocalDateTime.now();
        Review review1 = new Review(2L, "Bad review", 10, dateTime, USER_APP_BLOCKED);

        when(userRepository.findAll()).thenReturn(List.of(USER_APP_BLOCKED));
        when(reviewRepository.findAllByUserApp(USER_APP_BLOCKED)).thenReturn(List.of(review1));
        when(userMapper.toUserBlockedResponseDto(2L, "TestClient", true, dateTime)).thenReturn(
                new UserBlockedResponseDto(2L, "TestClient", true, dateTime));
        List<UserBlockedResponseDto> result = userServiceImpl.getUserBlocked();

        assertEquals(1, result.size());
        assertEquals(USER_APP_BLOCKED.getUserName(), result.get(0).getUserName());
        assertTrue(result.get(0).isBlocked());
        }

    @Test
    void getUserBlockedNoBlockedUsers() {
        when(userRepository.findAll()).thenReturn(List.of(USER_APP));
        List<UserBlockedResponseDto> result = userServiceImpl.getUserBlocked();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    void changeBlockingUser() {
        when(userRepository.getReferenceById(2L)).thenReturn(USER_APP_NOT_BLOCKED);
        LocalDateTime reviewDate = LocalDateTime.now();
        Review review1 = new Review(1L, "Good pizza", 10, reviewDate, USER_APP_NOT_BLOCKED);
        when(reviewRepository.findAllByUserApp(USER_APP_NOT_BLOCKED)).thenReturn(List.of(review1));
        when(userRepository.save(USER_APP_BLOCKED)).thenReturn(USER_APP_BLOCKED);
        when(userMapper.toUserBlockedResponseDto(2L, USER_APP_BLOCKED.getUserName(), true, reviewDate))
                .thenReturn(new UserBlockedResponseDto(2L, USER_APP_BLOCKED.getUserName(), true, reviewDate));

        UserBlockedResponseDto result = userServiceImpl.changeBlockingUser(2L, true);
        assertEquals(USER_BLOCKED_RESPONSE_DTO.getUserName(), result.getUserName());
        assertEquals(USER_BLOCKED_RESPONSE_DTO.isBlocked(), result.isBlocked());
    }
    @Test
    void changeBlockingNotFoundUser() {
        Long userId=123L;
        when(userRepository.getReferenceById(userId)).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.changeBlockingUser(userId, true));
        verifyNoMoreInteractions(reviewRepository);
    }
}

