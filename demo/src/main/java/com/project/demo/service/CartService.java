package com.project.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.demo.repository.CartRepository;
import com.project.demo.repository.ProductRepository;
import com.project.demo.repository.UserRepository;
import com.project.demo.mapper.CartMapper;
import com.project.demo.model.Cart;
import com.project.demo.model.CartItem;
import com.project.demo.model.Product;
import com.project.demo.model.User;
import com.project.demo.dto.cart.CartResponseDTO;
import com.project.demo.dto.cart.CartAddItemRequestDTO;
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
    private final CartMapper cartMapper;

    // --------- Cart ---------

    // Add item to cart
    @Transactional
    public CartResponseDTO addItemToCart(UUID userUuid, CartAddItemRequestDTO dto) {
        Cart cart = cartRepository.findByUserUuid(userUuid)
                .orElseGet(() -> createCartForUser(userUuid));

        Product product = productRepository.findByUuid(dto.productUuid())
                .orElseThrow(
                        () -> {
                            return new EntityNotFoundException("Product not found with uuid: " + dto.productUuid());
                        });

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getUuid().equals(dto.productUuid()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();

                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);

                    cart.getItems().add(newItem);

                    return newItem;
                });

        int newQuantity = cartItem.getQuantity() + dto.quantity();
        if (newQuantity > product.getStock()) {
            throw new InsufficientStockException(product.getName(), product.getStock());
        }

        cartItem.setQuantity(newQuantity);

        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        CartResponseDTO responseDTO = cartMapper.toCartResponseDTO(cart);

        return responseDTO;
    }

    // Get cart by user uuid
    public CartResponseDTO getCartByUserUuid(UUID userUuid) {
        Cart cart = cartRepository.findByUserUuid(userUuid)
                .orElseGet(() -> createCartForUser(userUuid));

        CartResponseDTO responseDTO = cartMapper.toCartResponseDTO(cart);

        return responseDTO;
    }

    // --------- CartItem ---------

    // Update cart item quantity
    @Transactional
    public CartResponseDTO updateCartItemQuantity(UUID userUuid, UUID productUuid,
            Integer quantity) {
        Cart cart = getExistingCartByUserUuid(userUuid);

        CartItem cartItem = getCartItemByProductUuid(cart, productUuid);

        Product product = cartItem.getProduct();

        if (quantity > product.getStock()) {
            throw new InsufficientStockException(product.getName(), product.getStock());
        }

        cartItem.setQuantity(quantity);

        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        CartResponseDTO responseDTO = cartMapper.toCartResponseDTO(cart);

        return responseDTO;
    }

    // Remove item from cart
    @Transactional
    public CartResponseDTO removeItemFromCart(UUID userUuid, UUID productUuid) {
        Cart cart = getExistingCartByUserUuid(userUuid);

        CartItem cartItem = getCartItemByProductUuid(cart, productUuid);

        cart.getItems().remove(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        CartResponseDTO responseDTO = cartMapper.toCartResponseDTO(cart);

        return responseDTO;
    }

    // Clear cart
    @Transactional
    public void clearCart(UUID userUuid) {
        Cart cart = getExistingCartByUserUuid(userUuid);

        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);
    }

	// ----- Private Helper Method -----

    private Cart createCartForUser(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: " + userUuid));

        Cart cart = new Cart();

        cart.setUser(user);
        cart.setUuid(UUID.randomUUID());
        cart.setCreatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(cart);

        return savedCart;
    }

    private Cart getExistingCartByUserUuid(UUID userUuid) {
        Cart cart = cartRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user with uuid: " + userUuid));

        return cart;
    }

    private CartItem getCartItemByProductUuid(Cart cart, UUID productUuid) {
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getUuid().equals(productUuid))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Product not found in cart with uuid: " + productUuid));

        return cartItem;
    }
}
