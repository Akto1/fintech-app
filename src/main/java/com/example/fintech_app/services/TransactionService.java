package com.example.fintech_app.services;

import com.example.fintech_app.DTO.TransactionResponseDTO;
import com.example.fintech_app.DTO.Transfer;
import com.example.fintech_app.Exceptions.InsufficientFundsException;
import com.example.fintech_app.Exceptions.UserNotFoundException;
import com.example.fintech_app.Repositories.IdempotencyRepository;
import com.example.fintech_app.Repositories.TransactionRepository;
import com.example.fintech_app.Repositories.TransactionsHRepository;
import com.example.fintech_app.Repositories.UserRepository;
import com.example.fintech_app.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionsHRepository transactionsHRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper mapper;
    @Transactional
    public TransactionResponseDTO deposit(String userEmail,BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть больше нуля");
        }

        User userRef = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        User user = userRepository.findByIdForUpdate(userRef.getId())
                        .orElseThrow(()-> new UserNotFoundException("Счёт не найден"));
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setSender(user);
        transaction.setReceiver(user);
        transaction.setAmount(amount);
        transaction.setType(Type.DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());

        TransactionsHistory history = new TransactionsHistory();
        history.setSenderPhoneNumber(transaction.getSender().getPhoneNumber());
        history.setReceiverPhoneNumber(transaction.getReceiver().getPhoneNumber());
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
    public TransactionResponseDTO transfer(String senderEmail, Transfer dto,String idempotencyKey) {
        if(idempotencyKey!=null && !idempotencyKey.isEmpty()){
            Optional<IdempotencyRecord> existingRecord = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
            if (existingRecord.isPresent()){
                IdempotencyRecord record = existingRecord.get();


            if(record.getStatus() == IdempotencyStatus.SUCCESS){
                try {
                    return mapper.readValue(record.getResponseBody(),TransactionResponseDTO.class);
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка при чтении сохраненного объекта",e);
                }
            }
            else if (record.getStatus() == IdempotencyStatus.IN_PROGRESS){
                throw new IllegalStateException("Запрос с таким ключом уже обрабатывается. Подождите ");
            }
        }
        else{
            IdempotencyRecord newRecord = new IdempotencyRecord();
            newRecord.setIdempotencyKey(idempotencyKey);
            newRecord.setStatus(IdempotencyStatus.IN_PROGRESS);
            idempotencyRepository.save(newRecord);
        }
        }



        if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть больше нуля");
        }


        User senderRef = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException("Отправитель не найден"));

        User receiverPhone = userRepository.findByPhoneNumber(dto.receiverPhoneNumber())
                .orElseThrow(()-> new UserNotFoundException("Получатель с таким номером не зарегистрирован"));

        if(senderRef.getId().equals(receiverPhone.getId())){
            throw new IllegalArgumentException("Нельзя перевести деньги самому себе");
        }

        Long firstId = Math.min(senderRef.getId(), receiverPhone.getId());
        Long secondId = Math.max(senderRef.getId(), receiverPhone.getId());

        User first = userRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new UserNotFoundException("Один из счетов не найден"));
        User second = userRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new UserNotFoundException("Один из счетов не найден"));

        User sender = senderRef.getId().equals(firstId) ? first : second;
        User receiver = sender.getId().equals(firstId) ? second : first;

        if (sender.getBalance().compareTo(dto.amount()) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на балансе");
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
        history.setSenderPhoneNumber(transaction.getSender().getPhoneNumber());
        history.setReceiverPhoneNumber(transaction.getReceiver().getPhoneNumber());
        history.setType(Type.TRANSFER);
        history.setDateTime(LocalDateTime.now());
        history.setAmount(dto.amount());

        transactionsHRepository.save(history);

        Transaction saved = transactionRepository.save(transaction);

        if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
            IdempotencyRecord record = idempotencyRepository.findByIdempotencyKey(idempotencyKey).orElseThrow();


            TransactionResponseDTO responseDTO = new TransactionResponseDTO(
                    saved.getSender().getId(),
                    saved.getReceiver().getId(),
                    saved.getAmount(),
                    saved.getType(),
                    saved.getTimestamp());

            try {
                String jsonResponse = mapper.writeValueAsString(responseDTO);
                record.setResponseBody(jsonResponse);
                record.setStatus(IdempotencyStatus.SUCCESS);
                idempotencyRepository.save(record);
                return responseDTO;

            } catch (JsonProcessingException e) {
                throw new RuntimeException("Ошибка при сериализации ответа ",e);
            }
        }
        return new TransactionResponseDTO(
                saved.getSender().getId(),
                saved.getReceiver().getId(),
                saved.getAmount(),
                saved.getType(),
                saved.getTimestamp()
        );

    }
}