package org.example.pizzeria.service.user;

import org.example.pizzeria.dto.user.*;
import org.example.pizzeria.dto.user.auth.UserRegisterRequestDto;
import org.example.pizzeria.entity.user.UserApp;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    UserResponseDto save(UserRegisterRequestDto candidate, String password);

    UserApp getByUserName(String userName);

    UserDetailsService userDetailsService();

    UserApp getCurrentUser();

    UserResponseDto getUser(Long id);

    UserResponseDto update(Long id, UserRequestDto userRequestDto, String password);

    List<UserResponseDto> getUsersByBirthday(LocalDate date);

    List<UserResponseDtoForAdmin> getUserByClientRole();

    List<UserBlockedResponseDto> getUserBlocked();

    UserBlockedResponseDto changeUserBlocking(Long id, boolean isBlocked);

    UserBonusDto getBonus(Long userId);

    UserBonusDto updateBonus(Long userId, int count, double sum);

}
