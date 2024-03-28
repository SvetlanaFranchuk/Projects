package org.example.pizzeria.service.user;

import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.user.UpdateReviewException;
import org.example.pizzeria.exception.user.UserBlockedException;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.example.pizzeria.TestData.REVIEW_REQUEST_DTO_3;
import static org.example.pizzeria.TestData.USER_APP;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTestReview {

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
        Review review = new Review(1L, "Good pizza", 10, LocalDateTime.now(), null);
        UserApp userApp = TestData.USER_APP;
        userApp.setReviews(new HashSet<>());
        when(userRepository.findById(userId)).thenReturn(Optional.of(userApp));
        when(reviewMapper.toReview("Good pizza", 10)).thenReturn(review);
        review.setUserApp(userApp);
        when(reviewRepository.save(review)).thenReturn(review);
        userApp.addReview(review);
        when(userRepository.save(userApp)).thenReturn(userApp);
        when(reviewMapper.toReviewResponseDto(review)).thenReturn(TestData.REVIEW_RESPONSE_DTO);

        ReviewResponseDto result = userServiceImpl.addReview(TestData.REVIEW_REQUEST_DTO, userId);

        assertEquals(TestData.REVIEW_RESPONSE_DTO, result);
    }

    @Test
    void addReview_UserNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
            assertThrows(EntityInPizzeriaNotFoundException.class, () -> {
            userServiceImpl.addReview(TestData.REVIEW_REQUEST_DTO, userId);
        });
    }

    @Test
    void addReview_UserBlocked_ThrowUserBlockedException() {
        Long userId = 1L;
        UserApp userApp = TestData.USER_APP_BLOCKED;
        when(userRepository.findById(userId)).thenReturn(Optional.of(userApp));
        assertThrows(UserBlockedException.class, () -> {
            userServiceImpl.addReview(TestData.REVIEW_REQUEST_DTO, userId);
        });
    }

    @Test
    void updateReview() {
        Long reviewId = 1L;
        Review existingReview = new Review(1L, "Super", 8, LocalDateTime.now(), TestData.USER_APP);
        when(reviewRepository.save(existingReview)).thenReturn(existingReview);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(userRepository.findById(TestData.USER_APP.getId())).thenReturn(Optional.of(TestData.USER_APP));
        when(reviewMapper.toReviewResponseDto(existingReview)).thenReturn(TestData.REVIEW_RESPONSE_DTO_NEW);
        ReviewResponseDto updatedReviewResponseDto = userServiceImpl.updateReview(reviewId, TestData.REVIEW_REQUEST_DTO, TestData.USER_APP.getId());

        assertEquals(TestData.REVIEW_RESPONSE_DTO_NEW, updatedReviewResponseDto);
        assertEquals(TestData.REVIEW_REQUEST_DTO.comment(), existingReview.getComment());
        assertEquals(TestData.REVIEW_REQUEST_DTO.grade(), existingReview.getGrade());
        verify(reviewRepository, times(1)).save(existingReview);
    }

    @Test
    void updateReview_ReviewNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long reviewId = 1L;
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> {
            userServiceImpl.updateReview(reviewId, TestData.REVIEW_REQUEST_DTO, TestData.USER_APP.getId());
        });
    }

    @Test
    void updateReview_UserNotFound_ThrowEntityInPizzeriaNotFoundException() {
        Long reviewId = 1L;
        Review existingReview = new Review(1L, "Super", 8, LocalDateTime.now(), TestData.USER_APP);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(userRepository.findById(TestData.USER_APP.getId())).thenReturn(Optional.empty());
        assertThrows(EntityInPizzeriaNotFoundException.class, () -> {
            userServiceImpl.updateReview(reviewId, TestData.REVIEW_REQUEST_DTO, TestData.USER_APP.getId());
        });
    }

    @Test
    void updateReview_UserBlocked_ThrowUserBlockedException() {
        Long reviewId = 1L;
        Review review = new Review(1L, "Super", 8, LocalDateTime.now(), null);
        review.setUserApp(TestData.USER_APP_BLOCKED);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        UserApp blockedUserApp = mock(UserApp.class);
        when(userRepository.findById(any())).thenReturn(Optional.of(blockedUserApp));
        when(blockedUserApp.isBlocked()).thenReturn(true);

        assertThrows(UserBlockedException.class, () -> {
            userServiceImpl.updateReview(reviewId, TestData.REVIEW_REQUEST_DTO, TestData.USER_APP_BLOCKED.getId());
        });
    }

    @Test
    void updateReview_InvalidUser_ThrowUpdateReviewException() {
        Long reviewId = 1L;
        Review review = new Review(1L, "Super", 8, LocalDateTime.now(), null);
        review.setUserApp(TestData.USER_APP_2);
        UserApp userApp = TestData.USER_APP;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userRepository.findById(userApp.getId())).thenReturn(Optional.of(userApp));
        assertThrows(UpdateReviewException.class, () -> {
            userServiceImpl.updateReview(reviewId, TestData.REVIEW_REQUEST_DTO, userApp.getId());
        });
    }
    @Test
    void deleteReview() {
        long reviewId = 2L;
        Review review = new Review(reviewId, "Good pizza", 10, LocalDateTime.now(), TestData.USER_APP);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        assertDoesNotThrow(() -> userServiceImpl.deleteReview(reviewId));

        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void deleteReview_InvalidIdReview_ThrowInvalidIDException() {
        long reviewId = 1L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());
        assertThrows(InvalidIDException.class, () -> userServiceImpl.deleteReview(reviewId));
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void getAllReview() {
        Review review1 = new Review(1L, "Good pizza", 10, LocalDateTime.now(), TestData.USER_APP);
        Review review2 = new Review(2L, "Super", 5, LocalDateTime.now(), TestData.USER_APP);
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(review1, review2));
        when(reviewMapper.toReviewResponseDto(review1)).thenReturn(TestData.REVIEW_RESPONSE_DTO);

        List<ReviewResponseDto> result = userServiceImpl.getAllReview();

        assertEquals(2, result.size());
        verify(reviewRepository, times(1)).findAll();
        verify(reviewMapper, times(2)).toReviewResponseDto(any());
    }

    @Test
    void getAllReview_EmptyList() {
        when(reviewRepository.findAll()).thenReturn(List.of());
        List<ReviewResponseDto> reviewResponseDtoList = userServiceImpl.getAllReview();
        assertEquals(new ArrayList<>(), reviewResponseDtoList);
        verify(reviewRepository, times(1)).findAll();
        verify(reviewMapper, never()).toReviewResponseDto(any());
    }

    @Test
    void getAllReviewByUser() {
        Review review1 = new Review(1L, "Good pizza", 10, LocalDateTime.now(), TestData.USER_APP);
        when(reviewRepository.findAllByUserApp_Id(TestData.USER_APP.getId())).thenReturn(List.of(review1));
        when(reviewMapper.toReviewResponseDto(review1)).thenReturn(TestData.REVIEW_RESPONSE_DTO);
        List<ReviewResponseDto> result = userServiceImpl.getAllReviewByUser(1L);

        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findAllByUserApp_Id(TestData.USER_APP.getId());
        verify(reviewMapper, times(1)).toReviewResponseDto(any());
    }

    @Test
    void getAllReviewByUser_NoReviewsFound() {
        Long userId = 1L;
        when(reviewRepository.findAllByUserApp_Id(userId)).thenReturn(Collections.emptyList());
        List<ReviewResponseDto> result = userServiceImpl.getAllReviewByUser(userId);
        assertEquals(0, result.size());
    }

    @Test
    void getAllReviewByPeriod() {
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 5, 1, 0, 0);
        Review review1 = new Review(1L, "Good pizza", 10, LocalDateTime.now(), TestData.USER_APP);
        when(reviewRepository.findAllByReviewDateBetween(startDate, endDate)).thenReturn(List.of(review1));
        when(reviewMapper.toReviewResponseDto(review1)).thenReturn(TestData.REVIEW_RESPONSE_DTO);
        List<ReviewResponseDto> result = userServiceImpl.getAllReviewByPeriod(startDate, endDate);

        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findAllByReviewDateBetween(startDate, endDate);
        verify(reviewMapper, times(1)).toReviewResponseDto(any());
    }

    @Test
    void getAllReviewByPeriod_NoReviewsFound() {
        LocalDateTime startDate = LocalDateTime.of(2024, 3, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 16, 23, 59);
        when(reviewRepository.findAllByReviewDateBetween(startDate, endDate)).thenReturn(List.of());
        List<ReviewResponseDto> result = userServiceImpl.getAllReviewByPeriod(startDate, endDate);
        assertEquals(0, result.size());
        verify(reviewRepository, times(1)).findAllByReviewDateBetween(startDate, endDate);
        verify(reviewMapper, never()).toReviewResponseDto(any());
    }
}