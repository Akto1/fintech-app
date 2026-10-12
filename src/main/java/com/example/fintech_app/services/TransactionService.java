package com.example.fintech_app.services;

import com.example.fintech_app.DTO.TransactionCreateDTO;
import com.example.fintech_app.DTO.TransactionResponseDTO;
import com.example.fintech_app.Repositories.TransactionRepository;
import com.example.fintech_app.Repositories.UserRepository;
import com.example.fintech_app.models.Transaction;
import com.example.fintech_app.models.Type;
import com.example.fintech_app.models.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponseDTO deposit(Long receiverId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть больше нуля");
        }

        User user = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setSender((user));
        transaction.setReceiver((user));
        transaction.setAmount(amount);
        transaction.setType(Type.DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);

        return new TransactionResponseDTO(
                saved.getSender().getId(),
                saved.getReceiver().getId(),
                saved.getAmount(),
                saved.getType(),
                saved.getTimestamp()
        );
    }

    @Transactional
    public TransactionResponseDTO transfer(TransactionCreateDTO dto) {
        if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть больше нуля");
        }

        if (dto.senderId().equals(dto.receiverId())) {
            throw new IllegalArgumentException("Нельзя перевести деньги самому себе");
        }

        User sender = userRepository.findById(dto.senderId())
                .orElseThrow(() -> new EntityNotFoundException("Отправитель не найден"));

        User receiver = userRepository.findById(dto.receiverId())
                .orElseThrow(() -> new EntityNotFoundException("Получатель не найден"));



        if (sender.getBalance().compareTo(dto.amount()) < 0) {
            throw new IllegalStateException("Недостаточно средств на балансе");
        }

        sender.setBalance(sender.getBalance().subtract(dto.amount()));
        receiver.setBalance(receiver.getBalance().add(dto.amount()));

        userRepository.save(sender);
        userRepository.save(receiver);

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(dto.amount());
        transaction.setType(Type.TRANSFER);
        transaction.setTimestamp(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);

        return new TransactionResponseDTO(
                saved.getSender().getId(),
                saved.getReceiver().getId(),
                saved.getAmount(),
                saved.getType(),
                saved.getTimestamp()
        );
    }
}
