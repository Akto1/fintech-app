package com.example.fintech_app.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "idempotency_record")
@NoArgsConstructor
@Getter
@Setter
public class IdempotencyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "idempotency_Key", nullable = false,unique = true)
    private String idempotencyKey;


    @Column(name = "response_Body",columnDefinition = "TEXT")
    private String responseBody;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false,unique = false)
    private IdempotencyStatus status;
}
