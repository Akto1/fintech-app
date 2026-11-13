package com.example.fintech_app.tests;

import com.example.fintech_app.DTO.UserCreateDTO;
import com.example.fintech_app.Repositories.UserRepository;
import com.example.fintech_app.models.User;
import com.example.fintech_app.services.JWTService;
import com.example.fintech_app.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private JWTService jwtService;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("Incorrect Password")
    void login() {
        User user = new User();
        user.setUsername("emil");
        user.setEmail("emil123@gmail.com");
        user.setPassword("pass1234");


        UserCreateDTO dto = new UserCreateDTO("emil", "emil123@gmail.com", "pass123");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches("pass123", "pass1234")).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> userService.login(dto));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("NotFoundUser")
    void notFound() {

        UserCreateDTO dto = new UserCreateDTO("boris", "123@gmail.com", "123");
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class, () -> userService.login(dto));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("UserAlreadyExists")
    void userExists() {
        User user = new User();
        user.setUsername("raul");
        user.setEmail("raul123@gmail.com");
        user.setPassword("1234");

        UserCreateDTO dto = new UserCreateDTO(user.getUsername(), user.getEmail(), user.getPassword());
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Success")
    void success() {
        User user = new User();
        user.setUsername("Alesha");
        user.setEmail("alesha1234@gmail.com");
        user.setPassword("encoded_pass");

        UserCreateDTO dto = new UserCreateDTO("Alesha","alesha1234@gmail.com","1234");
        when(userRepository.findByEmail("alesha1234@gmail.com")).thenReturn(Optional.of(user));
        when(encoder.matches("1234","encoded_pass")).thenReturn(true);
        when(jwtService.generateToken("alesha1234@gmail.com")).thenReturn("fake_token");
        String result = userService.login(dto);
        assertEquals("fake_token",result);
    verify(jwtService).generateToken("alesha1234@gmail.com");

    }
}
