package com.example.fintech_app.Repositories;

import com.example.fintech_app.models.TransactionsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsHRepository extends JpaRepository<TransactionsHistory,Long> {
    List<TransactionsHistory> findBySenderPhoneNumberOrReceiverPhoneNumber(String senderPhoneNumber, String receiverPhoneNumber);

}
