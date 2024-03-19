package org.example.pizzeria.mapper.benefits;

import jakarta.annotation.Nullable;
import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.entity.benefits.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    Review toReview(@Nullable Long id, ReviewRequestDto reviewRequestDto);
    ReviewResponseDto toReviewResponseDto(Review review);

}
