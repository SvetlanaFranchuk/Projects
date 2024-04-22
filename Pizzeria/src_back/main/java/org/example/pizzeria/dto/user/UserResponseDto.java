package org.example.pizzeria.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.pizzeria.entity.user.Address;
import org.example.pizzeria.entity.user.ContactInformation;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@Schema(description = "User form")
public class UserResponseDto {
    @Schema(description = "user id")
    private Long id;
    @Schema(description = "user name")
    private @Size(min = 5, max = 25, message = "User name length could be from 5 to 25 symbols") String userName;
    @Schema(description = "e-mail")
    private @Email @NotBlank String email;
    @Schema(description = "User birthday in format yyyy-MM-dd", example = "2004-08-29")
    private @NotNull @Past(message = "Date of birth must be in the past") LocalDate birthDate;
    @Schema(description = "This address is used for default delivery from user")
    private Address address;
    @Schema(description = "Phone number")
    private @Size(min = 5, max = 15, message = "Phone number length could be from 5 to 15 symbols")
    ContactInformation phoneNumber;
}
