package com.project.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.demo.model.Order;
import com.project.demo.enumeration.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserUuid(UUID userUuid, Pageable pageable);

    Optional<Order> findByUuid(UUID uuid);

    List<Order> findByStatusAndExpiredAtLessThan(OrderStatus status, LocalDateTime now);
}
