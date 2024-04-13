package org.example.pizzeria.service.user;

import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.dto.user.*;
import org.example.pizzeria.dto.user.auth.UserRegisterRequestDto;
import org.example.pizzeria.entity.user.UserApp;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    UserResponseDto save(UserRegisterRequestDto candidate, String password);

    UserApp getByUserName(String userName);
    UserDetailsService userDetailsService();
    UserApp getCurrentUser();

    UserResponseDto getUser(Long id);

    UserResponseDto update(Long id, UserRequestDto userRequestDto);

    List<UserResponseDto> getUsersByBirthday(LocalDate date);

    List<UserResponseDtoForAdmin> getUserByClientRole();

    List<UserBlockedResponseDto> getUserBlocked();

    UserBlockedResponseDto changeUserBlocking (Long id, boolean isBlocked);

    UserBonusDto getUserBonus(Long userId);

    UserBonusDto updateUserBonus(Long userId, int count, double sum);

    ReviewResponseDto addReview(ReviewRequestDto reviewRequestDto, Long userId);

    ReviewResponseDto updateReview(Long id, ReviewRequestDto reviewRequestDto, Long userId);

    void deleteReview(Long id);

    List<ReviewResponseDto> getAllReview();

    List<ReviewResponseDto> getAllReviewByUser(Long userId);

    List<ReviewResponseDto> getAllReviewByPeriod(LocalDateTime startDate, LocalDateTime endDate);


}
