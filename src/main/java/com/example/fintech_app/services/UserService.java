package com.example.fintech_app.services;

import com.example.fintech_app.DTO.UserCreateDTO;
import com.example.fintech_app.DTO.UserResponseDTO;
import com.example.fintech_app.Exceptions.UserNotFoundException;
import com.example.fintech_app.Repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
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

        if(userRepository.existsByEmail(dto.email())){
            throw new IllegalArgumentException("Пользователь с такой почтой уже зарегистрирован");
        }
        if(userRepository.existsByPhoneNumber(dto.phoneNumber())){
            throw new IllegalArgumentException("Пользователь с таким номером телефона уже зарегистрирован");
        }
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(encoder.encode(dto.password()));
        user.setPhoneNumber(dto.phoneNumber());
        user.setRoles(Roles.ROLE_USER);
        user.setBalance(BigDecimal.ZERO);
        User savedUser = userRepository.save(user);
        return new UserResponseDTO(
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber(),
                savedUser.getRoles());
    }


    public String login(@Valid UserCreateDTO dto){
        User existingUser = userRepository.findByEmail(dto.email()).orElseThrow(()-> new BadCredentialsException("Неверный логин или пароль!"));
    if(!encoder.matches(dto.password(), existingUser.getPassword())){
    throw new BadCredentialsException("Неверный логин или пароль!");
    }

    return jwtService.generateToken(existingUser.getEmail());

    }


    public String aboutMe(String email){
        User findUser = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("Пользователь не найден!"));
        return "Ваше имя : " + findUser.getUsername()+ "\nВаш айди : " + findUser.getId() + "\nВаш номер телефона " + findUser.getPhoneNumber()
                + "\n" + "Ваш баланс : " + findUser.getBalance() + " Тенге\n" + "Ваша роль : " + findUser.getRoles();
    }
}
