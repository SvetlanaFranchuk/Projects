package org.example.pizzeria.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "User profile form")
public record UserRequestDto(
        @Schema(description = "user name")
        @Size(min = 5, max = 25, message = "User name length could be from 5 to 25 symbols")
        String userName,
        @Schema(description = "password")
        @Size(min = 5, max = 15, message = "Password length could be from 5 to 15 symbols")
        String password,
        @Schema(description = "e-mail")
        @Email
        @NotBlank
        String email,
        @Schema(description = "User birthday in format yyyy-MM-dd", example = "2004-08-29")
        @Past(message = "Date of birth must be in the past")
        LocalDate birthDate,
        @Schema(description = "City")
        @Size(max = 75, message = "City length could be less than 76 symbols")
        String addressCity,
        @Schema(description = "Street name")
        @Size(max = 75, message = "Street name length could be less than 76 symbols")
        String addressStreetName,
        @Schema(description = "House number")
        @Size(max = 5, message = "House number length could be less than 6 symbols")
        String addressHouseNumber,
        @Schema(description = "Apartment number")
        @Size(max = 5, message = "Apartment number length could be less than 6 symbols")
        String addressApartmentNumber,
        @Schema(description = "Phone number")
        @Size(min = 5, max = 15, message = "Phone number length could be from 5 to 15 symbols")
        String phoneNumber) {
}
