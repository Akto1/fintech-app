package com.example.fintech_app.services;

import com.example.fintech_app.Repositories.TransactionsHRepository;
import com.example.fintech_app.Repositories.UserRepository;
import com.example.fintech_app.models.TransactionsHistory;
import com.example.fintech_app.models.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor

public class TransactionsHistoryService {
    private final TransactionsHRepository repository;
    private final UserRepository userRepository;

    public List<TransactionsHistory> getUserHistory(String email){
        User user = userRepository.findByEmail(email).
                orElseThrow(()-> new RuntimeException("User not found"));
        return repository.findBySenderIdOrReceiverId(user.getId(),user.getId());
    }
}
