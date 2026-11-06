package com.example.fintech_app.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositReplenishment(
        @NotNull(message = "Сумма пополнения обязательна!")
        @Positive(message = "Сумма пополнения должна быть больше 0")
        BigDecimal amount) {}
