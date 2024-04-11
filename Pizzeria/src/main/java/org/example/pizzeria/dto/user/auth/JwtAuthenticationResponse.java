package org.example.pizzeria.dto.user.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pizzeria.dto.user.UserResponseDto;
import org.example.pizzeria.entity.user.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Answer with JWT token")
public class JwtAuthenticationResponse {
    @Schema(description = "Token", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    private String token;
    private UserResponseDto userResponseDto;
    private Role role;
}
