package com.example.fintech_app.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


public record UserCreateDTO (
    @NotBlank String username,
    @Email @NotBlank String email,
    @Size(min = 6 ,max = 20)
     String password){}

