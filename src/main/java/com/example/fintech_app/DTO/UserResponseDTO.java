package com.example.fintech_app.DTO;

import com.example.fintech_app.models.Roles;

public record UserResponseDTO (

     String username,
     String email,
     Roles role){
}
