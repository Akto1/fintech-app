package com.example.fintech_app.DTO;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record Transfer(
        @NotBlank(message = "Номер телефона получателя обязательно!")
        String receiverPhoneNumber,
        @Positive(message = "Сумма перевода должна быть больше 100 тенге")
        @DecimalMin(value = "100.00",message = "Минимальная сумма перевода 100 Тенге")
        BigDecimal amount ){
}
