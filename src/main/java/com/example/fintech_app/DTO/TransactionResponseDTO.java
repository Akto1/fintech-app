package com.example.fintech_app.DTO;

import com.example.fintech_app.models.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long senderId,
        Long receiverId,
        BigDecimal amount,
        Type type,
        LocalDateTime timestamp){}

