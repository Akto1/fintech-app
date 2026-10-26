package com.example.fintech_app.services;

import com.example.fintech_app.DTO.TransactionCreateDTO;
import com.example.fintech_app.DTO.TransactionResponseDTO;
import com.example.fintech_app.Repositories.TransactionRepository;
import com.example.fintech_app.Repositories.TransactionsHRepository;
import com.example.fintech_app.Repositories.UserRepository;
import com.example.fintech_app.models.Transaction;
import com.example.fintech_app.models.TransactionsHistory;
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
    private final TransactionsHRepository transactionsHRepository;

    @Transactional
    public TransactionResponseDTO deposit(String userEmail, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть больше нуля");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setSender(user);
        transaction.setReceiver(user);
        transaction.setAmount(amount);
        transaction.setType(Type.DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());

        TransactionsHistory history = new TransactionsHistory();
        history.setSenderId(transaction.getSender().getId());
        history.setReceiverId(transaction.getReceiver().getId());
        history.setType(Type.DEPOSIT);
        history.setDateTime(LocalDateTime.now());
        history.setAmount(amount);

        transactionsHRepository.save(history);

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
    public TransactionResponseDTO transfer(String senderEmail, TransactionCreateDTO dto) {
        if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть больше нуля");
        }

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new EntityNotFoundException("Отправитель не найден"));

        User receiver = userRepository.findById(dto.receiverId())
                .orElseThrow(() -> new EntityNotFoundException("Получатель не найден"));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Нельзя перевести деньги самому себе");
        }

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

        TransactionsHistory history = new TransactionsHistory();
        history.setSenderId(sender.getId());
        history.setReceiverId(receiver.getId());
        history.setType(Type.TRANSFER);
        history.setDateTime(LocalDateTime.now());
        history.setAmount(dto.amount());

        transactionsHRepository.save(history);

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