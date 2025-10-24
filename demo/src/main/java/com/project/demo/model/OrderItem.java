package com.project.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@ToString(exclude = { "order", "product" })
public class OrderItem implements Sellable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    // ===== Relation =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_uuid", referencedColumnName = "uuid", nullable = false)
    private Product product;

    // ===== Basic Information =====
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // ===== Product Info Snapshot =====
    @Column(name = "product_name", nullable = false, updatable = false)
    private String productName;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2, updatable = false)
    private BigDecimal unitPrice;

    @Column(name = "discount_percentage", precision = 5, scale = 2, updatable = false)
    private BigDecimal discountPercentage;

    @Column(name = "image_url", updatable = false)
    private String imageUrl;
}
