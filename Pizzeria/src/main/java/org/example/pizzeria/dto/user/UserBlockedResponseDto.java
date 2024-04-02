package org.example.pizzeria.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Form for blocking user")
public class UserBlockedResponseDto {
    @Schema(description = "user id")
    private  @PositiveOrZero Long id;
    @Schema(description = "user name")
    private  @Size(min = 5, max = 25, message = "User name length could be from 5 to 25 symbols") String userName;
    @Schema(description = "is blocked")
    @BooleanFlag
    private  boolean isBlocked;
    @Schema(description = "data review")
    private  @PastOrPresent LocalDateTime reviewDate;
}
