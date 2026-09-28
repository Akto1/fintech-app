package com.example.fintech_app.services;

import com.example.fintech_app.DTO.UserCreateDTO;
import com.example.fintech_app.DTO.UserResponseDTO;
import com.example.fintech_app.Repositories.UserRepository;
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

    public UserResponseDTO createUser(UserCreateDTO dto){
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
}
