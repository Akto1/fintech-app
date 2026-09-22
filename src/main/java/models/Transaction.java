package models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private User sender;

    @Column(nullable = false,unique = true)
    private User receiver;

    @Column(nullable = false,unique = true)
    private BigDecimal amount;

    @Column(nullable = false,unique = true)
    private Type type;

    @Column(nullable = false,unique = true)
    private LocalDateTime timestamp = LocalDateTime.from(Instant.now());
}
