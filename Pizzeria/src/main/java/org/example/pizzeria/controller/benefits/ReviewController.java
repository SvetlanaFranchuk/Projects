package org.example.pizzeria.controller.benefits;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.service.benefits.ReviewServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(
        name = "Review controller",
        description = "All commonly methods for working with information about review users"
)
@Validated
@RestController
@RequestMapping(path = "/review",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
public class ReviewController {
    private final ReviewServiceImpl reviewService;

    @Autowired
    public ReviewController(ReviewServiceImpl reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Adding review")
    @PostMapping("/add/{id}")
    public ResponseEntity<ReviewResponseDto> add(@Parameter(description = "User id")
                                                 @PathVariable("id") @Positive @NotNull Long id,
                                                 @RequestBody @Valid ReviewRequestDto reviewRequestDto) {
        return ResponseEntity.ok(reviewService.add(reviewRequestDto, id));
    }

    @Operation(summary = "Update review")
    @PatchMapping("/update/{id}")
    public ResponseEntity<ReviewResponseDto> update(@Parameter(description = "Id review")
                                                    @PathVariable @Positive @NotNull Long id,
                                                    @Parameter(description = "Id review")
                                                    @RequestParam @Positive @NotNull Long userId,
                                                    @RequestBody @Valid ReviewRequestDto reviewRequestDto) {
        return ResponseEntity.ok(reviewService.update(id, reviewRequestDto, userId));
    }

    @Operation(summary = "Delete review")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@Parameter(description = "Id review")
                                         @PathVariable @Positive @NotNull Long id) {
        reviewService.delete(id);
        return ResponseEntity.ok("Review deleted successfully");
    }

    @Operation(summary = "Getting all review")
    @GetMapping("/getAll")
    public List<ReviewResponseDto> getAll() {
        return reviewService.getAll();
    }

    @Operation(summary = "Getting all review by user")
    @GetMapping("/getAllByUser/{id}")
    public List<ReviewResponseDto> getAllByUser(@Parameter(description = "User id")
                                                @PathVariable @Positive @NotNull Long id) {
        return reviewService.getAllByUser(id);
    }

    @Operation(summary = "Getting all review by period")
    @GetMapping("/getAllByPeriod")
    public List<ReviewResponseDto> getAllByPeriod(@Parameter(description = "Start date. Format 2024-01-01T00:00:00")
                                                  @RequestParam
                                                  @NotNull
                                                  @Past LocalDateTime startDate,
                                                  @Parameter(description = "End date. Format 2024-01-01T00:00:00")
                                                  @RequestParam
                                                  @NotNull
                                                  @Past LocalDateTime endDate) {
        return reviewService.getAllByPeriod(startDate, endDate);
    }
}