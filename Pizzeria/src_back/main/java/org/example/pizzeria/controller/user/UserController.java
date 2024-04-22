package org.example.pizzeria.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.pizzeria.dto.user.UserBonusDto;
import org.example.pizzeria.dto.user.UserResponseDto;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "User controller",
        description = "All commonly methods for working with information about users"
)
@Validated
@RestController
@RequestMapping(path = "/user",
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

    @Operation(summary = "Retrieving information about users bonus")
    @GetMapping("/getBonus/{id}")
    public ResponseEntity<UserBonusDto> getBonus(@Parameter(description = "User id")
                                                 @PathVariable("id") @Positive @NotNull Long id) {
        return ResponseEntity.ok(userService.getBonus(id));
    }

    @Operation(summary = "Updating information about users bonus")
    @PutMapping("/updateBonus/{id}")
    public ResponseEntity<UserBonusDto> updateBonus(@Parameter(description = "User id")
                                                    @PathVariable("id") @Positive @NotNull Long id,
                                                    @Parameter(description = "Count new ordered pizzas")
                                                    @RequestParam @Valid @Positive @NotNull int count,
                                                    @Parameter(description = "Sum new ordered pizzas")
                                                    @RequestParam @Valid @PositiveOrZero double sum) {
        return ResponseEntity.ok(userService.updateBonus(id, count, sum));
    }

}