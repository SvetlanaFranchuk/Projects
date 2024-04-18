package org.example.pizzeria.service.benefits;

import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewService {
    ReviewResponseDto add(ReviewRequestDto reviewRequestDto, Long userId);

    ReviewResponseDto update(Long id, ReviewRequestDto reviewRequestDto, Long userId);

    void delete(Long id);

    List<ReviewResponseDto> getAll();

    List<ReviewResponseDto> getAllByUser(Long userId);

    List<ReviewResponseDto> getAllByPeriod(LocalDateTime startDate, LocalDateTime endDate);

}
