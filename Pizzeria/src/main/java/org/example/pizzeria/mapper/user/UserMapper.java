package org.example.pizzeria.mapper.user;

import org.example.pizzeria.dto.user.UserBlockedResponseDto;
import org.example.pizzeria.dto.user.UserBonusDto;
import org.example.pizzeria.dto.user.UserRegisterRequestDto;
import org.example.pizzeria.dto.user.UserResponseDto;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.user.Address;
import org.example.pizzeria.entity.user.ContactInformation;
import org.example.pizzeria.entity.user.UserApp;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public UserApp toUserApp(UserRegisterRequestDto candidate) {
        return UserApp.builder()
                .userName(candidate.userName())
                .password(candidate.password())
                .email(candidate.email())
                .birthDate(candidate.birthDate())
                .address(new Address(candidate.addressCity(), candidate.addressStreetName(),
                        candidate.addressHouseNumber(), candidate.addressApartmentNumber()))
                .phoneNumber(new ContactInformation(candidate.phoneNumber()))
                .isBlocked(false)
                .role(org.example.pizzeria.entity.user.Role.CLIENT)
                .bonus(new Bonus(0, 0.0))
                .dateRegistration(java.time.LocalDate.now())
                .build();
    }

    public UserResponseDto toUserResponseDto(UserApp userApp) {
        return new UserResponseDto(userApp.getUserName(), userApp.getEmail(), userApp.getBirthDate(),
                userApp.getAddress(), userApp.getPhoneNumber());
    }

    public UserBonusDto toUserBonusDto(Integer countOrders, Double sumOrders) {
        return new UserBonusDto(countOrders, sumOrders);
    }

    public UserBlockedResponseDto toUserBlockedResponseDto(Long id, String userName,
                                                           boolean isBlocked, LocalDateTime reviewDate) {
        return new UserBlockedResponseDto(id, userName, isBlocked, reviewDate);
    }
}
