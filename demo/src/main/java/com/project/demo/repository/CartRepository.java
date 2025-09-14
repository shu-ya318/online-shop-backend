package com.project.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.demo.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items ci LEFT JOIN FETCH ci.product WHERE c.user.uuid = :userUuid ORDER BY ci.id")
    Optional<Cart> findByUserUuid(@Param("userUuid") UUID userUuid);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items ci LEFT JOIN FETCH ci.product WHERE c.uuid = :uuid ORDER BY ci.id")
    Optional<Cart> findByUuid(@Param("uuid") UUID uuid);
}
