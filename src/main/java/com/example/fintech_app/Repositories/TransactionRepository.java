package com.example.fintech_app.Repositories;

import com.example.fintech_app.models.Transaction;
import com.example.fintech_app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findBySenderOrReceiver(User sender,User receiver);
}
