package com.example.fintech_app.Repositories;

import com.example.fintech_app.models.Transaction;
import com.example.fintech_app.models.TransactionsHistory;
import com.example.fintech_app.models.Type;
import com.example.fintech_app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsHRepository extends JpaRepository<TransactionsHistory,Long> {
     List<TransactionsHistory> findByType(Type type);
    List<TransactionsHistory> findBySenderIdOrReceiverId(Long senderId, Long receiverId);

}
