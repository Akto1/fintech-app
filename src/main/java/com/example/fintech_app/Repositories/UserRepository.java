package com.example.fintech_app.Repositories;

import com.example.fintech_app.models.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
Optional<User> findByUsername(String username);
Optional<User> findByEmail(String email);
Optional<User> findByPhoneNumber(String phoneNumber);
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select u from User u where u.id = :id")
Optional<User> findByIdForUpdate(Long id);

boolean existsByUsername(String username);
boolean existsByEmail(String email);
boolean existsByPhoneNumber(String phoneNumber);
}
