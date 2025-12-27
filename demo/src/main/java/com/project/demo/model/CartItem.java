package com.project.demo.model;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@ToString(exclude = {"cart", "product"})
@EqualsAndHashCode(of = "uuid")
public class CartItem implements Sellable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    // ===== Unique Identifier =====
    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private UUID uuid;

    // ===== Relation =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_uuid", referencedColumnName = "uuid", nullable = false)
    private Product product;

    // ===== Basic Information =====
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }
}
