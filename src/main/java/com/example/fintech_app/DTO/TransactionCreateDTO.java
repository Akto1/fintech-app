package com.example.fintech_app.DTO;


import jakarta.validation.constraints.NotNull;
import com.example.fintech_app.models.Type;

import java.math.BigDecimal;

public record TransactionCreateDTO (
        @NotNull
        Long senderId,
     @NotNull
     Long receiverId,
    @NotNull
     BigDecimal amount,
    @NotNull
     Type sendFrom){}

