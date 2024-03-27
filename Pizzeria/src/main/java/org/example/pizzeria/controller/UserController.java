package org.example.pizzeria.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.dto.user.*;
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
            description = "The new user has the Client role by default")
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

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable("id") @Min(0) Long id, @RequestBody @Valid UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.update(id, userRequestDto);
        if (userRequestDto != null) {
            return ResponseEntity.ok(userResponseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("birthday")
    public List<UserResponseDto> getUsersByBirthday(@RequestParam LocalDate date) {
        return userService.getUsersByBirthday(date);
    }

    @GetMapping("/clients")
    public List<UserResponseDto> getUserByClientRole() {
        return userService.getUserByClientRole();
    }

    @GetMapping("/blocked_clients")
    public List<UserBlockedResponseDto> getUserBlocked() {
        return userService.getUserBlocked();
    }

    @PutMapping("/change_blocking/{id}")
    public ResponseEntity<UserBlockedResponseDto> changeBlockingUser(@PathVariable("id") @Min(0) Long id,
                                                                     @RequestParam boolean isBlocked) {
        UserBlockedResponseDto userBlockedResponseDto = userService.changeUserBlocking(id, isBlocked);
        if (userBlockedResponseDto != null) {
            return ResponseEntity.ok(userBlockedResponseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//
//    @GetMapping("/getBonus/{id}")
//    public UserBonusDto getUserBonus(@PathVariable("id") @Min(0) Long id) {
//        return userService.getUserBonus(id);
//    }
//
//    @PutMapping("/updateBonus/{}id")
//    public ResponseEntity<UserBonusDto> updateUserBonus(@PathVariable("id") @Min(0) Long id,
//                                                        @RequestParam int count, @RequestParam double sum) {
//        UserBonusDto userBonusDto = userService.updateUserBonus(id, count, sum);
//        if (userBonusDto != null) {
//            return ResponseEntity.ok(userBonusDto);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @PostMapping("/addReview")
//    public ResponseEntity<ReviewResponseDto> addReview(@RequestBody @Valid ReviewRequestDto reviewRequestDto) {
//        ReviewResponseDto reviewResponseDto = userService.addReview(reviewRequestDto);
//        if (reviewResponseDto != null) {
//            return ResponseEntity.ok(reviewResponseDto);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @PutMapping("/updateReview")
//    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable @Min(0) Long id, @RequestBody @Valid ReviewRequestDto reviewRequestDto) {
//        ReviewResponseDto reviewResponseDto = userService.updateReview(id, reviewRequestDto);
//        if (reviewResponseDto != null) {
//            return ResponseEntity.ok(reviewResponseDto);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @DeleteMapping("/deleteReview/{id}")
//    public ResponseEntity<String> deleteReview(Long id) {
//        userService.deleteReview(id);
//        return ResponseEntity.ok("Review deleted successfully");
//    }
//
//    @GetMapping("getAllReview")
//    public List<ReviewResponseDto> getAllReview() {
//        return userService.getAllReview();
//    }
//
//    @GetMapping("getAllReviewByUser/{id}")
//    public List<ReviewResponseDto> getAllReviewByUser(@PathVariable @Min(0) Long id) {
//        return userService.getAllReviewByUser(id);
//    }
//
//    @GetMapping("getAllReviewByPeriod/{startDate}/{endDate}")
//    public List<ReviewResponseDto> getAllReviewByPeriod(@RequestParam LocalDateTime startDate,
//                                                        @RequestParam LocalDateTime endDate) {
//        return userService.getAllReviewByPeriod(startDate, endDate);
//    }
}