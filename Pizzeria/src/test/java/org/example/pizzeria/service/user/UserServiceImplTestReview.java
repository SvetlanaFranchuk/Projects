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
class UserServiceImplTestReview {

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
    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @BeforeEach
    void setUp() {
        Mockito.reset(userRepository);
        Mockito.reset(basketRepository);
        Mockito.reset(favoritesRepository);
       }


//    @Test
//    void addReview() {
//
//    }
//
//    @Test
//    void updateReview() {
//    }
//
//    @Test
//    void deleteReview() {
//    }
//
//    @Test
//    void getAllReview() {
//    }
//
//    @Test
//    void getAllReviewByUser() {
//    }
//
//    @Test
//    void getAllReviewByPeriod() {
//        LocalDateTime startDate = LocalDateTime.of(2024,1,1,0,0);
//        LocalDateTime endDate = LocalDateTime.of(2024,5,1,0,0);
//        UserApp user = userRepository.getReferenceById(1L);
//        Review review = new Review(1L, "I like pizza Margaritta", 10,
//                LocalDateTime.of(2024,3, 17,7, 20),
//                user);
//        reviewRepository.save(review);
//        List<Review> reviews = List.of(review);
//        List<ReviewResponseDto> reviewResponseDtoList = List.of(REVIEW_RESPONSE_DTO);
//        when(reviewRepository.findAllByReviewDateBetween(startDate, endDate)).thenReturn(reviews);
//
//        List<ReviewResponseDto> result = userServiceImpl.getAllReviewByPeriod(startDate,endDate);
//        assertEquals(reviewResponseDtoList, result);
//    }
}