package com.example.fintech_app.controllers;

import com.example.fintech_app.DTO.DepositReplenishment;
import com.example.fintech_app.DTO.TransactionResponseDTO;
import com.example.fintech_app.DTO.Transfer;
import com.example.fintech_app.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService service;
    @PostMapping("/deposit")
    public TransactionResponseDTO deposit (@Valid  @RequestBody DepositReplenishment dto, Principal principal){
        String currentUserEmail = principal.getName();
        return service.deposit(currentUserEmail,dto.amount());
    }
    @PostMapping("/transfer")
    public TransactionResponseDTO transfer(@Valid @RequestBody Transfer dto, Principal principal, @RequestHeader(value = "Idempotency-Key",required = false) String idempotencyKey){
        String senderEmail = principal.getName();
        return service.transfer(senderEmail,dto,idempotencyKey);
    }
}
