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

    /**
     * Adds a review provided by a user.
     * *
     * This method adds a review provided by a user. It first retrieves the user by ID and throws an
     * EntityInPizzeriaNotFoundException if the specified user is not found. Then, it checks if the user
     * is blocked and throws a UserBlockedException if the user is blocked. Next, it maps the review details
     * from the ReviewRequestDto object to a Review entity, sets the user associated with the review,
     * adds the review to the user's reviews, saves the updated user entity, and finally maps the review entity
     * to a ReviewResponseDto object and returns it.
     *
     * @param reviewRequestDto the ReviewRequestDto object containing the review details
     * @param userId the ID of the user submitting the review
     * @return a ReviewResponseDto object representing the added review
     * @throws EntityInPizzeriaNotFoundException if the specified user is not found
     * @throws UserBlockedException if the specified user is blocked
     */
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

    /**
     * Updates an existing review provided by a user.
     * *
     * This method updates an existing review provided by a user. It first retrieves the review by ID and throws an
     * EntityInPizzeriaNotFoundException if the specified review is not found. Then, it retrieves the user by ID
     * and throws an EntityInPizzeriaNotFoundException if the specified user is not found. Next, it checks if the
     * user is blocked and throws a UserBlockedException if the user is blocked. After that, it checks if the user
     * is allowed to update the review and throws an UpdateReviewException if the user is not allowed to update the
     * review. Finally, it updates the review with the new comment and grade, saves the updated review entity, and
     * returns the mapped ReviewResponseDto object.
     *
     * @param reviewId the ID of the review to be updated
     * @param reviewRequestDto the ReviewRequestDto object containing the updated review details
     * @param userId the ID of the user updating the review
     * @return a ReviewResponseDto object representing the updated review
     * @throws EntityInPizzeriaNotFoundException if the specified review or user is not found
     * @throws UserBlockedException if the specified user is blocked
     * @throws UpdateReviewException if the user is not allowed to update the review
     */
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

    /**
     * Deletes a review by its ID.
     * *
     * This method deletes a review by its ID. It first retrieves the review by ID using the findById method of
     * the reviewRepository. If the review is present, it deletes the review using the delete method of the
     * reviewRepository. If the review is not present, it throws an InvalidIDException with the message "Invalid ID"
     *
     * @param id the ID of the review to be deleted
     * @throws InvalidIDException if the specified ID is invalid
     */
    @Override
    @Transactional
    public void delete(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(()->new EntityInPizzeriaNotFoundException("Review", ErrorMessage.ENTITY_NOT_FOUND));
        UserApp userApp = review.getUserApp();
        userApp.removeReview(review);
        reviewRepository.deleteById(id);
    }

    /**
     * Retrieves all reviews.
     * *
     * This method retrieves all reviews from the database using the findAll method of the reviewRepository.
     * It then maps each Review object to a ReviewResponseDto object using the toReviewResponseDto method of
     * the reviewMapper. Finally, it collects the mapped objects into a list and returns the list of
     * ReviewResponseDto objects representing all reviews.
     *
     * @return a list of ReviewResponseDto objects representing all reviews
     */
    @Override
    public List<ReviewResponseDto> getAll() {
        return reviewRepository.findAll().stream()
                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
    }

    /**
     * Retrieves all reviews associated with a specific user.
     * *
     * This method retrieves all reviews associated with a specific user from the database using the
     * findAllByUserApp_Id method of the reviewRepository. It then maps each Review object to a
     * ReviewResponseDto object using the toReviewResponseDto method of the reviewMapper. Finally,
     * it collects the mapped objects into a list and returns the list of ReviewResponseDto objects
     * representing the reviews associated with the user.
     *
     * @param userId the ID of the user
     * @return a list of ReviewResponseDto objects representing the reviews associated with the user
     */
    @Override
    public List<ReviewResponseDto> getAllByUser(Long userId) {
        return reviewRepository.findAllByUserApp_Id(userId).stream()
                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
    }

    /**
     * Retrieves all reviews within a specified period.
     * *
     * This method retrieves all reviews within a specified period from the database using the
     * findAllByReviewDateBetween method of the reviewRepository. It then maps each Review object to a
     * ReviewResponseDto object using the toReviewResponseDto method of the reviewMapper. Finally, it
     * collects the mapped objects into a list and returns the list of ReviewResponseDto objects representing
     * the reviews within the specified period.
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @return a list of ReviewResponseDto objects representing the reviews within the specified period
     */
    @Override
    public List<ReviewResponseDto> getAllByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return reviewRepository.findAllByReviewDateBetween(startDate, endDate).stream()
                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
    }
}
