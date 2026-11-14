package com.example.fintech_app.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long TransactionId;

    @Column(nullable = false)
    private String senderPhoneNumber;

    @Column(nullable = false)
    private String receiverPhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private BigDecimal amount;
}