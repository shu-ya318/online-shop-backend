package com.project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.demo.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Use JPA auto-generated methods
}
