package org.example.pizzeria.service.auth;

import jakarta.transaction.Transactional;
import org.example.pizzeria.TestData;
import org.example.pizzeria.dto.user.auth.JwtAuthenticationResponse;
import org.example.pizzeria.dto.user.auth.UserRegisterRequestDto;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.user.UnauthorizedException;
import org.example.pizzeria.exception.user.UserCreateException;
import org.example.pizzeria.mapper.user.UserMapper;
import org.example.pizzeria.repository.user.UserRepository;
import org.example.pizzeria.service.user.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userRepository);
    }

    @Test
    @Transactional
    void registration() {
        String password = "validPassword";
        String jwt = "d5679ab7-260a-42a3-87ab-49c72b3d7407";

        when(passwordEncoder.encode(TestData.USER_REGISTER_REQUEST_DTO.password())).thenReturn(password);
        when(userService.save(TestData.USER_REGISTER_REQUEST_DTO, password)).thenReturn(TestData.USER_RESPONSE_DTO);
        when(userRepository.getReferenceById(1L)).thenReturn(TestData.USER_APP);
        when(jwtService.generateToken(TestData.USER_APP)).thenReturn(jwt);
        when(userMapper.toUserResponseDto(TestData.USER_APP)).thenReturn(TestData.USER_RESPONSE_DTO);
        JwtAuthenticationResponse response = authenticationService.registration(TestData.USER_REGISTER_REQUEST_DTO);
        assertEquals(TestData.JWT_AUTHENTICATION_RESPONSE, response);
    }

    @Test
    @Transactional
    public void registration_InvalidUserData_ThenExceptionThrown() {
        UserApp existingUser = TestData.USER_APP;
        userRepository.save(existingUser);
        UserRegisterRequestDto request = TestData.USER_REGISTER_REQUEST_DTO;
        Assertions.assertThrows(UserCreateException.class, () -> authenticationService.registration(request));
    }

    @Test
    void authentication() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(TestData.USER_LOGIN_FORM_REQUEST_DTO.getUserName(),
                        TestData.USER_LOGIN_FORM_REQUEST_DTO.getPassword()));

        UserDetails userDetails = new User("IvanAdmin", "validPassword", new ArrayList<>());
        when(userDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(userDetails);
        Mockito.when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userService.userDetailsService().loadUserByUsername(TestData.USER_LOGIN_FORM_REQUEST_DTO.getUserName()))
                .thenReturn(userDetails);
        String token = "validToken";
        when(jwtService.generateToken(userDetails)).thenReturn(token);
        UserApp user = TestData.USER_APP;
        when(userRepository.findByUserName(TestData.USER_LOGIN_FORM_REQUEST_DTO.getUserName())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(TestData.USER_RESPONSE_DTO);
        JwtAuthenticationResponse response = authenticationService.authentication(TestData.USER_LOGIN_FORM_REQUEST_DTO);
        Assertions.assertNotNull(response);
        assertEquals(token, response.getToken());
    }

    @Test
    public void authentication_InvalidCredentials_ThenExceptionThrown() {
        Mockito.when(authenticationManager.authenticate(any()))
                .thenThrow(UnauthorizedException.class);
        Assertions.assertThrows(UnauthorizedException.class, () -> authenticationService.authentication(TestData.USER_LOGIN_FORM_REQUEST_DTO));
    }


}