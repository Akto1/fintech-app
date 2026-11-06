package com.example.fintech_app.tests;


import com.example.fintech_app.DTO.TransactionResponseDTO;
import com.example.fintech_app.DTO.Transfer;
import com.example.fintech_app.Exceptions.InsufficientFundsException;
import com.example.fintech_app.Exceptions.UserNotFoundException;
import com.example.fintech_app.Repositories.TransactionRepository;
import com.example.fintech_app.Repositories.TransactionsHRepository;
import com.example.fintech_app.Repositories.UserRepository;
import com.example.fintech_app.models.Transaction;
import com.example.fintech_app.models.TransactionsHistory;
import com.example.fintech_app.models.Type;
import com.example.fintech_app.models.User;
import com.example.fintech_app.services.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    @Mock
    private TransactionsHRepository transactionsHRepository; // Не забыли!

    @InjectMocks
    private TransactionService service;

    @Test
    @DisplayName("Успешный перевод")
    void transfer_Success() {

        User sender = new User();
        sender.setId(1L);
        sender.setBalance(new BigDecimal("1000"));

        User receiver = new User();
        receiver.setId(2L);
        receiver.setBalance(new BigDecimal("600"));

        Transfer dto = new Transfer(2L, new BigDecimal("200"), Type.TRANSFER);

        String senderEmail = "test@mail.com";

        when(userRepository.findByEmail(senderEmail)).thenReturn(Optional.of(sender));

        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sender));

        when(userRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(receiver));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponseDTO result = service.transfer(senderEmail, dto);

        assertEquals(new BigDecimal("800"), sender.getBalance());
        assertEquals(new BigDecimal("800"), receiver.getBalance());

        verify(transactionsHRepository, times(1)).save(any(TransactionsHistory.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Недостаточно средств для перевода")
    void transfer_InsufficientFunds() {
        User sender = new User();
        sender.setId(1L);
        sender.setBalance(new BigDecimal("400"));

        User receiver = new User();
        receiver.setId(2L);

        Transfer dto = new Transfer(2L, new BigDecimal("600"),Type.TRANSFER);
        String senderEmail = "test@mail.com";

        when(userRepository.findByEmail(senderEmail)).thenReturn(Optional.of(sender));

        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sender));

        when(userRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(receiver));


        assertThrows(InsufficientFundsException.class, () -> service.transfer(senderEmail, dto));

        assertEquals(new BigDecimal("400"), sender.getBalance());


        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Пользователь не найден при переводе")
    void transfer_UserNotFound() {
        String senderEmail = "notfound@mail.com";
        Transfer dto = new Transfer(2L, new BigDecimal("100"),Type.TRANSFER);

        when(userRepository.findByEmail(senderEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.transfer(senderEmail, dto));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}