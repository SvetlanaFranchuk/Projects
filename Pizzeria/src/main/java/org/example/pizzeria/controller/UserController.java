package org.example.pizzeria.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.dto.user.*;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.user.StatusAlreadyExistsException;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Tag(
        name = "User controller",
        description = "All methods for working with information about users"
)
@Validated
@RestController
@RequestMapping(path = "user",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Operation(summary = "New User Registration",
            description =  "The new user has the Client role by default")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userRegisterRequestDto));
    }

    @Operation(summary = "Getting information about user")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@Parameter(description = "user id")
                                                       @PathVariable("id") @Min(0) Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @Operation(summary = "Updating information about user")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@Parameter(description = "User id")
                                                      @PathVariable("id") @Min(0) Long id,
                                                      @RequestBody @Valid UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.update(id, userRequestDto);
        if (userRequestDto != null) {
            return ResponseEntity.ok(userResponseDto);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Retrieving information about users whose birthday is on a given date")
    @GetMapping("birthday")
    public List<UserResponseDto> getUsersByBirthday(@Parameter(description = "Given date ")
                                                    @RequestParam @Valid LocalDate date) {
        return userService.getUsersByBirthday(date);
    }

    @Operation(summary = "Retrieving information about users whose have role CLIENT")
    @GetMapping("/clients")
    public List<UserResponseDto> getUserByClientRole() {
        return userService.getUserByClientRole();
    }

    @Operation(summary = "Retrieving information about users blocked")
    @GetMapping("/blocked_clients")
    public List<UserBlockedResponseDto> getUserBlocked() {
        return userService.getUserBlocked();
    }

    @Operation(summary = "Changing blocked status of user")
    @PutMapping("/change_blocking/{id}")
    public ResponseEntity<UserBlockedResponseDto> changeBlockingUser(@Parameter(description = "User id")
                                                                     @PathVariable("id") @Min(0) Long id,
                                                                     @RequestParam @Valid boolean isBlocked) {
        try {
            UserBlockedResponseDto userBlockedResponseDto = userService.changeUserBlocking(id, isBlocked);
            return ResponseEntity.ok(userBlockedResponseDto);
        } catch (StatusAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new UserBlockedResponseDto());
        } catch (EntityInPizzeriaNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Retrieving information about users bonus")
    @GetMapping("/getBonus/{id}")
    public ResponseEntity<UserBonusDto> getUserBonus(@Parameter(description = "User id")
                                                     @PathVariable("id") @Min(0) Long id) {
        try {
            UserBonusDto userBonusDto = userService.getUserBonus(id);
            return ResponseEntity.ok(userBonusDto);
        } catch (EntityInPizzeriaNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Updating information about users bonus")
    @PutMapping("/updateBonus/{id}")
    public ResponseEntity<UserBonusDto> updateUserBonus(@Parameter(description = "User id")
                                                        @PathVariable("id") @Min(0) Long id,
                                                        @Parameter(description = "Count new ordered pizzas")
                                                        @RequestParam @Valid @Positive int count,
                                                        @Parameter(description = "Sum new ordered pizzas")
                                                        @RequestParam @Valid @PositiveOrZero double sum) {
        UserBonusDto userBonusDto = userService.updateUserBonus(id, count, sum);
        if (userBonusDto != null) {
            return ResponseEntity.ok(userBonusDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Adding review")
    @PostMapping("/addReview/{id}")
    public ResponseEntity<ReviewResponseDto> addReview(@Parameter(description = "User id")
                                                       @PathVariable("id") @Min(0) Long id,
                                                       @RequestBody @Valid ReviewRequestDto reviewRequestDto) {
        ReviewResponseDto reviewResponseDto = userService.addReview(reviewRequestDto, id);
        if (reviewResponseDto != null) {
            return ResponseEntity.ok(reviewResponseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update review")
    @PatchMapping("/updateReview/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(@Parameter(description = "Id review")
                                                          @PathVariable @Min(0) Long id,
                                                          @Parameter(description = "Id review")
                                                          @RequestParam @Min(0) Long userId,
                                                          @RequestBody @Valid ReviewRequestDto reviewRequestDto) {
        ReviewResponseDto reviewResponseDto = userService.updateReview(id, reviewRequestDto, userId);
        if (reviewResponseDto != null) {
            return ResponseEntity.ok(reviewResponseDto);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete review")
    @DeleteMapping("/deleteReview/{id}")
    public ResponseEntity<String> deleteReview(@Parameter(description = "Id review")
                                               @PathVariable @Min(0) Long id) {
        try {
            userService.deleteReview(id);
            return ResponseEntity.ok("Review deleted successfully");
        } catch (InvalidIDException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid review ID");
        }
    }

    @Operation(summary = "Getting all review")
    @GetMapping("/getAllReview")
    public List<ReviewResponseDto> getAllReview() {
        return userService.getAllReview();
    }

    @Operation(summary = "Getting all review by user")
    @GetMapping("/getAllReviewByUser/{id}")
    public List<ReviewResponseDto> getAllReviewByUser(@Parameter(description = "User id")
                                                      @PathVariable @Min(0) Long id) {
        return userService.getAllReviewByUser(id);
    }

    @Operation(summary = "Getting all review by period")
    @GetMapping("/getAllReviewByPeriod")
    public List<ReviewResponseDto> getAllReviewByPeriod(@Parameter(description = "Start date. Format 2024-01-01T00:00:00")
                                                            @RequestParam
                                                            @Valid LocalDateTime startDate,
                                                        @Parameter(description = "End date. Format 2024-01-01T00:00:00")
                                                        @RequestParam
                                                        @Valid LocalDateTime endDate) {
        return userService.getAllReviewByPeriod(startDate, endDate);
    }
}