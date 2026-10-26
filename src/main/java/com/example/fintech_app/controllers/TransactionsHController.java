package com.example.fintech_app.controllers;

import com.example.fintech_app.models.TransactionsHistory;
import com.example.fintech_app.services.TransactionsHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionsHController {
    private final TransactionsHistoryService service;

    @GetMapping("/history")
    public List<TransactionsHistory>getMyHistory(Principal principal){
        return service.getUserHistory(principal.getName());
    }
}
