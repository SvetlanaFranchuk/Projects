package org.example.pizzeria.service.user;

import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exeption.InvalidIDException;
import org.example.pizzeria.exeption.UpdateReviewException;
import org.example.pizzeria.exeption.UserBlockedException;
import org.example.pizzeria.exeption.UserNotFoundException;
import org.example.pizzeria.mapper.benefits.ReviewMapper;
import org.example.pizzeria.repository.benefits.ReviewRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTestReview {

    private static final UserApp USER_APP = UserApp.builder()
            .id(1L)
            .userName("IvanAdmin")
            .password("12345")
            .email("iv.admin@pizzeria.com")
            .birthDate(LocalDate.of(2000, 1, 15))
            .dateRegistration(LocalDate.now())
            .isBlocked(false)
            .role(Role.CLIENT)
            .build();
    private static final UserApp USER_BLOCKED = UserApp.builder()
            .id(2L)
            .userName("TestClient")
            .password("12345")
            .email("client1@pizzeria.com")
            .birthDate(LocalDate.of(2002, 3, 15))
            .dateRegistration(LocalDate.now())
            .isBlocked(true)
            .role(Role.CLIENT)
            .build();
    private static final ReviewRequestDto REVIEW_REQUEST_DTO = new ReviewRequestDto(1L, "Good pizza", 10);
    private static final ReviewRequestDto REVIEW_REQUEST_DTO_2 = new ReviewRequestDto(2L, "Super", 10);
    private static final ReviewRequestDto REVIEW_REQUEST_DTO_3 = new ReviewRequestDto(1L, "Super", 10);
    private static final ReviewResponseDto REVIEW_RESPONSE_DTO = new ReviewResponseDto("Good pizza",
            10, "IvanAdmin", LocalDateTime.now());
    private static final ReviewResponseDto REVIEW_RESPONSE_DTO_NEW = new ReviewResponseDto("Super",
            8, "IvanAdmin", LocalDateTime.now());

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;
     @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        Mockito.reset(userRepository);
    }

    @Test
    void addReview() {
        Long userId = 1L;
        Review review = new Review(1L, "Good pizza", 10, LocalDateTime.now(), USER_APP);
        when(userRepository.getReferenceById(userId)).thenReturn(USER_APP);
        when(reviewMapper.toReview("Good pizza", 10, USER_APP)).thenReturn(review);
        when(reviewMapper.toReviewResponseDto(review)).thenReturn(REVIEW_RESPONSE_DTO);
        when(reviewRepository.save(review)).thenReturn(review);

        ReviewResponseDto result = userServiceImpl.addReview(REVIEW_REQUEST_DTO);
        assertEquals(REVIEW_RESPONSE_DTO, result);
    }

    @Test
    void addReviewBlockedUser() {
        when(userRepository.getReferenceById(2L)).thenReturn(USER_BLOCKED);
        assertThrows(UserBlockedException.class, () -> userServiceImpl.addReview(REVIEW_REQUEST_DTO_2));
        verify(userRepository, times(1)).getReferenceById(REVIEW_REQUEST_DTO_2.UserId());
        verify(reviewRepository, never()).save(any());
        verify(reviewMapper, never()).toReviewResponseDto(any());
    }

    @Test
    void updateReview() {
        Long reviewId = 1L;
        Review existingReview = new Review(1L, "Super", 8, LocalDateTime.now(), USER_APP);
        when(reviewRepository.save(existingReview)).thenReturn(existingReview);
        when(reviewRepository.getReferenceById(reviewId)).thenReturn(existingReview);
        when(userRepository.getReferenceById(USER_APP.getId())).thenReturn(USER_APP);
        when(reviewMapper.toReviewResponseDto(existingReview)).thenReturn(REVIEW_RESPONSE_DTO_NEW);
        ReviewResponseDto updatedReviewResponseDto = userServiceImpl.updateReview(reviewId, REVIEW_REQUEST_DTO);

        assertEquals(REVIEW_RESPONSE_DTO_NEW, updatedReviewResponseDto);
        assertEquals(REVIEW_REQUEST_DTO.comment(), existingReview.getComment());
        assertEquals(REVIEW_REQUEST_DTO.grade(), existingReview.getGrade());
        verify(reviewRepository, times(1)).save(existingReview);
    }

    @Test
    void updateReviewBlockedUser() {
        when(userRepository.getReferenceById(REVIEW_REQUEST_DTO_2.UserId())).thenReturn(USER_BLOCKED);

        assertThrows(UserBlockedException.class, () -> userServiceImpl.updateReview(1L, REVIEW_REQUEST_DTO_2));
        verify(userRepository, times(1)).getReferenceById(REVIEW_REQUEST_DTO_2.UserId());
        verify(reviewRepository, never()).getReferenceById(any());
        verify(reviewRepository, never()).save(any());
        verify(reviewMapper, never()).toReviewResponseDto(any());
    }

    @Test
    void updateInaccessibleReview() {
        long userId = 1L;
        long reviewId = 2L;
        Review review = new Review(reviewId, "Good", 8, LocalDateTime.now(), USER_BLOCKED);

        when(userRepository.getReferenceById(userId)).thenReturn(USER_APP);
        when(reviewRepository.getReferenceById(reviewId)).thenReturn(review);
        assertThrows(UpdateReviewException.class, () -> userServiceImpl.updateReview(reviewId, REVIEW_REQUEST_DTO_3));
    }
    @Test
    void deleteReview() {
        long reviewId = 2L;
        Review review = new Review(reviewId, "Good pizza", 10, LocalDateTime.now(), USER_APP);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        assertDoesNotThrow(() -> userServiceImpl.deleteReview(reviewId));

        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void deleteInvalidIdReview(){
        long reviewId = 1L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());
        assertThrows(InvalidIDException.class, () -> userServiceImpl.deleteReview(reviewId));
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void getAllReview() {
        Review review1 = new Review(1L, "Good pizza", 10, LocalDateTime.now(), USER_APP);
        Review review2 = new Review(2L, "Super", 5, LocalDateTime.now(), USER_APP);
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(review1, review2));
        when(reviewMapper.toReviewResponseDto(review1)).thenReturn(REVIEW_RESPONSE_DTO);

        List<ReviewResponseDto> result = userServiceImpl.getAllReview();

        assertEquals(2, result.size());
        verify(reviewRepository, times(1)).findAll();
        verify(reviewMapper, times(2)).toReviewResponseDto(any());
    }

    @Test
    void getAllReviewEmptyList() {
        when(reviewRepository.findAll()).thenReturn(List.of());
        List<ReviewResponseDto> reviewResponseDtoList = userServiceImpl.getAllReview();
        assertEquals(new ArrayList<>(), reviewResponseDtoList);
        verify(reviewRepository, times(1)).findAll();
        verify(reviewMapper, never()).toReviewResponseDto(any());
    }
    @Test
    void getAllReviewByUser() {
        Review review1 = new Review(1L, "Good pizza", 10, LocalDateTime.now(), USER_APP);
        when(userRepository.getReferenceById(1L)).thenReturn(USER_APP);
        when(reviewRepository.findAllByUserApp(USER_APP)).thenReturn(List.of(review1));
        when(reviewMapper.toReviewResponseDto(review1)).thenReturn(REVIEW_RESPONSE_DTO);
        List<ReviewResponseDto> result = userServiceImpl.getAllReviewByUser(1L);

        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findAllByUserApp(USER_APP);
        verify(reviewMapper, times(1)).toReviewResponseDto(any());
    }

    @Test
    void getAllReviewByNotFoundUser() {
        Long userId=123L;
        when(userRepository.getReferenceById(userId)).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.getAllReviewByUser(userId));
        verifyNoMoreInteractions(reviewRepository);
    }
    @Test
    void getAllReviewByPeriod() {
        LocalDateTime startDate = LocalDateTime.of(2024,1,1,0,0);
        LocalDateTime endDate = LocalDateTime.of(2024,5,1,0,0);
        Review review1 = new Review(1L, "Good pizza", 10, LocalDateTime.now(), USER_APP);
        when(reviewRepository.findAllByReviewDateBetween(startDate, endDate)).thenReturn(List.of(review1));
        when(reviewMapper.toReviewResponseDto(review1)).thenReturn(REVIEW_RESPONSE_DTO);
        List<ReviewResponseDto> result = userServiceImpl.getAllReviewByPeriod(startDate, endDate);

        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findAllByReviewDateBetween(startDate, endDate);
        verify(reviewMapper, times(1)).toReviewResponseDto(any());
    }

    @Test
    void getAllReviewByPeriodNoReviewsFound() {
        LocalDateTime startDate = LocalDateTime.of(2024, 3, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 16, 23, 59);
        when(reviewRepository.findAllByReviewDateBetween(startDate, endDate)).thenReturn(Arrays.asList());
        List<ReviewResponseDto> result = userServiceImpl.getAllReviewByPeriod(startDate, endDate);

        assertEquals(0, result.size());
        verify(reviewRepository, times(1)).findAllByReviewDateBetween(startDate, endDate);
        verify(reviewMapper, never()).toReviewResponseDto(any());
    }
}