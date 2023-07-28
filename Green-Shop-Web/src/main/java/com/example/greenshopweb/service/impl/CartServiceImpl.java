package com.example.greenshopweb.service.impl;

import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.entity.Cart;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.mapper.CartMapper;
import com.example.greenshopcommon.mapper.ProductMapper;
import com.example.greenshopcommon.mapper.UserMapper;
import com.example.greenshopcommon.repository.CartRepository;
import com.example.greenshopcommon.repository.ProductRepository;
import com.example.greenshopweb.security.CurrentUser;
import com.example.greenshopweb.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;


    // Method to find all carts based on the user's role
    @Override
    public List<Cart> findCarts(User user) {
        log.info("Finding carts for user: {}", user.getName());
        List<Cart> carts;
        try {
            if (user.getRole() == Role.ADMIN) {
                log.info("User is an admin. Fetching all carts.");
                carts = cartRepository.findAll();
            } else {
                log.info("User is not an admin. Fetching user-specific carts.");
                carts = cartRepository.findAllByUserId(user.getId());
            }
        } catch (Exception e) {
            log.error("Error while finding carts for user: {}", user.getName(), e);
            throw new RuntimeException("Failed to find carts for user.");
        }
        return carts;
    }


    // Method to find a cart by its ID
    @Override
    public Optional<Cart> findById(int id) {
        log.info("Finding cart by ID: {}", id);
        try {
            return cartRepository.findById(id);
        } catch (Exception e) {
            log.error("Error while finding cart by ID: {}", id, e);
            throw new RuntimeException("Failed to find cart by ID.");
        }
    }


    // Method to add a new cart
    @Override
    public void addCart(int productId, User user, int quantity) throws IOException {
        log.info("Adding a new cart for user: {}", user.getName());
        if (productId <= 0 || quantity <= 0) {
            throw new ValidationException("Invalid input data for adding cart.");
        }

        try {
            Optional<Product> byId = productRepository.findById(productId);
            if (byId.isPresent()) {
                Product product = byId.get();
                Cart cart = new Cart();
                cart.setUser(user);
                cart.setProduct(product);
                cart.setQuantity(quantity);
                cartRepository.save(cart);
                log.info("Cart added successfully for product ID: {}", productId);
            } else {
                log.warn("Failed to add cart. Product with ID {} not found.", productId);
                throw new ValidationException("Product with ID not found.");
            }
        } catch (Exception e) {
            log.error("Error while adding cart for user: {}", user.getName(), e);
            throw new RuntimeException("Failed to add cart for user.");
        }
    }


    // Method to delete a cart by its ID
    @Override
    public void deleteById(int id) {
        log.info("Deleting cart with ID: {}", id);
        if (id <= 0) {
            throw new ValidationException("Invalid cart ID for deletion.");
        }

        try {
            cartRepository.deleteById(id);
            log.info("Cart with ID {} deleted successfully.", id);
        } catch (Exception e) {
            log.error("Error while deleting cart with ID: {}", id, e);
            throw new RuntimeException("Failed to delete cart.");
        }
    }


    // Method to update an existing cart
    @Override
    public void updateCart(Cart cart) {
        log.info("Updating cart with ID: {}", cart.getId());
        try {
            cartRepository.save(cart);
            log.info("Cart with ID {} updated successfully.", cart.getId());
        } catch (Exception e) {
            log.error("Error while updating cart with ID: {}", cart.getId(), e);
            throw new RuntimeException("Failed to update cart.");
        }
    }


    // Method to find all carts along with their user and product information
    @Override
    public List<CartDto> findCartsByUser(User user) {
        log.info("Finding carts with details for user: {}", user.getName());
        List<Cart> carts;
        List<CartDto> cartDtos = new ArrayList<>();
        try {
            if (user.getRole() == Role.ADMIN) {
                log.info("User is an admin. Fetching all carts with details.");
                carts = cartRepository.findAll();
            } else {
                log.info("User is not an admin. Fetching user-specific carts with details.");
                carts = cartRepository.findAllByUserId(user.getId());
            }

            for (Cart cart : carts) {
                CartDto cartDto = cartMapper.mapToDto(cart);
                cartDto.setUserDto(userMapper.mapToDto(cart.getUser()));
                cartDto.setProductDto(productMapper.mapToDto(cart.getProduct()));
                cartDtos.add(cartDto);
            }
        } catch (Exception e) {
            log.error("Error while finding carts for user: {}", user.getName(), e);
            throw new RuntimeException("Failed to find carts for user.");
        }
        return cartDtos;
    }


    // Method to update the quantity of a cart by the current user
    @Override
    public void updateCartByCurrentUser(int userId, int newQuantity, int cartId) {
        log.info("Updating cart quantity for user ID: {}, cart ID: {}", userId, cartId);
        if (userId <= 0 || cartId <= 0 || newQuantity < 0) {
            throw new ValidationException("Invalid input data for updating cart quantity.");
        }
        try {
            Optional<Cart> optionalCart = cartRepository.findById(cartId);
            if (optionalCart.isPresent()) {
                Cart cart = optionalCart.get();
                cart.setQuantity(newQuantity);
                cartRepository.save(cart);
                log.info("Cart quantity updated successfully for cart ID: {}", cartId);
            } else {
                log.warn("Failed to update cart quantity. Cart with ID {} not found.", cartId);
                throw new ValidationException("Cart with ID not found.");
            }
        } catch (Exception e) {
            log.error("Error while updating cart quantity for user ID: {}, cart ID: {}", userId, cartId, e);
            throw new RuntimeException("Failed to update cart quantity.");
        }
    }


    // Method to calculate the total price of the current user's cart
    @Override
    public double calculateCurrentUserCartTotal(CurrentUser currentUser) {
        log.info("Calculating total cart price for current user: {}", currentUser.getUser().getName());
        double productPriceTotal = 0.0;
        try {
            List<Cart> carts = cartRepository.findAllByUserId(currentUser.getUser().getId());

            for (Cart cart : carts) {
                double productPrice = cart.getProduct().getPrice();
                int quantity = cart.getQuantity();
                double subtotal = productPrice * quantity;
                productPriceTotal += subtotal;
            }
            log.info("Total cart price calculated successfully for user: {}. Total: {}", currentUser.getUser().getName(), productPriceTotal);
        } catch (Exception e) {
            log.error("Error while calculating total cart price for user: {}", currentUser.getUser().getName(), e);
            throw new RuntimeException("Failed to calculate total cart price.");
        }
        return productPriceTotal;
    }
}