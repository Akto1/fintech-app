package com.example.fintech_app.services;

import com.example.fintech_app.DTO.UserCreateDTO;
import com.example.fintech_app.DTO.UserResponseDTO;
import com.example.fintech_app.Repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import com.example.fintech_app.models.Roles;
import com.example.fintech_app.models.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JWTService jwtService;
    public UserResponseDTO createUser(@Valid  UserCreateDTO dto){
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(encoder.encode(dto.password()));
        user.setRoles(Roles.ROLE_USER);
        user.setBalance(BigDecimal.ZERO);
        User savedUser = userRepository.save(user);
        return new UserResponseDTO(
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRoles());
    }

    public String login(@Valid UserCreateDTO dto){
        User existingUser = userRepository.findByEmail(dto.email()).orElseThrow(()-> new RuntimeException("Incorrect email "));
    if(!encoder.matches(dto.password(), existingUser.getPassword())){
    throw new RuntimeException("Incorrect password");
    }
    String token = jwtService.generateToken(existingUser.getEmail());
        System.out.println("Token " + token);
    return  token;

    }


    public String aboutMe(String email){
        User findUser = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Incorrect email "));
        return "Your name : " + findUser.getUsername()+ "\nYour id : " + findUser.getId()
                + "\n" + "Your balance : " + findUser.getBalance() + " Tenge\n" + "Your role : " + findUser.getRoles();
    }
}
