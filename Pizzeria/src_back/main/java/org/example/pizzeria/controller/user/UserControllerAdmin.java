package org.example.pizzeria.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import org.example.pizzeria.dto.user.UserBlockedResponseDto;
import org.example.pizzeria.dto.user.UserResponseDto;
import org.example.pizzeria.dto.user.UserResponseDtoForAdmin;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(
        name = "User controller for admin",
        description = "All methods for working with information about users"
)
@Validated
@RestController
@RequestMapping(path = "/admin/user",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class UserControllerAdmin {
    private final UserServiceImpl userService;

    @Autowired
    public UserControllerAdmin(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Operation(summary = "Retrieving information about users whose birthday is on a given date")
    @GetMapping("birthday")
    public List<UserResponseDto> getUsersByBirthday(@Parameter(description = "Given date ")
                                                    @RequestParam
                                                    @NotNull
                                                    @Past
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return userService.getUsersByBirthday(date);
    }

    @Operation(summary = "Retrieving information about users whose have role CLIENT")
    @GetMapping("/clients")
    public List<UserResponseDtoForAdmin> getUserByClientRole() {
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
                                                                     @PathVariable("id") @Positive @NotNull Long id,
                                                                     @RequestParam @Valid boolean isBlocked) {
        return ResponseEntity.ok(userService.changeUserBlocking(id, isBlocked));
    }

}