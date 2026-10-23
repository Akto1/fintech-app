package com.example.fintech_app.controllers;

import com.example.fintech_app.DTO.TransactionCreateDTO;
import com.example.fintech_app.DTO.TransactionResponseDTO;
import com.example.fintech_app.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService service;
    @PostMapping("/deposit")
    public TransactionResponseDTO deposit (@RequestBody TransactionCreateDTO dto,Principal principal){
        String currentUserEmail = principal.getName();
        return service.deposit(currentUserEmail,dto.amount());
    }
    @PostMapping("/transfer")
    public TransactionResponseDTO transfer(@RequestBody TransactionCreateDTO dto,Principal principal){
        String senderEmail = principal.getName();
        return service.transfer(senderEmail,dto);
    }
}
