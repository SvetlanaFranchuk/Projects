package org.example.pizzeria.dto.user.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Form for authenticate user")
public class UserLoginFormRequestDto {
    @Schema(description = "user name")
    @Size(min = 5, max = 25, message = "User name length could be from 5 to 25 symbols")
    @NotBlank(message = "Username cannot be empty")
    String userName;
    @Schema(description = "password")
    @Size(min = 5, max = 15, message = "Password length could be from 5 to 15 symbols")
    String password;
}
