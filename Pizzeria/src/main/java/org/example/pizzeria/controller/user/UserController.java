package org.example.pizzeria.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.dto.user.UserBonusDto;
import org.example.pizzeria.dto.user.UserRequestDto;
import org.example.pizzeria.dto.user.UserResponseDto;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(
        name = "User controller",
        description = "All commonly methods for working with information about users"
)
@Validated
@RestController
@RequestMapping(path = "user",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
public class UserController {
    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Operation(summary = "Getting information about user")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@Parameter(description = "user id")
                                                       @PathVariable("id")
                                                       @Positive
                                                       @NotNull Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @Operation(summary = "Updating information about user")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@Parameter(description = "User id")
                                                      @PathVariable("id")
                                                      @Positive
                                                      @NotNull
                                                      Long id,
                                                      @RequestBody @Valid UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.update(id, userRequestDto));
    }

    @Operation(summary = "Retrieving information about users bonus")
    @GetMapping("/getBonus/{id}")
    public ResponseEntity<UserBonusDto> getUserBonus(@Parameter(description = "User id")
                                                     @PathVariable("id") @Positive @NotNull Long id) {
        return ResponseEntity.ok(userService.getUserBonus(id));
    }

    @Operation(summary = "Updating information about users bonus")
    @PutMapping("/updateBonus/{id}")
    public ResponseEntity<UserBonusDto> updateUserBonus(@Parameter(description = "User id")
                                                        @PathVariable("id") @Positive @NotNull Long id,
                                                        @Parameter(description = "Count new ordered pizzas")
                                                        @RequestParam @Valid @Positive @NotNull int count,
                                                        @Parameter(description = "Sum new ordered pizzas")
                                                        @RequestParam @Valid @PositiveOrZero double sum) {
        return ResponseEntity.ok(userService.updateUserBonus(id, count, sum));
    }

    @Operation(summary = "Adding review")
    @PostMapping("/addReview/{id}")
    public ResponseEntity<ReviewResponseDto> addReview(@Parameter(description = "User id")
                                                       @PathVariable("id") @Positive @NotNull Long id,
                                                       @RequestBody @Valid ReviewRequestDto reviewRequestDto) {
        return ResponseEntity.ok(userService.addReview(reviewRequestDto, id));
    }

    @Operation(summary = "Update review")
    @PatchMapping("/updateReview/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(@Parameter(description = "Id review")
                                                          @PathVariable @Positive @NotNull Long id,
                                                          @Parameter(description = "Id review")
                                                          @RequestParam @Positive @NotNull Long userId,
                                                          @RequestBody @Valid ReviewRequestDto reviewRequestDto) {
        return ResponseEntity.ok(userService.updateReview(id, reviewRequestDto, userId));
    }

    @Operation(summary = "Delete review")
    @DeleteMapping("/deleteReview/{id}")
    public ResponseEntity<String> deleteReview(@Parameter(description = "Id review")
                                               @PathVariable @Positive @NotNull Long id) {
        userService.deleteReview(id);
        return ResponseEntity.ok("Review deleted successfully");
    }

    @Operation(summary = "Getting all review")
    @GetMapping("/getAllReview")
    public List<ReviewResponseDto> getAllReview() {
        return userService.getAllReview();
    }

    @Operation(summary = "Getting all review by user")
    @GetMapping("/getAllReviewByUser/{id}")
    public List<ReviewResponseDto> getAllReviewByUser(@Parameter(description = "User id")
                                                      @PathVariable @Positive @NotNull Long id) {
        return userService.getAllReviewByUser(id);
    }

    @Operation(summary = "Getting all review by period")
    @GetMapping("/getAllReviewByPeriod")
    public List<ReviewResponseDto> getAllReviewByPeriod(@Parameter(description = "Start date. Format 2024-01-01T00:00:00")
                                                        @RequestParam
                                                        @NotNull
                                                        @Past LocalDateTime startDate,
                                                        @Parameter(description = "End date. Format 2024-01-01T00:00:00")
                                                        @RequestParam
                                                        @NotNull
                                                        @Past LocalDateTime endDate) {
        return userService.getAllReviewByPeriod(startDate, endDate);
    }
}