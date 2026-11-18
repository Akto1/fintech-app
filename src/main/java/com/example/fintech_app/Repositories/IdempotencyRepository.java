package com.example.fintech_app.Repositories;

import com.example.fintech_app.models.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord,Long> {
    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);
    boolean existsByIdempotencyKey(String idempotencyKey);
}
