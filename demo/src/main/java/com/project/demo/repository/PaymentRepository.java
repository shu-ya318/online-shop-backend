package com.project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.demo.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderUuid(UUID orderUuid); // one to many relation

    Optional<Payment> findByUuid(UUID uuid);

    Optional<Payment> findByTransactionId(String transactionId);
}
