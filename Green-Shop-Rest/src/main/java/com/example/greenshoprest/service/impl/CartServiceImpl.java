package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.dto.cartDto.CreateCartRequestDto;
import com.example.greenshopcommon.dto.cartDto.UpdateCartRequestDto;
import com.example.greenshopcommon.entity.Cart;
import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.mapper.CartMapper;
import com.example.greenshopcommon.mapper.ProductMapper;
import com.example.greenshopcommon.repository.CartRepository;
import com.example.greenshoprest.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Override
    public ResponseEntity<List<CartDto>> findCartsByUser(User user) {
        List<Cart> carts = (user.getRole() == Role.ADMIN) ? cartRepository.findAll() : cartRepository.findAllByUserId(user.getId());
        List<CartDto> cartDtos = new ArrayList<>();
        for (Cart cart : carts) {
            cartDtos.add(cartMapper.mapToDto(cart));
        }
        return ResponseEntity.ok(cartDtos);
    }

    @Override
    public ResponseEntity<CartDto> findById(int id) {
        Optional<Cart> byId = cartRepository.findById(id);
        return byId.map(cart -> ResponseEntity.ok(cartMapper.mapToDto(cart)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<CartDto> addCart(User user, CreateCartRequestDto createCartRequestDto) {
        Cart cart = cartMapper.map(createCartRequestDto);
        cart.setUser(user);
        cart.setProduct(productMapper.dtoToMap(createCartRequestDto.getProductDto()));
        cartRepository.save(cart);
        return ResponseEntity.ok(cartMapper.mapToDto(cart));
    }

    @Override
    public ResponseEntity<?> deleteById(int id) {
        return cartRepository.findById(id)
                .map(cart -> {
                    cartRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<CartDto> updateCart(int id, User user, UpdateCartRequestDto updateCartRequestDto) {
        return cartRepository.findById(id)
                .map(cart -> {
                    cart.setUser(user);
                    cart.setProduct(productMapper.dtoToMap(updateCartRequestDto.getProductDto()));
                    cart.setQuantity(updateCartRequestDto.getQuantity());
                    cartRepository.save(cart);
                    return ResponseEntity.ok(cartMapper.mapToDto(cart));
                })
                .orElse(ResponseEntity.noContent().build());
    }

}
