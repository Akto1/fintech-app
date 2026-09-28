package com.example.fintech_app.controllers;

import com.example.fintech_app.DTO.UserCreateDTO;
import com.example.fintech_app.DTO.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.fintech_app.services.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class userController {
    private final UserService service;
    @PostMapping("/register")
    public  UserResponseDTO createUser(@RequestBody UserCreateDTO user){
    return service.createUser(user);
    }
}
