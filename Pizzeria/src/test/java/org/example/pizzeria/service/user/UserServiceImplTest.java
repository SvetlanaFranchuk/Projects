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
    private static final UserResponseDto USER_RESPONSE_DTO_2 = new UserResponseDto("IvanAdmin",
            "iv.admin@pizzeria.com", LocalDate.of(2000, 1, 15), ADDRESS_NEW,
            CONTACT_INFORMATION_NEW);
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
            .bonus(new Bonus(0,0))
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
            .bonus(new Bonus(0,0))
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
            .bonus(new Bonus(0,0))
            .build();
    private static final UserApp USER_APP_NEW = UserApp.builder()
            .id(1L)
            .userName("IvanAdmin")
            .password("12345")
            .email("iv.admin@pizzeria.com")
            .birthDate(LocalDate.of(2000, 1, 15))
            .dateRegistration(LocalDate.now())
            .address(ADDRESS_NEW)
            .phoneNumber(CONTACT_INFORMATION_NEW)
            .isBlocked(false)
            .role(Role.CLIENT)
            .bonus(new Bonus(0,0))
            .build();
    private static final ReviewResponseDto REVIEW_RESPONSE_DTO = new ReviewResponseDto("I like pizza Margaritta",
            10, "IvanAdmin", LocalDateTime.of(2024, 3, 17, 7, 20));
    private static final UserBlockedResponseDto USER_BLOCKED_RESPONSE_DTO = new UserBlockedResponseDto(2L,
            "TestClient", true, LocalDateTime.of(2024,3,17, 7,15));
    private static final Review REVIEW = new Review(1L, "Bad comment", 0,
            LocalDateTime.of(2024,3,17, 7,15), USER_APP_BLOCKED);

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
        UserRegisterRequestDto candidate = USER_REGISTER_REQUEST_DTO;
        UserApp userApp = USER_APP;
        when(userMapper.toUserApp(candidate.userName(), candidate.password(),
                        candidate.email(), candidate.birthDate(), ADDRESS,
                        CONTACT_INFORMATION, false, Role.CLIENT,
                        new Bonus(0,0))).thenReturn(userApp);
        UserResponseDto expectedResponseDto = USER_RESPONSE_DTO;
        when(userRepository.save(userApp)).thenReturn(USER_APP);
        when(userMapper.toUserResponseDto(userApp)).thenReturn(expectedResponseDto);
        Basket basket = new Basket();
        basket.setUserApp(userApp);
        Favorites favorites = new Favorites();
        favorites.setUserApp(userApp);

        UserResponseDto actualResponseDto = userServiceImpl.save(candidate);
        assertEquals(expectedResponseDto, actualResponseDto);
        assertEquals("IvanAdmin", actualResponseDto.getUserName());
        verify(basketRepository, times(1)).save(basket);
        verify(favoritesRepository, times(1)).save(favorites);
    }

    @Test
    void save_UserAlreadyExists() {
        UserRegisterRequestDto requestDto = USER_REGISTER_REQUEST_DTO;
        when(userRepository.findAllByUserNameAndEmail(requestDto.userName(),
                requestDto.email())).thenReturn(Collections.singletonList(new UserApp()));

        assertThrows(UserCreateError.class, () -> userServiceImpl.save(requestDto));
        verify(userRepository).findAllByUserNameAndEmail(requestDto.userName(), requestDto.email());
        verifyNoMoreInteractions(userRepository);
    }
    @Test
    void getUser() {
        Long userId = 1L;
        UserApp userApp = USER_APP;
        when(userRepository.getReferenceById(userId)).thenReturn(userApp);
        when(userMapper.toUserResponseDto(userApp)).thenReturn(USER_RESPONSE_DTO);

        UserResponseDto result = userServiceImpl.getUser(userId);
        assertEquals(USER_RESPONSE_DTO, result);
        verify(userMapper, times(1)).toUserResponseDto(userApp);
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
    void update() {
        Long id = 1L;
        UserRequestDto requestDto = USER_REQUEST_DTO;
        UserApp userApp = USER_APP;
        UserApp updatedUserApp = USER_APP_NEW;
        when(userRepository.getReferenceById(id)).thenReturn(userApp);
        when(userRepository.updateUserAppByIdAndPasswordAndEmailAndBirthDateAndAddressAndPhoneNumber(id,
                USER_REQUEST_DTO.password(),
                USER_REQUEST_DTO.email(), USER_REQUEST_DTO.birthDate(), ADDRESS_NEW, CONTACT_INFORMATION_NEW))
                .thenReturn(updatedUserApp);
        when(userMapper.toUserResponseDto(updatedUserApp)).thenReturn(USER_RESPONSE_DTO_2);

        UserResponseDto responseDto = userServiceImpl.update(id, requestDto);
        assertNotNull(responseDto);
        assertEquals(ADDRESS_NEW, responseDto.getAddress());
        verify(userRepository).getReferenceById(id);
        verify(userRepository).updateUserAppByIdAndPasswordAndEmailAndBirthDateAndAddressAndPhoneNumber(id,
                USER_REQUEST_DTO.password(),
                USER_REQUEST_DTO.email(), USER_REQUEST_DTO.birthDate(), ADDRESS_NEW, CONTACT_INFORMATION_NEW);
        verify(userMapper).toUserResponseDto(updatedUserApp);
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
//        List<UserApp> userList = List.of(USER_APP, USER_APP_BLOCKED);
//        Review review = REVIEW;
//
//        when(reviewRepository.save(REVIEW)).thenReturn(review);
//        when(userRepository.findAll()).thenReturn(userList);
//        when(userMapper.toUserBlockedResponseDto(USER_APP_BLOCKED,
//                LocalDateTime.of(2024,3,17, 7,15))).thenReturn(USER_BLOCKED_RESPONSE_DTO);
//
//        List<UserBlockedResponseDto> responseDtoList = userServiceImpl.getUserBlocked();
//
//        assertNotNull(responseDtoList);
//        assertEquals(1, responseDtoList.size());
//        assertEquals("TestClient", responseDtoList.get(1).userName());
//        assertNotNull(responseDtoList.get(1).reviewDate());
//
//        verify(userRepository).findAll();
//        verify(userMapper).toUserBlockedResponseDto(USER_APP, null);
//        verify(userMapper, times(2)).toUserBlockedResponseDto(any(), any());
//        verifyNoMoreInteractions(userRepository, userMapper);
     }


    @Test
    void changeBlockingUser() {
        Long userId = 2L;
        LocalDateTime reviewDate = LocalDateTime.of(2024,3,17, 7,15);
        UserApp userApp = USER_APP_NOT_BLOCKED;
        when(userRepository.getReferenceById(userId)).thenReturn(userApp);
        when(reviewRepository.findLastMessageByUserId(userId)).thenReturn(REVIEW);

        UserBlockedResponseDto responseDto = userServiceImpl.changeBlockingUser(2L, true);
        assertEquals(userId, responseDto.getId());
        assertEquals("TestClient", responseDto.getUserName());
        assertTrue(responseDto.isBlocked());
        assertEquals(reviewDate, responseDto.getReviewDate());
   }
}