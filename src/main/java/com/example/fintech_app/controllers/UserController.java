package com.example.fintech_app.controllers;

import com.example.fintech_app.DTO.UserCreateDTO;
import com.example.fintech_app.DTO.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.fintech_app.services.UserService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService service;
    @PostMapping("/register")
    public  UserResponseDTO createUser(@RequestBody UserCreateDTO user){
    return service.createUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserCreateDTO user){
        return service.login(user);
    }

    @GetMapping("/myprofile")
    public String aboutMe(Principal principal){
        String email = principal.getName();
        return service.aboutMe(email);
    }
}
