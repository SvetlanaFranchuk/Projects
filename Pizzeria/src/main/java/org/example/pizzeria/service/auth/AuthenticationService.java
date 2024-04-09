package org.example.pizzeria.service.auth;

import lombok.RequiredArgsConstructor;
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
import org.springdoc.api.ErrorMessage;
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
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

   @Autowired
   public AuthenticationService(UserServiceImpl userService, JwtService jwtService, PasswordEncoder passwordEncoder,
                                AuthenticationManager authenticationManager, UserRepository userRepository,
                                UserMapper userMapper) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
            String encodedPassword = passwordEncoder.encode(request.password());
            UserResponseDto userResponseDto = userService.save(request, encodedPassword);
            var user = userRepository.getReferenceById(userResponseDto.getId());
            var jwt = jwtService.generateToken(user);
            return new JwtAuthenticationResponse(jwt, userMapper.toUserResponseDto(user), user.getRole());
        } catch (RuntimeException e){
            throw new UserCreateException("Failed to register user");
        }
       }
    /**
     * Authenticate user
     *
     * @param request data of user
     * @return token
     *
     * */
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
}
