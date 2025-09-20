package com.project.demo.model;

import java.util.UUID;

import com.project.demo.enumeration.OrderStatus;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.HashSet;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_uuid", columnList = "uuid")
})
@Getter
@Setter
@ToString
public class Order {

    // ===== 主鍵 =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    // ===== 基本資訊 =====
    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    // ===== 關聯 =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> items = new HashSet<>();

    // ===== 收件人資訊 =====
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_phone_number", nullable = false, length = 20)
    private String recipientPhoneNumber;

    @Column(name = "recipient_address", nullable = false, length = 255)
    private String recipientAddress;

    // ===== 其他資訊 =====
    @Column(name = "order_notes")
    private String orderNotes;

    // ===== 審計欄位 =====
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
