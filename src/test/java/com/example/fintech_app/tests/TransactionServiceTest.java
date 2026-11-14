package com.example.fintech_app.tests;


import com.example.fintech_app.DTO.TransactionResponseDTO;
import com.example.fintech_app.DTO.Transfer;
import com.example.fintech_app.Exceptions.UserNotFoundException;
import com.example.fintech_app.Repositories.TransactionRepository;
import com.example.fintech_app.Repositories.TransactionsHRepository;
import com.example.fintech_app.Repositories.UserRepository;
import com.example.fintech_app.models.Transaction;
import com.example.fintech_app.models.TransactionsHistory;
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
    private TransactionsHRepository transactionsHRepository;

    @InjectMocks
    private TransactionService service;

    @Test
    @DisplayName("Успешный перевод")
    void transfer_Success() {

        User sender = new User();
        sender.setPhoneNumber("7 700 746 7794");
        sender.setId(1L);
        sender.setBalance(new BigDecimal("1000"));

        User receiver = new User();
        receiver.setPhoneNumber("7 754 747 3737");
        receiver.setId(2L);
        receiver.setBalance(new BigDecimal("600"));

        Transfer dto = new Transfer(receiver.getPhoneNumber(), new BigDecimal("200"));

        String senderEmail = "test@mail.com";

        when(userRepository.findByEmail(senderEmail)).thenReturn(Optional.of(sender));
        when(userRepository.findByPhoneNumber("7 754 747 3737")).thenReturn(Optional.of(receiver));
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
        sender.setPhoneNumber("7 700 744 4443");
        sender.setBalance(new BigDecimal("400"));

        User receiver = new User();
        receiver.setPhoneNumber("7 700 746 7794");
        Transfer dto = new Transfer(receiver.getPhoneNumber(), new BigDecimal("600"));
        String senderEmail = "test@mail.com";

        when(userRepository.findByEmail(senderEmail)).thenReturn(Optional.of(sender));


        assertThrows(UserNotFoundException.class, () -> service.transfer(senderEmail, dto));

        assertEquals(new BigDecimal("400"), sender.getBalance());


        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Пользователь не найден при переводе")
    void transfer_UserNotFound() {
        String senderEmail = "notfound@mail.com";
        Transfer dto = new Transfer("7 7700 746 7794", new BigDecimal("100"));

        when(userRepository.findByEmail(senderEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.transfer(senderEmail, dto));

        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}