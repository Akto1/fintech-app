package com.example.fintech_app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.example.fintech_app.models.Type;

import java.math.BigDecimal;

public record TransactionCreateDTO (
        @NotBlank
        String senderId,
    @NotBlank
     String receiverId,
    @NotNull
     BigDecimal amount,
    @NotNull
     Type sendFrom){}

