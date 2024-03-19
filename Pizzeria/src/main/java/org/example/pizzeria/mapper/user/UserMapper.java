package org.example.pizzeria.mapper.user;

//import org.example.pizzeria.dto.user.UserBlockedResponseDto;
//import org.example.pizzeria.dto.user.UserBonusDto;
//import org.example.pizzeria.dto.user.UserResponseDto;
import org.example.pizzeria.dto.user.UserBlockedResponseDto;
import org.example.pizzeria.dto.user.UserBonusDto;
import org.example.pizzeria.dto.user.UserResponseDto;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.user.Address;
import org.example.pizzeria.entity.user.ContactInformation;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    @Mapping(target = "dateRegistration", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "basket", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    UserApp toUserApp(String userName, String password, String email, LocalDate birthDate,
                      Address address, ContactInformation phoneNumber, boolean isBlocked, Role role,
                      Bonus bonus);

    UserResponseDto toUserResponseDto(UserApp userApp);

    UserBonusDto toUserBonusDto(Integer countOrders, Double sumOrders);

    UserBlockedResponseDto toUserBlockedResponseDto(Long id, String userName, boolean isBlocked, LocalDateTime reviewDate);
}
