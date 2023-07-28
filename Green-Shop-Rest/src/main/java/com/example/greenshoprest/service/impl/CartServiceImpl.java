package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.dto.cartDto.CreateCartRequestDto;
import com.example.greenshopcommon.dto.cartDto.UpdateCartRequestDto;
import com.example.greenshopcommon.entity.Cart;
import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.exception.EntityNotFoundException;
import com.example.greenshopcommon.exception.IllegalArgumentExceptionError;
import com.example.greenshopcommon.exception.InternalServerError;
import com.example.greenshopcommon.mapper.CartMapper;
import com.example.greenshopcommon.mapper.ProductMapper;
import com.example.greenshopcommon.repository.CartRepository;
import com.example.greenshoprest.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    //    Get a list of carts for the specified user.
    @Override
    public ResponseEntity<List<CartDto>> findCartsByUser(User user) {
        log.info("Finding carts for user: {}", user.getName());
        List<Cart> carts = (user.getRole() == Role.ADMIN) ? cartRepository.findAll() : cartRepository.findAllByUserId(user.getId());
        List<CartDto> cartDtos = new ArrayList<>();
        for (Cart cart : carts) {
            cartDtos.add(cartMapper.mapToDto(cart));
        }
        log.debug("Found {} carts for user: {}", cartDtos.size(), user.getName());
        return ResponseEntity.ok(cartDtos);
    }

    //    Get a cart by its ID.
    @Override
    public ResponseEntity<CartDto> findById(int id) throws EntityNotFoundException {
        if (id <= 0) {
            throw new InternalServerError("The id can not be 0 or less than 0 : " + id);
        }
        log.info("Finding cart with ID: {}", id);
        Optional<Cart> byId = cartRepository.findById(id);
        if (byId.isEmpty()) {
            throw new EntityNotFoundException("cart with " + id + " id does not exists");
        }
        return byId.map(cart -> {
                    log.debug("Found cart with ID: {}", id);
                    return ResponseEntity.ok(cartMapper.mapToDto(cart));
                })
                .orElseGet(() -> {
                    log.debug("No cart found with ID: {}", id);
                    throw new EntityNotFoundException("cart with ID " + id + " does not exist.");
                });
    }

    //    Add a new cart for the specified user.
    @Override
    public ResponseEntity<CartDto> addCart(User user, CreateCartRequestDto createCartRequestDto) throws IllegalArgumentExceptionError {
        if (createCartRequestDto == null && user == null) {
            throw new IllegalArgumentExceptionError("CreateCartRequestDto must not be null.");
        }
        log.info("Adding cart for user: {}", user.getName());
        Cart cart = cartMapper.map(createCartRequestDto);
        cart.setUser(user);
        cart.setProduct(productMapper.dtoToMap(createCartRequestDto.getProductDto()));
        cartRepository.save(cart);
        log.debug("Cart added with ID: {}", cart.getId());
        return ResponseEntity.ok(cartMapper.mapToDto(cart));
    }

    //  Delete a cart by its ID.
    @Override
    public ResponseEntity<?> deleteById(int id) {
        if (id <= 0) {
            throw new InternalServerError("The id cannot be 0 or less than 0: " + id);
        }
        log.info("Deleting cart with ID: {}", id);
        return cartRepository.findById(id)
                .map(cart -> {
                    cartRepository.deleteById(id);
                    log.info("Cart with ID {} deleted successfully.", id);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> {
                    log.info("No cart found with ID: {}", id);
                    throw new EntityNotFoundException("Cart with ID " + id + " does not exist.");
                });
    }

    //  Update a cart with the specified ID for the given user.
    @Override
    public ResponseEntity<CartDto> updateCart(int id, User user, UpdateCartRequestDto updateCartRequestDto) {
        log.info("Updating cart with ID: {}", id);
        if (updateCartRequestDto == null) {
            throw new IllegalArgumentException("UpdateCartRequestDto must not be null.");
        }
        return cartRepository.findById(id)
                .map(cart -> {
                    cart.setUser(user);
                    cart.setProduct(productMapper.dtoToMap(updateCartRequestDto.getProductDto()));
                    cart.setQuantity(updateCartRequestDto.getQuantity());
                    cartRepository.save(cart);
                    log.debug("Cart with ID {} updated successfully.", id);
                    return ResponseEntity.ok(cartMapper.mapToDto(cart));
                })
                .orElseThrow(() -> {
                    log.debug("No cart found with ID: {}", id);
                    throw new EntityNotFoundException("Cart with ID " + id + " does not exist.");
                });
    }

}
