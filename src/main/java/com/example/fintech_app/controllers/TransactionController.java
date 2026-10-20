package com.example.fintech_app.controllers;

import com.example.fintech_app.DTO.TransactionCreateDTO;
import com.example.fintech_app.DTO.TransactionResponseDTO;
import com.example.fintech_app.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService service;
    @PostMapping("/deposit")
    public TransactionResponseDTO deposit (@RequestBody TransactionCreateDTO dto){
        return service.deposit(dto.receiverId(),dto.amount());
    }
    @PostMapping("/transfer")
    public TransactionResponseDTO transfer(@RequestBody TransactionCreateDTO dto){
        return service.transfer(dto);
    }
}
