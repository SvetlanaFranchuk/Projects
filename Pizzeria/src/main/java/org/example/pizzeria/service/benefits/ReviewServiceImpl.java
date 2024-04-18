package org.example.pizzeria.service.benefits;

import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.user.UpdateReviewException;
import org.example.pizzeria.exception.user.UserBlockedException;
import org.example.pizzeria.mapper.benefits.ReviewMapper;
import org.example.pizzeria.repository.benefits.ReviewRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    public UserRepository userRepository;
    public ReviewRepository reviewRepository;
    public ReviewMapper reviewMapper;

    @Autowired
    public ReviewServiceImpl(UserRepository userRepository, ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional
    public ReviewResponseDto add(ReviewRequestDto reviewRequestDto, Long userId) {
        UserApp userApp = userRepository.findById(userId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));
        if (userApp.isBlocked())
            throw new UserBlockedException(ErrorMessage.USER_BLOCKED);
        Review review = reviewMapper.toReview(reviewRequestDto.comment(), reviewRequestDto.grade());
        review.setUserApp(userApp);
        userApp.addReview(review);
        userRepository.save(userApp);
        return reviewMapper.toReviewResponseDto(review);
    }

    @Override
    @Transactional
    public ReviewResponseDto update(Long reviewId, ReviewRequestDto reviewRequestDto, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Review", ErrorMessage.ENTITY_NOT_FOUND));
        UserApp userApp = userRepository.findById(userId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));
        if (userApp.isBlocked()) {
            throw new UserBlockedException(ErrorMessage.USER_BLOCKED);
        }
        if (!Objects.equals(review.getUserApp().getId(), userApp.getId())) {
            throw new UpdateReviewException(ErrorMessage.CANT_REVIEW_UPDATED);
        }
        review.setComment(reviewRequestDto.comment());
        review.setGrade(reviewRequestDto.grade());
        return reviewMapper.toReviewResponseDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent()) {
            reviewRepository.delete(review.get());
        } else {
            throw new InvalidIDException(ErrorMessage.INVALID_ID);
        }
    }

    @Override
    public List<ReviewResponseDto> getAll() {
        return reviewRepository.findAll().stream()
                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
    }

    @Override
    public List<ReviewResponseDto> getAllByUser(Long userId) {
        return reviewRepository.findAllByUserApp_Id(userId).stream()
                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
    }

    @Override
    public List<ReviewResponseDto> getAllByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return reviewRepository.findAllByReviewDateBetween(startDate, endDate).stream()
                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
    }
}
