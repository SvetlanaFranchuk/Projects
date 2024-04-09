package org.example.pizzeria.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.pizzeria.dto.user.auth.JwtAuthenticationResponse;
import org.example.pizzeria.dto.user.auth.UserLoginFormRequestDto;
import org.example.pizzeria.dto.user.auth.UserRegisterRequestDto;
import org.example.pizzeria.service.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "User controller for authenticate"
)
@Validated
@RestController
@RequestMapping(path = "auth",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class AuthUserController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthUserController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "New User Registration", description = "The new user has the Client role by default")
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> registerUser(@RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto) {
        JwtAuthenticationResponse response = authenticationService.registration(userRegisterRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Authorization")
    @PostMapping("/authentication")
    public JwtAuthenticationResponse authentication(@RequestBody @Valid UserLoginFormRequestDto request) {
        return authenticationService.authentication(request);
    }

}