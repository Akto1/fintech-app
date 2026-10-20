package com.example.fintech_app.tests;


import com.example.fintech_app.DTO.TransactionCreateDTO;
import com.example.fintech_app.DTO.TransactionResponseDTO;
import com.example.fintech_app.DTO.UserCreateDTO;
import com.example.fintech_app.Repositories.TransactionRepository;
import com.example.fintech_app.Repositories.UserRepository;
import com.example.fintech_app.models.Transaction;
import com.example.fintech_app.models.Type;
import com.example.fintech_app.models.User;
import com.example.fintech_app.services.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionService service;

    @Test
    @DisplayName("Успешный тест")
    public void success(){
        User sender = new User();
        sender.setId(1L);
        sender.setBalance(new BigDecimal(1000));

        User receiver = new User();
        receiver.setId(2L);
        receiver.setBalance(new BigDecimal(600));


        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation->invocation.getArgument(0));
        TransactionCreateDTO dto = new TransactionCreateDTO(1L,2L,new BigDecimal(200), Type.TRANSFER);
        TransactionResponseDTO result = service.transfer(dto);

        assertEquals(new BigDecimal("800"),sender.getBalance());
        assertEquals(new BigDecimal("800"),receiver.getBalance());

        System.out.println("Tест отработал");
    }

    @Test
    @DisplayName("Недостаточно средств")
    public void notEnoughMoney(){
        User sender = new User();
        sender.setId(1L);
        sender.setBalance(new BigDecimal(400));

        User receiver = new User();
        receiver.setId(2L);
        receiver.setBalance(new BigDecimal(600));

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        TransactionCreateDTO dto = new TransactionCreateDTO(1L,2L,new BigDecimal(600),Type.TRANSFER);
        assertThrows(RuntimeException.class,()-> {
            service.transfer(dto);
        });
        verify(transactionRepository,never()).save(any(Transaction.class));

        System.err.println("Недостаточно средств");
    }


    @Test
    @DisplayName("Существуют юзер")
    public void notExistsUser(){
        User sender = new User();
        sender.setId(1L);
        sender.setBalance(new BigDecimal(1000));
        User receiver = new User();
        receiver.setId(2L);
        receiver.setBalance(new BigDecimal(600));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        TransactionCreateDTO dto = new TransactionCreateDTO(1L,2L,new BigDecimal(400),Type.TRANSFER);
        assertThrows(EntityNotFoundException.class,()-> {
            service.transfer(dto);
        });
        verify(transactionRepository,never()).save(any(Transaction.class));

        System.out.println("Юзера не существует");
    }

    @Test
    @DisplayName("Проверка Депозита")
    public void isDepositIncreasesTheScore(){
        User user = new User();
        user.setId(1L);
        user.setBalance(new BigDecimal(100));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        TransactionResponseDTO dto = service.deposit(1L,new BigDecimal(500));
        assertEquals(new BigDecimal(600),user.getBalance());
        verify(transactionRepository,times(1)).save(any(Transaction.class));
        System.out.println("Депозит пополнился");
    }

    @Test
    @DisplayName("Проверка юзера для депозита")
    public void isUserExistForDeposit(){
        User user = new User();
        user.setId(1L);
        user.setBalance(new BigDecimal(100));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,()->{
            service.deposit(1L,new BigDecimal(500));
                });
        verify(transactionRepository,never()).save(any(Transaction.class));
        System.out.println("Юзера для пополнения баланса не существует");
    }
}