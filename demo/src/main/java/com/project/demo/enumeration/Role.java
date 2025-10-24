package com.project.demo.enumeration;

public enum Role {
	// ===== Frontend =====
    GUEST,       // Guest, not logged in, can only browse products and information, cannot shop or view personal information
    CUSTOMER,    // Customer, registered user, can shop, place orders, manage their own orders and personal information
    SELLER,      // Seller, certified user, can manage products, orders, and inventory
    
    // ===== TODO: Backend =====
    USER,        // User, Seller
    ADMIN,       // System administrator
    SUPER_ADMIN, // Super administrator // Can see soft deleted accounts
}
