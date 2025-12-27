package com.project.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.demo.repository.CartRepository;
import com.project.demo.repository.ProductRepository;
import com.project.demo.repository.UserRepository;
import com.project.demo.repository.CartItemRepository;
import com.project.demo.mapper.CartMapper;
import com.project.demo.model.Cart;
import com.project.demo.model.CartItem;
import com.project.demo.model.Product;
import com.project.demo.model.User;
import com.project.demo.dto.cart.CartResponseDTO;
import com.project.demo.dto.cart.CartItemAddRequestDTO;
import com.project.demo.exception.EntityNotFoundException;
import com.project.demo.exception.InsufficientStockException;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final ProductService productService;

    // --------- Cart ---------

    // Add item to cart
    @Transactional
    public CartResponseDTO addItemToCart(UUID userUuid, CartItemAddRequestDTO dto) {
        Cart cart = getOrCreateCartByUserUuid(userUuid);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getUuid().equals(dto.productUuid()))
                .findFirst()
                .orElse(null);

        int newQuantity = (cartItem != null) ? cartItem.getQuantity() + dto.quantity() : dto.quantity();

        Product product = productService.checkAndGetProduct(dto.productUuid(), newQuantity);

        if (cartItem != null) {
            cartItem.setQuantity(newQuantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(newQuantity);
            cart.getItems().add(cartItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        return cartMapper.toCartResponseDTO(cart);
    }

    // Get cart by user uuid
    public CartResponseDTO getCartByUserUuid(UUID userUuid) {
        Cart cart = getOrCreateCartByUserUuid(userUuid);

        return cartMapper.toCartResponseDTO(cart);
    }

    // --------- CartItem ---------

    // Update cart item quantity
    @Transactional
    public CartResponseDTO updateCartItemQty(UUID userUuid, UUID cartItemUuid,
            Integer quantity) {
        Cart cart = getExistingCartByUserUuid(userUuid);

        CartItem cartItem = getCartItemByUuid(cart, cartItemUuid);

        productService.checkAndGetProduct(cartItem.getProduct().getUuid(), quantity);

        cartItem.setQuantity(quantity);

        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        return cartMapper.toCartResponseDTO(cart);
    }

    // Remove item from cart
    @Transactional
    public CartResponseDTO removeItemFromCart(UUID userUuid, UUID cartItemUuid) {
        Cart cart = getExistingCartByUserUuid(userUuid);

        CartItem cartItem = getCartItemByUuid(cart, cartItemUuid);

        cartItemRepository.delete(cartItem);
        cart.getItems().remove(cartItem);
        
        cart.setUpdatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toCartResponseDTO(savedCart);
    }

    // Clear cart
    @Transactional
    public void clearCart(UUID userUuid) {
        Cart cart = getExistingCartByUserUuid(userUuid);

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);
    }

	// ----- Private Helper Method -----

    // Get or create cart by user uuid
    private Cart getOrCreateCartByUserUuid(UUID userUuid) {
        return cartRepository.findByUserUuid(userUuid)
                .orElseGet(() -> createCartForUser(userUuid));
    }

    // Create cart for user
    private Cart createCartForUser(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: " + userUuid));

        Cart cart = new Cart();

        cart.setUser(user);
        cart.setUuid(UUID.randomUUID());
        cart.setCreatedAt(LocalDateTime.now());

        return cartRepository.save(cart);
    }

    // Get existing cart by user uuid
    private Cart getExistingCartByUserUuid(UUID userUuid) {
        return cartRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user with uuid: " + userUuid));
    }

    // Get cart item by uuid
    private CartItem getCartItemByUuid(Cart cart, UUID cartItemUuid) {
        return cart.getItems().stream()
                .filter(item -> item.getUuid().equals(cartItemUuid))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found in cart with uuid: " + cartItemUuid));
    }

}
