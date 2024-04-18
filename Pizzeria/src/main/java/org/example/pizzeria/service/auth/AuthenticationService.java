package org.example.pizzeria.service.auth;

import org.example.pizzeria.dto.user.UserRequestDto;
import org.example.pizzeria.dto.user.UserResponseDto;
import org.example.pizzeria.dto.user.auth.JwtAuthenticationResponse;
import org.example.pizzeria.dto.user.auth.UserLoginFormRequestDto;
import org.example.pizzeria.dto.user.auth.UserRegisterRequestDto;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.user.UnauthorizedException;
import org.example.pizzeria.exception.user.UserCreateException;
import org.example.pizzeria.mapper.user.UserMapper;
import org.example.pizzeria.repository.user.UserRepository;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
    private final UserServiceImpl userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UserServiceImpl userService, JwtService jwtService,
                                 AuthenticationManager authenticationManager, UserRepository userRepository,
                                 UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registration user
     *
     * @param request data of user
     * @return token
     */
    @Transactional
    public JwtAuthenticationResponse registration(UserRegisterRequestDto request) {
        try {
            UserResponseDto userResponseDto = userService.save(request, encodePassword(request.password()));
            var user = userRepository.getReferenceById(userResponseDto.getId());
            var jwt = jwtService.generateToken(user);
            return new JwtAuthenticationResponse(jwt, userMapper.toUserResponseDto(user), user.getRole());
        } catch (RuntimeException e) {
            throw new UserCreateException("Failed to register user");
        }
    }

    /**
     * Update user
     *
     * @param userRequestDto data of user
     */
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        return userService.update(id, userRequestDto, encodePassword(userRequestDto.password()));
    }


    /**
     * Authenticate user
     *
     * @param request data of user
     * @return token
     */
    public JwtAuthenticationResponse authentication(UserLoginFormRequestDto request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUserName(),
                    request.getPassword()
            ));
            var user = userService
                    .userDetailsService()
                    .loadUserByUsername(request.getUserName());
            var jwt = jwtService.generateToken(user);
            UserApp userApp = userRepository.findByUserName(user.getUsername()).orElseThrow();
            return new JwtAuthenticationResponse(jwt, userMapper.toUserResponseDto(userApp), userApp.getRole());
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Authentication failed");
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
