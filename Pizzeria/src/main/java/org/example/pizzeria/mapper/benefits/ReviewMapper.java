package org.example.pizzeria.mapper.benefits;

import jakarta.annotation.Nullable;
import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.user.UserApp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "reviewDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "userApp", ignore = true)
    @Mapping(target = "id", ignore = true)
    Review toReview(String comment, Integer grade);

    @Mapping(target = "userName", source = "userApp.username")
    ReviewResponseDto toReviewResponseDto(Review review);

}
