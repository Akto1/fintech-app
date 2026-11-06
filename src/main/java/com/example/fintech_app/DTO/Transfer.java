package com.example.fintech_app.DTO;

import com.example.fintech_app.models.Type;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record Transfer(
        @NotNull(message = "Id получателя обязательно!")
        Long receiverId,
        @Positive(message = "Сумма перевода должна быть больше 100 тенге")
        @DecimalMin(value = "100.00",message = "Минимальная сумма перевода 100 Тенге")
        BigDecimal amount,
        @NotBlank(message = "Укажите с как")
        Type type) {
}
